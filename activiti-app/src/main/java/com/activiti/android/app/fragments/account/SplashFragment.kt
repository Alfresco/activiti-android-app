package com.activiti.android.app.fragments.account

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.activiti.android.app.R
import com.activiti.android.app.fragments.auth.StartAuthFragment
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder

/**
 * Created by Bogdan Roatis on 11/5/2019.
 */
class SplashFragment : Fragment(R.layout.fragment_splash) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        StartAuthFragment.with(activity!!).back(false).display()
    }

    companion object {
        val tag = SplashFragment::class.java.name

        fun newInstanceByTemplate(b: Bundle): SplashFragment {
            return SplashFragment().apply {
                arguments = b
            }
        }

        fun with(activity: FragmentActivity): Builder {
            return Builder(activity)
        }
    }

    class Builder : AlfrescoFragmentBuilder {
        constructor(activity: FragmentActivity) : super(activity) {
            this.extraConfiguration = Bundle()
        }

        constructor(appActivity: FragmentActivity, configuration: Map<String, Any>) : super(appActivity, configuration) {}

        override fun createFragment(b: Bundle): Fragment? {
            return newInstanceByTemplate(b)
        }
    }
}
