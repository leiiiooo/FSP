package com.turbo.fsp.editor

import android.content.SharedPreferences
import com.turbo.fsp.`interface`.FEditor
import java.io.Serializable

/**
 * @author leiiiooo
 * @date 2020/5/7
 */
class FspEditor : FEditor {
    override fun putSerializable(key: String, serializable: Serializable?): FEditor {
        put(key, serializable)
        return this
    }

    override fun putLong(key: String?, value: Long): SharedPreferences.Editor {
        put(key, value)
        return this
    }

    override fun putInt(key: String?, value: Int): SharedPreferences.Editor {
        put(key, value)
        return this
    }

    override fun putBoolean(key: String?, value: Boolean): SharedPreferences.Editor {
        put(key, value)
        return this
    }

    override fun putStringSet(key: String?, values: MutableSet<String>?): SharedPreferences.Editor {
        put(key, values)
        return this
    }

    override fun putFloat(key: String?, value: Float): SharedPreferences.Editor {
        put(key, value)
        return this
    }

    override fun putString(key: String?, value: String?): SharedPreferences.Editor {
        put(key, value)
        return this
    }

    override fun clear(): SharedPreferences.Editor {
        mListener.mClearAction?.invoke()
        return this
    }

    override fun remove(key: String?): SharedPreferences.Editor {
        mListener.mRemoveAction?.invoke(key)
        return this
    }

    override fun commit(): Boolean {
        mListener.mCommitAction?.invoke()
        return true
    }

    override fun apply() {
        mListener.mApplyAction?.invoke()
    }

    private fun put(s: String?, obj: Any?) {
        mListener.mPutAction?.invoke(s, obj)
    }

    private lateinit var mListener: ListenerBuilder

    fun setListener(listenerBuilder: ListenerBuilder.() -> Unit) {
        mListener = ListenerBuilder().also(listenerBuilder)
    }

    inner class ListenerBuilder {
        internal var mApplyAction: (() -> Unit)? = null
        internal var mClearAction: (() -> Unit)? = null
        internal var mCommitAction: (() -> Unit)? = null
        internal var mPutAction: ((s: String?, obj: Any?) -> Unit)? = null
        internal var mRemoveAction: ((key: String?) -> Unit)? = null

        fun onApply(action: () -> Unit) {
            mApplyAction = action
        }

        fun onClear(action: () -> Unit) {
            mClearAction = action
        }

        fun onCommit(action: () -> Unit) {
            mCommitAction = action
        }

        fun onPut(action: (s: String?, obj: Any?) -> Unit) {
            mPutAction = action
        }

        fun onRemove(action: (key: String?) -> Unit) {
            mRemoveAction = action
        }
    }

}