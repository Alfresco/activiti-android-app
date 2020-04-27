package com.alfresco.auth.fragments

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.alfresco.android.aims.R
import com.alfresco.common.FragmentBuilder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class HelpFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_auth_help, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val view = view ?: return

        arguments?.let {
            val messageResId = it.getInt(ARG_MESSAGE_RES_ID, -1)
            val bodyTv: TextView = view.findViewById(R.id.bodyTxt)
            val value = resources.getString(messageResId)
            bodyTv.setText(HtmlCompat.fromHtml(value, HtmlCompat.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE)
        }

        val closeBtn: Button = view.findViewById(R.id.btnClose)
        closeBtn.setOnClickListener { dismiss() }

        // Fix for https://issuetracker.google.com/issues/37132390
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val bottomSheet = (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheet?.let {
                    BottomSheetBehavior.from<View>(it).apply {
                        state = BottomSheetBehavior.STATE_EXPANDED
                        peekHeight = bottomSheet.height
                    }
                }
                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    class Builder(parent: FragmentActivity) : FragmentBuilder(parent) {
        override val fragmentTag = TAG

        override fun build(args: Bundle): Fragment {
            val fragment = HelpFragment()
            fragment.arguments = args

            return fragment
        }

        fun message(@StringRes msgResId: Int) : Builder {
            extraConfiguration.putInt(ARG_MESSAGE_RES_ID, msgResId)
            return this
        }

        fun show() {
            (build(extraConfiguration) as BottomSheetDialogFragment).show(parent.supportFragmentManager, TAG)
        }
    }

    companion object {
        private val TAG = AdvancedSettingsFragment::class.java.name
        private const val ARG_MESSAGE_RES_ID = "message_res_id"

        fun with(activity: FragmentActivity): HelpFragment.Builder = HelpFragment.Builder(activity)
    }
}
