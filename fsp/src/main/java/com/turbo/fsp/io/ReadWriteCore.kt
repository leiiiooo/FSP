package com.turbo.fsp.io

import android.content.Context
import com.turbo.fsp.model.Lock
import java.io.*

/**
 * @author leiiiooo
 * @date 2020/5/7
 */
class ReadWriteCore constructor(context: Context, name: String) {
    private val filePath: String = FileUtils.getFilePath(context, name)
    private val lockFilePath: String = FileUtils.getFileLockPath(context, name)

    init {
        prepare()
    }

    internal fun write(obj: Any) {
        var oos: ObjectOutputStream? = null
        var fos: FileOutputStream? = null
        var lock: Lock? = null
        try {
            prepare()
            lock = Lock(lockFilePath).lock()
            fos = FileOutputStream(filePath)
            oos = ObjectOutputStream(BufferedOutputStream(fos))
            oos.writeObject(obj)
            oos.flush()
        } catch (e: Exception) {
            //do nothing
        } finally {
            IOUtils.closeSilently(oos)
            IOUtils.closeSilently(fos)
            lock?.release()
        }
    }

    internal fun read(): Any? {
        if (!IOUtils.isFileExist(filePath)) {
            return null
        }

        var ois: ObjectInputStream? = null
        var fis: FileInputStream? = null
        var lock: Lock? = null
        return try {
            lock = Lock(lockFilePath).lock()
            fis = FileInputStream(filePath)
            ois = ObjectInputStream(BufferedInputStream(fis))
            ois.readObject()
        } catch (e: Exception) {
            null
        } finally {
            IOUtils.closeSilently(ois)
            IOUtils.closeSilently(fis)
            lock?.release()
        }
    }

    private fun prepare() {
        val file = File(filePath)
        val parentFile = file.parentFile
        parentFile?.also {
            if (!it.exists()) {
                it.mkdirs()
            }
        }
    }
}