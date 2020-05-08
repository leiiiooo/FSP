package com.turbo.fsp.io

import java.io.Closeable
import java.io.File
import java.io.IOException

/**
 * @author leiiiooo
 * @date 2020/5/6
 */
class IOUtils {
    companion object {
        //close silently
        fun closeSilently(closeable: Closeable?) {
            closeable?.also {
                try {
                    it.close()
                } catch (exception: IOException) {
                    //do nothing
                }
            }
        }

        //is file exist
        fun isFileExist(path: String): Boolean {
            val file = File(path)
            return file.exists()
        }
    }
}