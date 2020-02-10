package com.alfresco.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.auth.fragments.SsoAuthFragment
import com.alfresco.android.aims.R

abstract class FragmentBuilder(private val parent: FragmentActivity) {
    val extraConfiguration = Bundle()

    abstract fun build(args: Bundle): Fragment;

    fun display() {
        parent.supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_left,
                        R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_right)
                .replace(R.id.frame_placeholder, build(extraConfiguration), SsoAuthFragment.TAG)
                .addToBackStack(null)
                .commit()
    }

    fun replace() {
        parent.supportFragmentManager.beginTransaction()
                .replace(R.id.frame_placeholder, build(extraConfiguration), SsoAuthFragment.TAG)
                .commit()
    }
}
