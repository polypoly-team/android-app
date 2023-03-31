package com.github.polypoly.app.util
import android.content.res.Resources

val Int.toDp get() = (this / Resources.getSystem().displayMetrics.density).toInt()