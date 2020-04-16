package com.alfresco.ui.components

import android.annotation.SuppressLint

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityManager
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.alfresco.android.aims.R
import com.google.android.material.snackbar.BaseTransientBottomBar

class Snackbar private constructor(
        parent: ViewGroup,
        content: View,
        contentViewCallback: com.google.android.material.snackbar.ContentViewCallback) : BaseTransientBottomBar<Snackbar?>(parent, content, contentViewCallback) {
    private val accessibilityManager: AccessibilityManager?
    private var hasAction = false

    /**
     * Callback class for [Snackbar] instances.
     *
     *
     * Note: this class is here to provide backwards-compatible way for apps written before the
     * existence of the base [BaseTransientBottomBar] class.
     *
     * @see BaseTransientBottomBar.addCallback
     */
    class Callback : BaseCallback<Snackbar?>() {
        override fun onShown(sb: Snackbar?) { // Stub implementation to make API check happy.
        }

        override fun onDismissed(transientBottomBar: Snackbar?, @DismissEvent event: Int) { // Stub implementation to make API check happy.
        }

        companion object {
            /** Indicates that the Snackbar was dismissed via a swipe.  */
            const val DISMISS_EVENT_SWIPE = BaseCallback.DISMISS_EVENT_SWIPE
            /** Indicates that the Snackbar was dismissed via an action click.  */
            const val DISMISS_EVENT_ACTION = BaseCallback.DISMISS_EVENT_ACTION
            /** Indicates that the Snackbar was dismissed via a timeout.  */
            const val DISMISS_EVENT_TIMEOUT = BaseCallback.DISMISS_EVENT_TIMEOUT
            /** Indicates that the Snackbar was dismissed via a call to [.dismiss].  */
            const val DISMISS_EVENT_MANUAL = BaseCallback.DISMISS_EVENT_MANUAL
            /** Indicates that the Snackbar was dismissed from a new Snackbar being shown.  */
            const val DISMISS_EVENT_CONSECUTIVE = BaseCallback.DISMISS_EVENT_CONSECUTIVE
        }
    }

    private var callback: BaseCallback<Snackbar>? = null

    /**
     * Update the text in this [Snackbar].
     *
     * @param message The new text for this [BaseTransientBottomBar].
     */
    fun setText(message: CharSequence): Snackbar {
        val contentLayout = view.getChildAt(0) as SnackbarContentLayout
        val tv = contentLayout.titleView
        tv.text = message
        return this
    }

    /**
     * Update the text in this [Snackbar].
     *
     * @param resId The new text for this [BaseTransientBottomBar].
     */
    fun setText(@StringRes resId: Int): Snackbar {
        return setText(context.getText(resId))
    }

    /**
     * Update the text in this [Snackbar].
     *
     * @param message The new text for this [BaseTransientBottomBar].
     */
    fun setMessage(message: CharSequence): Snackbar {
        val contentLayout = view.getChildAt(0) as SnackbarContentLayout
        val tv = contentLayout.messageView
        tv.text = message
        return this
    }

    /**
     * Update the text in this [Snackbar].
     *
     * @param resId The new text for this [BaseTransientBottomBar].
     */
    fun setMessage(@StringRes resId: Int): Snackbar {
        return setMessage(context.getText(resId))
    }

    /**
     * Set the action to be displayed in this [BaseTransientBottomBar].
     *
     * @param resId String resource to display for the action
     * @param listener callback to be invoked when the action is clicked
     */
    fun setAction(@StringRes resId: Int, listener: View.OnClickListener?): Snackbar {
        return setAction(context.getText(resId), listener)
    }

    /**
     * Set the action to be displayed in this [BaseTransientBottomBar].
     *
     * @param text Text to display for the action
     * @param listener callback to be invoked when the action is clicked
     */
    fun setAction(
            text: CharSequence?, listener: View.OnClickListener?): Snackbar {
        val contentLayout = view.getChildAt(0) as SnackbarContentLayout
        val tv = contentLayout.actionView!!
        if (TextUtils.isEmpty(text) || listener == null) {
            tv.visibility = View.GONE
            tv.setOnClickListener(null)
            hasAction = false
        } else {
            hasAction = true
            tv.visibility = View.VISIBLE
            tv.text = text
            tv.setOnClickListener { view ->
                listener.onClick(view)
                // Now dismiss the Snackbar
                dispatchDismiss(BaseCallback.DISMISS_EVENT_ACTION)
            }
        }
        return this
    }

    @Duration
    override fun getDuration(): Int {
        val userSetDuration = super.getDuration()
        if (userSetDuration == LENGTH_INDEFINITE) {
            return LENGTH_INDEFINITE
        }

        // If touch exploration is enabled override duration to give people chance to interact.
        return if (hasAction && accessibilityManager!!.isTouchExplorationEnabled) LENGTH_INDEFINITE else userSetDuration
    }

    /**
     * Sets the text color of the message specified in [.setText] and [ ][.setText].
     */
    fun setTextColor(colors: ColorStateList?): Snackbar {
        val contentLayout = view.getChildAt(0) as SnackbarContentLayout
        val tv = contentLayout.titleView
        tv.setTextColor(colors)
        return this
    }

    /**
     * Sets the text color of the message specified in [.setText] and [ ][.setText].
     */
    fun setTextColor(@ColorInt color: Int): Snackbar {
        val contentLayout = view.getChildAt(0) as SnackbarContentLayout
        val tv = contentLayout.titleView
        tv.setTextColor(color)
        return this
    }

    /**
     * Sets the text color of the action specified in [.setAction].
     */
    fun setActionTextColor(colors: ColorStateList?): Snackbar {
        val contentLayout = view.getChildAt(0) as SnackbarContentLayout
        val tv = contentLayout.actionView
        tv.setTextColor(colors)
        return this
    }

    /**
     * Sets the text color of the action specified in [.setAction].
     */
    fun setActionTextColor(@ColorInt color: Int): Snackbar {
        val contentLayout = view.getChildAt(0) as SnackbarContentLayout
        val tv = contentLayout.actionView
        tv.setTextColor(color)
        return this
    }

    /** Sets the tint color of the background Drawable.  */
    fun setBackgroundTint(@ColorInt color: Int): Snackbar {
        return setBackgroundTintList(ColorStateList.valueOf(color))
    }

    /** Sets the tint color state list of the background Drawable.  */
    @SuppressLint("RestrictedApi")
    fun setBackgroundTintList(colorStateList: ColorStateList?): Snackbar {
        view.backgroundTintList = colorStateList
        return this
    }

    @SuppressLint("RestrictedApi")
    fun setBackgroundTintMode(mode: PorterDuff.Mode?): Snackbar {
        view.backgroundTintMode = mode
        return this
    }

    fun setStyle(context: Context, style: Int) {
        val backgroundResId = when (style) {
            STYLE_SUCCESS -> R.drawable.alfresco_snackbar_background_success
            STYLE_WARNING -> R.drawable.alfresco_snackbar_background_warning
            STYLE_INFO -> R.drawable.alfresco_snackbar_background_info
            STYLE_ERROR -> R.drawable.alfresco_snackbar_background_error
            else -> -1
        }

        if (backgroundResId != -1) {
            getView().background = context.resources.getDrawable(backgroundResId, null)
        }
    }

    companion object {
        const val LENGTH_LONG = BaseTransientBottomBar.LENGTH_LONG
        const val LENGTH_SHORT = BaseTransientBottomBar.LENGTH_SHORT
        const val LENGTH_INDEFINITE = BaseTransientBottomBar.LENGTH_INDEFINITE

        const val STYLE_SUCCESS = 1
        const val STYLE_WARNING = 2
        const val STYLE_INFO = 3
        const val STYLE_ERROR = 4

        private val SNACKBAR_BUTTON_STYLE_ATTR = intArrayOf(R.attr.snackbarButtonStyle)
        private val SNACKBAR_CONTENT_STYLE_ATTRS = intArrayOf(R.attr.snackbarButtonStyle, R.attr.snackbarTextViewStyle)
        /**
         * Make a Snackbar to display a message
         *
         *
         * Snackbar will try and find a parent view to hold Snackbar's view from the value given to
         * `view`. Snackbar will walk up the view tree trying to find a suitable parent, which is
         * defined as a [CoordinatorLayout] or the window decor's content view, whichever comes
         * first.
         *
         *
         * Having a [CoordinatorLayout] in your view hierarchy allows Snackbar to enable certain
         * features, such as swipe-to-dismiss and automatically moving of widgets.
         *
         * @param view The view to find a parent from. This view is also used to find the anchor view when
         * calling [Snackbar.setAnchorView].
         * @param style Either .STYLE_POSITIVE, .STYLE_NEGATIVE, .STYLE_NEUTRAL
         * @param title The text to show. Can be formatted text.
         * @param message The subtitle to show. Can be formatted text.
         * @param duration How long to display the message. Can be [.LENGTH_SHORT], [     ][.LENGTH_LONG], [.LENGTH_INDEFINITE], or a custom duration in milliseconds.
         */
        fun make(view: View, style: Int, title: CharSequence, message: CharSequence, duration: Int): Snackbar {
            val parent = findSuitableParent(view)
                    ?: throw IllegalArgumentException(
                            "No suitable parent found from the given view. Please provide a valid view.")
            val inflater = LayoutInflater.from(parent.context)
            val content = inflater.inflate(com.alfresco.android.aims.R.layout.view_alfresco_snackbar, parent, false) as SnackbarContentLayout
            val snackbar = Snackbar(parent, content, content)
            snackbar.setText(title)
            snackbar.setMessage(message)
            snackbar.duration = duration
            snackbar.setStyle(parent.context, style)
            return snackbar
        }

        /**
         * Make a Snackbar to display a message.
         *
         *
         * Snackbar will try and find a parent view to hold Snackbar's view from the value given to
         * `view`. Snackbar will walk up the view tree trying to find a suitable parent, which is
         * defined as a [CoordinatorLayout] or the window decor's content view, whichever comes
         * first.
         *
         *
         * Having a [CoordinatorLayout] in your view hierarchy allows Snackbar to enable certain
         * features, such as swipe-to-dismiss and automatically moving of widgets.
         *
         * @param view The view to find a parent from.
         * @param style Either .STYLE_POSITIVE, .STYLE_NEGATIVE, .STYLE_NEUTRAL
         * @param titleResId The resource id of the string resource to use. Can be formatted text.
         * @param resId The resource id of the string resource to use. Can be formatted text.
         * @param duration How long to display the message. Can be [.LENGTH_SHORT], [     ][.LENGTH_LONG], [.LENGTH_INDEFINITE], or a custom duration in milliseconds.
         */
        fun make(view: View, style: Int, @StringRes titleResId: Int, @StringRes messageResId: Int, duration: Int): Snackbar {
            return make(view, style, view.resources.getText(titleResId), view.resources.getText(messageResId), duration)
        }

        @Suppress("NAME_SHADOWING")
        private fun findSuitableParent(view: View): ViewGroup? {
            var view: View? = view
            var fallback: ViewGroup? = null
            do {
                if (view is CoordinatorLayout) { // We've found a CoordinatorLayout, use it
                    return view
                } else if (view is FrameLayout) {
                    fallback = if (view.getId() == android.R.id.content) { // If we've hit the decor content view, then we didn't find a CoL in the
// hierarchy, so use it.
                        return view
                    } else { // It's not the content view but we'll use it as our fallback
                        view
                    }
                }
                if (view != null) { // Else, we will loop and crawl up the view hierarchy and try to find a parent
                    val parent = view.parent
                    view = if (parent is View) parent else null
                }
            } while (view != null)
            // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
            return fallback
        }
    }

    init {
        accessibilityManager = parent.context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    }
}
