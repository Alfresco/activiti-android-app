package com.activiti.android.app.fragments.account.aims

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.activiti.android.app.R
import com.activiti.android.app.activity.AIMSWelcomeViewModel
import com.activiti.android.ui.fragments.AlfrescoFragment
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder
import com.google.android.material.textfield.TextInputLayout

class AIMSWelcomeFragment : AlfrescoFragment() {

    private val viewModel: AIMSWelcomeViewModel by activityViewModels()

    private lateinit var connectBtn: Button

    private lateinit var cloudConnectBtn: Button

    private lateinit var advancedSettings: Button

    private lateinit var connectUrlTil: TextInputLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fr_aims_welcome, container, false)

        return rootView
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
    }

    fun checkConnectUrl() {
        viewModel.checkAuthType(connectUrlTil.editText?.text.toString())
    }

    fun cloudConnect() {
        viewModel.cloudAuthType()
    }

    fun showAdvancedSettings() {
        AIMSAdvancedSettingsFragment.with(activity!!).display()
    }

    class Builder : AlfrescoFragmentBuilder {

        constructor(activity: FragmentActivity) : super(activity) {
            extraConfiguration = Bundle()
        }

        constructor(activity: FragmentActivity, configuration: Map<String, Object>) : super(activity, configuration)

        override fun createFragment(bundle: Bundle) = newInstancebyTemplate(bundle)
    }

    companion object {

        val TAG = AIMSWelcomeFragment::class.java.name

        fun newInstancebyTemplate(args: Bundle): AIMSWelcomeFragment {
            val fragment = AIMSWelcomeFragment()
            fragment.arguments = args

            return fragment
        }

        fun with(activity: FragmentActivity): Builder = Builder(activity)
    }
}