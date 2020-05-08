package com.turbo.fsp.cache

import android.util.LruCache
import com.turbo.fsp.FSP

/**
 * @author leiiiooo
 * @date 2020/5/7
 */
class FspCache constructor(maxSize: Int) : LruCache<String, FSP>(maxSize) {
    companion object {
        private val DEFAULT_MAX_SIZE = Runtime.getRuntime().maxMemory() / 1024 / 16
    }

    constructor() : this(DEFAULT_MAX_SIZE.toInt()) {}

    override fun sizeOf(key: String?, value: FSP?): Int {
        return if (value === null) 0 else value.sizeOf()
    }

    override fun create(key: String): FSP {
        return FSP(key)
    }
}