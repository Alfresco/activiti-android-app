package com.alfresco.common

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.alfresco.android.aims.R

object BindingAdapters {
    @BindingAdapter("app:textColorEnabled")
    @JvmStatic fun textColorEnabled(view: TextView, enabled: Boolean) {
        val context = view.context
        val color = if (enabled) context.getColor(R.color.aims_primary_text)
            else context.getColor(R.color.aims_secondary_text)
        view.setTextColor(color)
    }
}
