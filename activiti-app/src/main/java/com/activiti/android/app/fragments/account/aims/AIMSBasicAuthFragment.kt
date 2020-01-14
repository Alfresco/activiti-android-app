package com.activiti.android.app.fragments.account.aims

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.activiti.android.app.R
import com.activiti.android.app.activity.AIMSWelcomeViewModel
import com.activiti.android.ui.fragments.AlfrescoFragment
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder

class AIMSBasicAuthFragment : AlfrescoFragment() {

    private val viewModel: AIMSWelcomeViewModel by activityViewModels()

    private var withCloud: Boolean = false

    private var hostname: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fr_aims_basic_auth, container, false)

        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.setHasNavigation(true)

        arguments?.let {
            withCloud = it.getBoolean(ARG_WITH_CLOUD, false)
            hostname = it.getString(ARG_HOSTNAME)

            updateUi()
        }
    }

    private fun updateUi() {
        if (withCloud) {
            rootView.findViewById<View>(R.id.tvSigninTo).visibility = View.GONE
            rootView.findViewById<View>(R.id.tvConnectUrl).visibility = View.GONE

            rootView.findViewById<View>(R.id.tvBasicAuthInfoCloud).visibility = View.VISIBLE

        } else {
            rootView.findViewById<View>(R.id.tvSigninTo).visibility = View.VISIBLE
            rootView.findViewById<View>(R.id.tvConnectUrl).visibility = View.VISIBLE

            rootView.findViewById<TextView>(R.id.tvConnectUrl).setText(hostname)

            rootView.findViewById<View>(R.id.tvBasicAuthInfoCloud).visibility = View.GONE
        }
    }

    class Builder : AlfrescoFragmentBuilder {

        constructor(activity: FragmentActivity) : super(activity) {
            extraConfiguration = Bundle()
        }

        constructor(activity: FragmentActivity, configuration: Map<String, Object>) : super(activity, configuration)

        fun withHostname(hostname: String): Builder {
            extraConfiguration.putString(ARG_HOSTNAME, hostname)

            return this
        }

        fun withCloud(withCloud: Boolean): Builder {
            extraConfiguration.putBoolean(ARG_WITH_CLOUD, withCloud)

            return this
        }

        override fun createFragment(bundle: Bundle) = newInstancebyTemplate(bundle)
    }

    companion object {

        val TAG = AIMSBasicAuthFragment::class.java.name

        val ARG_HOSTNAME = "arg_hostname"
        val ARG_WITH_CLOUD = "arg_auth_with_cloud"

        fun newInstancebyTemplate(args: Bundle): AIMSBasicAuthFragment {
            val fragment = AIMSBasicAuthFragment()
            fragment.arguments = args

            return fragment
        }

        fun with(activity: FragmentActivity): Builder = Builder(activity)
    }
}