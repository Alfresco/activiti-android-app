package com.activiti.android.app.fragments.auth

import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.activiti.android.app.R
import com.activiti.android.app.extensions.observe
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder
import com.activiti.client.api.constant.ActivitiAPI
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_help.*
import kotlinx.android.synthetic.main.fragment_login_start.*
import kotlinx.android.synthetic.main.layout_loading.*


/**
 * Created by Bogdan Roatis on 10/28/2019.
 */
class StartAuthFragment : Fragment(R.layout.fragment_login_start) {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        tilAlfrescoUrl.editText?.setText("192.168.0.103:8080")

        btnConnect.setOnClickListener {
            authViewModel.connect(tilAlfrescoUrl.editText?.text.toString())
            //            authViewModel.createHostnameURL(tilAlfrescoUrl.editText?.text.toString())
        }

        btnNeedHelp.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        btnAdvancedSettings.setOnClickListener {
            AdvancedSettingsFragment.with(activity!!).back(true).display()
        }

        btnLoginCloud.setOnClickListener {
            AuthCredentialsFragment.with(activity!!)
                    .hostname(ActivitiAPI.SERVER_URL_ENDPOINT)
                    .https(true)
                    .back(true)
                    .display()
        }

        authViewModel.apply {
            //TODO do event based actions using the view models
//            observe(connectResult, ::handleConnectResult)
            observe(onResult, ::handleConnectResult)
            observe(isLoading, ::handleLoading)
        }

//        tilAlfrescoUrl.editText?.setText("http://alfresco-identity-service.mobile.dev.alfresco.me")
        tilAlfrescoUrl.editText?.setText(ActivitiAPI.SERVER_URL_ENDPOINT)

        bottomSheetBehavior = BottomSheetBehavior.from<View>(bottomSheetHelp)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        tvHelpTitle.text = Html.fromHtml(getString(R.string.auth_start_help_title))
        tvHelpSubTitle.text = Html.fromHtml(getString(R.string.auth_start_help_subtitle))
        tvHelpDescription.text = Html.fromHtml(getString(R.string.auth_start_help_description))
        btnHelpClose.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        loadingContainer.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun handleConnectResult(connectResult: ConnectResult) {
        connectResult.run {
            when {
                isBasicUrl -> {
                    tilAlfrescoUrl.error = null
                    AuthCredentialsFragment.with(activity!!)
                            .isCloud(false)
                            .hostname(tilAlfrescoUrl?.editText?.text.toString())
                            .back(true)
                            .display()
                }
                isIdentityUrl -> AuthSSOProcessFragment.with(activity!!).back(true).display()
                else -> tilAlfrescoUrl.error = null
            }
        }
    }

    companion object {
        val tag = StartAuthFragment::class.java.name

        fun newInstanceByTemplate(b: Bundle): StartAuthFragment {
            return StartAuthFragment().apply {
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
