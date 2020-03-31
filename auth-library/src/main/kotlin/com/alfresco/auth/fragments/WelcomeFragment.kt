package com.alfresco.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.alfresco.auth.activity.LoginViewModel
import com.alfresco.android.aims.R
import com.alfresco.android.aims.databinding.FrAuthWelcomeBinding
import com.alfresco.common.FragmentBuilder

class WelcomeFragment : DialogFragment() {

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FrAuthWelcomeBinding>(inflater, R.layout.fr_auth_welcome, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.setHasNavigation(false)
    }

    override fun onStart() {
        super.onStart()
        activity?.title = ""
    }

    class Builder(parent: FragmentActivity) : FragmentBuilder(parent) {
        override val fragmentTag = TAG

        override fun build(args: Bundle): Fragment {
            val fragment = WelcomeFragment()
            fragment.arguments = args

            return fragment
        }
    }

    companion object {

        val TAG = WelcomeFragment::class.java.name

        fun with(activity: FragmentActivity): Builder = Builder(activity)
    }
}