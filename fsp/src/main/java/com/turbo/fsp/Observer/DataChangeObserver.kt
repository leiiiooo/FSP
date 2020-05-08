package com.turbo.fsp.Observer

import android.os.FileObserver

/**
 * @author leiiiooo
 * @date 2020/5/7
 */
class DataChangeObserver(path: String?) : FileObserver(path, FILE_EVENTS) {
    companion object {
        private const val FILE_EVENTS = MODIFY or CLOSE_WRITE or DELETE
    }

    override fun onEvent(event: Int, path: String?) {
        when (event) {
            CLOSE_WRITE -> onCloseWrite(path)
            DELETE -> onDelete(path)
        }
    }

    private fun onDelete(path: String?) {
        path ?: return
        mListener.mDeleteAction?.invoke(path)
    }

    private fun onCloseWrite(path: String?) {
        path ?: return
        mListener.mCloseWriteAction?.invoke(path)
    }

    private lateinit var mListener: ListenerBuilder

    fun setListener(listenerBuilder: ListenerBuilder.() -> Unit) {
        mListener = ListenerBuilder().also(listenerBuilder)
    }

    inner class ListenerBuilder {
        internal var mCloseWriteAction: ((path: String?) -> Unit)? = null
        internal var mDeleteAction: ((path: String?) -> Unit)? = null

        fun onCloseWrite(action: (path: String?) -> Unit) {
            mCloseWriteAction = action
        }

        fun onDelete(action: (path: String?) -> Unit) {
            mDeleteAction = action
        }
    }
}