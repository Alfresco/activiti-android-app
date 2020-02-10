package com.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.alfresco.android.aims.R
import com.alfresco.auth.activity.AIMSWelcomeViewModel
import com.alfresco.common.FragmentBuilder
import com.alfresco.core.extension.isBlankOrEmpty
import com.google.android.material.textfield.TextInputLayout

class SsoAuthFragment : DialogFragment() {

    private val viewModel: AIMSWelcomeViewModel by activityViewModels()

    private lateinit var identityServiceTv: TextView

    private lateinit var processUrlTil: TextInputLayout
    private lateinit var signInBtn: Button

    private val rootView: View get() = view!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fr_aims_sso_auth, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.setHasNavigation(true)

        identityServiceTv = rootView.findViewById(R.id.tvConnectUrl)

        processUrlTil = rootView.findViewById(R.id.tilProcessUrl)

        signInBtn = rootView.findViewById(R.id.btnSSOSignIn)
        signInBtn.setOnClickListener { ssoSignIn() }

        arguments?.let {
            val defaultProcessUrl = if (it.getString(ARG_PROCESS_LOCATION) != null) it.getString(ARG_PROCESS_LOCATION) else ""
            processUrlTil.editText?.setText(defaultProcessUrl)
            processUrlTil.editText?.setSelection(0)

            val identityServiceUrl = if (it.getString(ARG_IDENTITY_SERVICE_URL) != null) it.getString(ARG_IDENTITY_SERVICE_URL) else ""
            identityServiceTv.setText(identityServiceUrl)
        }
    }

    private fun ssoSignIn() {
        val processRepositoryUrl = processUrlTil.editText?.text.toString()
        val identityServiceUrl = identityServiceTv.text.toString()

        if (!processRepositoryUrl.isBlankOrEmpty() && !identityServiceUrl.isBlankOrEmpty()) {
            viewModel.ssoLogin(identityServiceUrl, processRepositoryUrl)
        }
    }

    class Builder(parent: FragmentActivity) : FragmentBuilder(parent) {

        fun identityServiceUrl(identityServiceUrl: String): Builder {
            extraConfiguration.putString(ARG_IDENTITY_SERVICE_URL, identityServiceUrl)

            return this
        }

        fun processRepositoryLocation(location: String): Builder {
            extraConfiguration.putString(ARG_PROCESS_LOCATION, location)

            return this
        }

        override fun build(args: Bundle): Fragment {
            val fragment = SsoAuthFragment()
            fragment.arguments = args
            return fragment
        }
    }

    companion object {

        val TAG = SsoAuthFragment::class.java.name

        val ARG_IDENTITY_SERVICE_URL = "arg_identity_service_url"
        val ARG_PROCESS_LOCATION = "arg_process_repository_location"

        fun with(activity: FragmentActivity): Builder = Builder(activity)
    }
}