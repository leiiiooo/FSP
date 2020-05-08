package com.turbo.fsp.task

/**
 * @author leiiiooo
 * @date 2020/5/7
 */
class ReloadTask : Runnable {
    override fun run() {
        mListener.mRunAction?.invoke()
    }

    private lateinit var mListener: ListenerBuilder

    fun setListener(listenerBuilder: ListenerBuilder.() -> Unit) {
        mListener = ListenerBuilder().also(listenerBuilder)
    }

    inner class ListenerBuilder {
        internal var mRunAction: (() -> Unit)? = null

        fun onRun(action: () -> Unit) {
            mRunAction = action
        }
    }

}