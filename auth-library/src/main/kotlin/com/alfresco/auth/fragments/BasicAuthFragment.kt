package com.alfresco.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.alfresco.android.aims.R
import com.alfresco.auth.activity.AIMSWelcomeViewModel
import com.alfresco.common.FragmentBuilder

class BasicAuthFragment : DialogFragment() {

    private val viewModel: AIMSWelcomeViewModel by activityViewModels()

    private var withCloud: Boolean = false

    private var hostname: String? = null

    private val rootView: View get() = view!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fr_aims_basic_auth, container, false)
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

    class Builder(parent: FragmentActivity) : FragmentBuilder(parent) {
        override val fragmentTag = TAG

        fun withHostname(hostname: String): Builder {
            extraConfiguration.putString(ARG_HOSTNAME, hostname)

            return this
        }

        fun withCloud(withCloud: Boolean): Builder {
            extraConfiguration.putBoolean(ARG_WITH_CLOUD, withCloud)

            return this
        }

        override fun build(args: Bundle): Fragment {
            val fragment = BasicAuthFragment()
            fragment.arguments = args
            return fragment
        }
    }

    companion object {

        val TAG = BasicAuthFragment::class.java.name

        val ARG_HOSTNAME = "arg_hostname"
        val ARG_WITH_CLOUD = "arg_auth_with_cloud"

        fun with(activity: FragmentActivity): Builder = Builder(activity)
    }
}