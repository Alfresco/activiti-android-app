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
import com.alfresco.android.aims.R
import com.alfresco.android.aims.databinding.FrAuthSsoBinding
import com.alfresco.auth.activity.LoginViewModel
import com.alfresco.common.FragmentBuilder

class SsoAuthFragment : DialogFragment() {

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FrAuthSsoBinding>(inflater, R.layout.fr_auth_sso, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.setHasNavigation(true)
    }

    override fun onStart() {
        super.onStart()

        // Reset action bar title
        activity?.title = ""
    }

    class Builder(parent: FragmentActivity) : FragmentBuilder(parent) {
        override val fragmentTag = TAG

        override fun build(args: Bundle): Fragment {
            val fragment = SsoAuthFragment()
            fragment.arguments = args
            return fragment
        }
    }

    companion object {

        val TAG = SsoAuthFragment::class.java.name

        fun with(activity: FragmentActivity): Builder = Builder(activity)
    }
}