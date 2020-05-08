package com.turbo.fsp.io

import android.content.Context
import java.io.File

/**
 * @author leiiiooo
 * @date 2020/5/7
 */
class FileUtils {
    companion object {
        private const val DEFAULT_DIR_PATH = "fsp"
        private const val DEFAULT_LOCK_SUFFIX = ".lock"

        fun getFilePath(context: Context, name: String): String {
            return context.filesDir.absolutePath + File.separator + DEFAULT_DIR_PATH + File.separator + name
        }

        fun getFileLockPath(context: Context, name: String): String {
            return getFilePath(context, name + DEFAULT_LOCK_SUFFIX)
        }
    }
}