package com.alfresco.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.alfresco.android.aims.R

abstract class FragmentBuilder(protected val parent: FragmentActivity) {
    val extraConfiguration = Bundle()
    abstract val fragmentTag: String

    abstract fun build(args: Bundle): Fragment;

    fun display() {
        if (parent.supportFragmentManager.findFragmentById(R.id.frame_placeholder) != null) {
            internalDisplay()
        } else {
            replace()
        }
    }

    private fun internalDisplay() {
        parent.supportFragmentManager.beginTransaction()
                .setCustomAnimations(
                        R.anim.anim_slide_in_right,
                        R.anim.anim_slide_out_left,
                        R.anim.anim_slide_in_left,
                        R.anim.anim_slide_out_right)
                .replace(R.id.frame_placeholder, build(extraConfiguration), fragmentTag)
                .addToBackStack(null)
                .commit()
    }

    fun replace() {
        parent.supportFragmentManager.beginTransaction()
                .replace(R.id.frame_placeholder, build(extraConfiguration), fragmentTag)
                .commit()
    }
}
