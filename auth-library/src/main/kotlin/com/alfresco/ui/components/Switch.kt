package com.alfresco.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.Switch
import androidx.databinding.BindingAdapter


/**
 * Subclass used only for data binding drag interaction
 */
class Switch(context: Context, attrs: AttributeSet?) : Switch(context, attrs) {

    internal var touchAction: OnTouchEvent? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        val result = super.onTouchEvent(ev)
        touchAction?.onTouchEvent(this, ev)
        return result
    }
}

@BindingAdapter("android:onTouchEvent")
fun setOnTouchListener(switch: com.alfresco.ui.components.Switch, action: OnTouchEvent) {
    switch.touchAction = action
}

interface OnTouchEvent {
    fun onTouchEvent(switch: Switch, event: MotionEvent?)
}
