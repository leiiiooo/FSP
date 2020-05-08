package com.turbo.fsp

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.turbo.fsp.Observer.DataChangeObserver
import com.turbo.fsp.`interface`.FSharedPreferences
import com.turbo.fsp.cache.FspCache
import com.turbo.fsp.editor.FspEditor
import com.turbo.fsp.io.FileUtils
import com.turbo.fsp.io.ReadWriteCore
import com.turbo.fsp.task.ReloadTask
import com.turbo.fsp.task.SyncTask
import java.io.File
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.locks.ReentrantReadWriteLock

/**
 * @author leiiiooo
 * @date 2020/5/7
 */
class FSP internal constructor(private val name: String) : FSharedPreferences {
    private var observer: DataChangeObserver
    private val editor = FspEditor()
    private val needSync = AtomicBoolean(false)
    private val syncing = AtomicBoolean(false)
    private val copyLock = ReentrantReadWriteLock()

    private val keyValueMap: MutableMap<String, Any> = ConcurrentHashMap()

    companion object {
        private var sContext: Context? = null
        private val sFspCache = FspCache()
        private val sSyncExecutor = Executors.newFixedThreadPool(4)

        fun init(context: Context?) {
            context ?: return
            sContext = context.applicationContext
        }

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun setMax(maxSize: Int) {
            sFspCache.resize(maxSize)
        }

        fun get(name: String): FSP? {
            return if (name.isBlank()) {
                null
            } else {
                synchronized(FSP::class) {
                    sFspCache[name]
                }
            }
        }
    }

    init {
        sContext ?: throw Exception(Consts.NO_CONTEXT_ERROR_TIP)
        reload()
        observer = DataChangeObserver(FileUtils.getFilePath(sContext!!, name))
        observer.setListener {
            onCloseWrite {
                onCloseWrite(it)
            }
            onDelete {
                onDelete(it)
            }
        }
        observer.startWatching()

        editor.setListener {
            onPut { s, obj ->
                s ?: return@onPut
                obj ?: return@onPut
                copyLock.readLock().lock()
                keyValueMap[s] = obj
                copyLock.readLock().unlock()
            }
            onApply {
                sync()
            }
            onClear {

            }
            onCommit {
                sync()
            }
            onRemove { }
        }
    }

    private fun sync() {
        needSync.compareAndSet(false, true)
        postSyncTask()
    }

    private fun postSyncTask() {
        if (syncing.get()) {
            return
        }

        val syncTask = SyncTask()
        syncTask.setListener {
            onRun {
                if (!needSync.get()) {
                    return@onRun
                }

                sContext ?: throw Exception(Consts.NO_CONTEXT_ERROR_TIP)
                syncing.compareAndSet(false, true)
                copyLock.writeLock().lock()
                val storeMap = HashMap(keyValueMap)
                copyLock.writeLock().unlock()
                needSync.compareAndSet(true, false)
                observer.stopWatching()
                val core = ReadWriteCore(sContext!!, name)
                core.write(storeMap)
                syncing.compareAndSet(true, false)
                //check whether need re sync
                if (needSync.get()) {
                    postSyncTask()
                } else {
                    observer.startWatching()
                }
            }
        }
        sSyncExecutor.execute(syncTask)
    }

    private fun onDelete(path: String?) {
        keyValueMap.clear()
    }

    private fun onCloseWrite(path: String?) {
        if (syncing.get()) {
            return
        }

        val reloadTask = ReloadTask()
        reloadTask.setListener {
            onRun {
                reload()
            }
        }
        sSyncExecutor.execute(reloadTask)
    }

    override fun getSerializable(key: String, defValue: Serializable?): Serializable? {
        if (keyValueMap.containsKey(key)) {
            return (keyValueMap[key]) as Serializable
        }

        return defValue
    }

    override fun contains(key: String?): Boolean {
        return keyValueMap.containsKey(key)
    }

    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        if (keyValueMap.containsKey(key)) {
            return keyValueMap[key] as Boolean
        }

        return defValue
    }

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        //do nothing
    }

    override fun getInt(key: String?, defValue: Int): Int {
        if (keyValueMap.containsKey(key)) {
            return keyValueMap[key] as Int
        }

        return defValue
    }

    override fun getAll(): MutableMap<String, *> {
        return keyValueMap
    }

    override fun edit(): SharedPreferences.Editor {
        return editor
    }

    override fun getLong(key: String?, defValue: Long): Long {
        if (keyValueMap.containsKey(key)) {
            return keyValueMap[key] as Long
        }

        return defValue
    }

    override fun getFloat(key: String?, defValue: Float): Float {
        if (keyValueMap.containsKey(key)) {
            return keyValueMap[key] as Float
        }

        return defValue
    }

    override fun getStringSet(key: String?, defValues: MutableSet<String>?): MutableSet<String>? {
        if (keyValueMap.containsKey(key)) {
            return keyValueMap[key] as MutableSet<String>
        }

        return defValues
    }

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener?) {
        //do nothing
    }

    override fun getString(key: String?, defValue: String?): String? {
        if (keyValueMap.containsKey(key)) {
            return keyValueMap[key] as String
        }
        return defValue
    }

    private fun reload() {
        sContext ?: throw Exception(Consts.NO_CONTEXT_ERROR_TIP)
        val loadedData = ReadWriteCore(sContext!!, name).read()
        keyValueMap.clear()
        loadedData?.also {
            this.keyValueMap.putAll(it as Map<out String, Any>)
        }
    }

    fun sizeOf(): Int {
        sContext ?: throw Exception(Consts.NO_CONTEXT_ERROR_TIP)
        val file = File(FileUtils.getFilePath(sContext!!, name))
        if (!file.exists()) {
            return 0
        }
        return (file.length() / 1024).toInt()
    }
}











