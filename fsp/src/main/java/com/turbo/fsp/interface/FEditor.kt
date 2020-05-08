package com.turbo.fsp.`interface`

import android.content.SharedPreferences
import androidx.annotation.Nullable
import java.io.Serializable

/**
 * @author leiiiooo
 * @date 2020/5/6
 */
interface FEditor : SharedPreferences.Editor {
    fun putSerializable(
        key: String,
        @Nullable serializable: Serializable?
    ): FEditor
}