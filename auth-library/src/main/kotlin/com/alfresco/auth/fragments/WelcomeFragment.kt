package com.alfresco.auth.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.alfresco.auth.activity.AIMSWelcomeViewModel
import com.alfresco.android.aims.R
import com.alfresco.common.FragmentBuilder
import com.google.android.material.textfield.TextInputLayout

class WelcomeFragment : DialogFragment() {

    private val viewModel: AIMSWelcomeViewModel by activityViewModels()

    private lateinit var connectBtn: Button

    private lateinit var cloudConnectBtn: Button

    private lateinit var advancedSettings: Button

    private lateinit var connectUrlTil: TextInputLayout

    private lateinit var helpBtn: Button

    private val rootView: View get() = view!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fr_aims_welcome, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.setHasNavigation(false)

        connectUrlTil = rootView.findViewById(R.id.tilConnectUrl)

        connectUrlTil.editText?.setText("alfresco-identity-service.mobile.dev.alfresco.me")

        //connectUrlTil.editText?.setText("activiti.alfresco.com")

        connectBtn = rootView.findViewById(R.id.btnConnect)
        connectBtn.setOnClickListener { checkConnectUrl() }

        cloudConnectBtn = rootView.findViewById(R.id.btnCloudSignIn)
        cloudConnectBtn.setOnClickListener { cloudConnect() }

        advancedSettings = rootView.findViewById(R.id.btnAdvancedSettings)
        advancedSettings.setOnClickListener { showAdvancedSettings() }

        helpBtn = rootView.findViewById(R.id.btnHelp)
        helpBtn.setOnClickListener { showHelp() }
    }

    override fun onStart() {
        super.onStart()
        activity?.title = ""
    }

    fun checkConnectUrl() {
        viewModel.checkAuthType(connectUrlTil.editText?.text.toString())
    }

    fun cloudConnect() {
        viewModel.cloudAuthType()
    }

    fun showAdvancedSettings() {
        AdvancedSettingsFragment.with(activity!!).display()
    }

    fun showHelp() {
        HelpFragment.with(activity!!).message(R.string.auth_help_identity_body).show()
    }

    class Builder(parent: FragmentActivity) : FragmentBuilder(parent) {

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