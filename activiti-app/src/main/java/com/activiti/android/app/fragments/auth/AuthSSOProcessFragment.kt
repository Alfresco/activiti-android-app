package com.activiti.android.app.fragments.auth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import com.activiti.android.app.R
import com.activiti.android.app.extensions.observe
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_help.*
import kotlinx.android.synthetic.main.fragment_login_sso_process.*

/**
 * Created by Bogdan Roatis on 11/11/2019.
 */
class AuthSSOProcessFragment : Fragment(R.layout.fragment_login_sso_process) {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetBehavior = BottomSheetBehavior.from<View>(bottomSheetHelp)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        btnNeedHelp.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        btnConnect.setOnClickListener {
            authViewModel.connect(tilProcessUrl.editText?.text.toString())
        }

        authViewModel.run {
            observe(onResult, ::handleConnectResult)
        }

        tilProcessUrl.requestFocus()
    }

    private fun handleConnectResult(connectResult: ConnectResult) {
        connectResult.run {
            when {
                isBasicUrl -> {
                    tilProcessUrl.error = null

                }
                else -> tilProcessUrl.error = null
            }
        }
    }

    companion object {
        val tag = AuthSSOProcessFragment::class.java.name
        private const val ARGUMENT_HOSTNAME = "hostname"

        fun newInstanceByTemplate(b: Bundle): AuthSSOProcessFragment {
            return AuthSSOProcessFragment().apply {
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

        fun hostname(hostname: String): Builder {
            extraConfiguration.putString(ARGUMENT_HOSTNAME, hostname)
            return this
        }

        override fun createFragment(b: Bundle): Fragment? {
            return newInstanceByTemplate(b)
        }
    }
}
