package com.alfresco.ui.components

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.google.android.material.R
import com.google.android.material.textfield.TextInputLayout

class TextInputLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
        TextInputLayout(context, attrs, defStyleAttr) {

    constructor(context: Context): this(context, null) {
    }

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, R.attr.textInputStyle) {
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        super.addView(child, index, params)

        editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val editText = editText ?: return

                // Changing text programmatically incorrectly displays clear text icon
                if (!editText.isFocused && endIconMode == END_ICON_CLEAR_TEXT) {
                    isEndIconVisible = false
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no-op
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // no-op
            }
        })
    }
}