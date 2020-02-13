package com.alfresco.ui.components

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RestrictTo
import com.alfresco.android.aims.R
import com.google.android.material.snackbar.ContentViewCallback

/** @hide
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
class SnackbarContentLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs), ContentViewCallback {
    lateinit var titleView: TextView
        private set
    lateinit var messageView: TextView
        private set
    lateinit var actionView: Button
        private set

    var backgroundTint: Int = -1

    override fun onFinishInflate() {
        super.onFinishInflate()

        titleView = findViewById(R.id.snackbar_title)
        messageView = findViewById(R.id.snackbar_message)
        actionView = findViewById(R.id.snackbar_action)
    }

    override fun animateContentIn(delay: Int, duration: Int) {

        titleView.alpha = 0f
        titleView.animate().alpha(1f).setDuration(duration.toLong()).setStartDelay(delay.toLong()).start()
        if (actionView.visibility == View.VISIBLE) {
            actionView.alpha = 0f
            actionView.animate().alpha(1f).setDuration(duration.toLong()).setStartDelay(delay.toLong()).start()
        }
    }

    override fun animateContentOut(delay: Int, duration: Int) {
        titleView.alpha = 1f
        titleView.animate().alpha(0f).setDuration(duration.toLong()).setStartDelay(delay.toLong()).start()
        if (actionView.visibility == View.VISIBLE) {
            actionView.alpha = 1f
            actionView.animate().alpha(0f).setDuration(duration.toLong()).setStartDelay(delay.toLong()).start()
        }
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.SnackbarContentLayout)
        val confirmationTint = a.getColor(R.styleable.SnackbarContentLayout_tintConfirmation, -1)
        backgroundTint = confirmationTint
        a.recycle()
    }
}
