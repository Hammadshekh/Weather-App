package com.fictivestudios.wheatherapp.utils
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class NonScrollableRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        // Do not intercept touch events
        return false
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        // Do not handle touch events
        return false
    }
}