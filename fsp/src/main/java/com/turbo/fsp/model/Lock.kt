package com.turbo.fsp.model

import com.turbo.fsp.io.IOUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.nio.channels.FileLock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock

/**
 * @author leiiiooo
 * @date 2020/5/7
 */
class Lock constructor(private val lockFilePath: String) {
    companion object {
        private val LOCK_MAP = ConcurrentHashMap<String, ReentrantLock>()
    }

    private var reentrantLock: ReentrantLock
    private var fos: FileOutputStream? = null
    private var channel: FileChannel? = null
    private var fileLock: FileLock? = null

    init {
        reentrantLock = getLock(lockFilePath)
        val file = File(lockFilePath)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (exception: IOException) {
                //do nothing
            }
        }
    }

    private fun getLock(lockFilePath: String): ReentrantLock {
        return if (!LOCK_MAP.containsKey(lockFilePath)) {
            val reentrantLock = ReentrantLock()
            LOCK_MAP[lockFilePath] = reentrantLock
            reentrantLock
        } else {
            LOCK_MAP[lockFilePath]!!
        }
    }

    fun lock(): Lock {
        reentrantLock.lock()
        fos = FileOutputStream(lockFilePath)
        channel = fos?.channel
        fileLock = channel?.lock()
        return this
    }

    fun release() {
        fileLock?.also {
            try {
                it.release()
            } catch (exception: IOException) {
                //do nothing
            }
        }
        IOUtils.closeSilently(channel)
        IOUtils.closeSilently(fos)
        reentrantLock.unlock()
    }
}