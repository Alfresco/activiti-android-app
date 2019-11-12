package com.activiti.android.app.fragments.auth

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.activiti.android.app.R
import com.activiti.android.app.extensions.observe
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder
import com.alfresco.auth.GlobalAuthConfig
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_help.*
import kotlinx.android.synthetic.main.fragment_advanced_settings.*

/**
 * Created by Bogdan Roatis on 10/28/2019.
 */
class AdvancedSettingsFragment : Fragment() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_advanced_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        btnSave.setOnClickListener {
            authViewModel.changeSettings(
                    tilRealm.editText?.text.toString(),
                    tilClientId.editText?.text.toString())
            activity?.onBackPressed()
        }

        authViewModel.apply {
            observe(authConfig, ::showDetails)
        }

        bottomSheetBehavior = BottomSheetBehavior.from<View>(bottomSheetHelp)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        btnNeedHelp.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        tvHelpTitle.text = Html.fromHtml(getString(R.string.auth_start_help_title))
        tvHelpSubTitle.text = Html.fromHtml(getString(R.string.auth_start_help_subtitle))
        tvHelpDescription.text = Html.fromHtml(getString(R.string.auth_start_help_description))
        btnHelpClose.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun showDetails(globalAuthConfig: GlobalAuthConfig) {
        tilClientId.editText?.setText(globalAuthConfig.clientId)
        tilRedirectUrl.editText?.setText(globalAuthConfig.redirectUrl)
        tilPort.editText?.setText("443")
        tilServiceDocument.editText?.setText("activiti-app")
        tilRealm.editText?.setText(globalAuthConfig.realm)
    }

    companion object {
        val tag = AdvancedSettingsFragment::class.java.name

        fun newInstanceByTemplate(b: Bundle): AdvancedSettingsFragment {
            return AdvancedSettingsFragment().apply {
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
