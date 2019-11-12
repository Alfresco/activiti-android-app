package com.activiti.android.app.fragments.auth

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import com.activiti.android.app.R
import com.activiti.android.app.activity.MainActivity
import com.activiti.android.app.extensions.observe
import com.activiti.android.ui.fragments.builder.AlfrescoFragmentBuilder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_help.*
import kotlinx.android.synthetic.main.fragment_login_credentials.*
import kotlinx.android.synthetic.main.layout_loading.*

/**
 * Created by Bogdan Roatis on 10/29/2019.
 */
class AuthCredentialsFragment : Fragment(R.layout.fragment_login_credentials) {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private val mLoginUsernamePasswordViewModel: LoginUsernamePasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetBehavior = BottomSheetBehavior.from<View>(bottomSheetHelp)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        tvHelpTitle.text = Html.fromHtml(getString(R.string.auth_start_help_title))
        tvHelpSubTitle.text = Html.fromHtml(getString(R.string.auth_start_help_subtitle))
        tvHelpDescription.text = Html.fromHtml(getString(R.string.auth_start_help_description))

        btnHelpClose.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        btnNeedHelp.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        tvAlfrescoUrl.visibility = if (arguments?.getBoolean(ARGUMENT_HTTPS) == true) View.GONE else View.VISIBLE
        tvAlfrescoUrl.text = "Signing in to\n${arguments?.getString(ARGUMENT_HOSTNAME)}"

        btnConnect.setOnClickListener {
            tilUsername.error = null
            tilPassword.error = null

            mLoginUsernamePasswordViewModel.login(
                    tilUsername.editText?.text.toString(),
                    tilPassword.editText?.text.toString(),
                    arguments?.getString(ARGUMENT_HOSTNAME),
                    arguments?.getBoolean(ARGUMENT_HTTPS))
        }

        btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        mLoginUsernamePasswordViewModel.run {
            observe(onLoading, ::showLoading)
            observe(onResult, ::onLoginResult)
        }
    }

    private fun onLoginResult(connectResult: LoginResult) {
        connectResult.run {
            if (success) {
                startActivity(Intent(activity, MainActivity::class.java))
                activity!!.finish()
            } else {
                if (passwordError != null) {
                    tilPassword.error = getString(passwordError)
                }

                if (usernameError != null) {
                    tilUsername.error = getString(usernameError)
                }

                if (error != null) {
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        loadingContainer.visibility = if (show) View.VISIBLE else View.GONE
//        loginContainer.visibility = if (show) View.GONE else View.VISIBLE
    }

    companion object {
        val tag = AuthCredentialsFragment::class.java.name
        private const val ARGUMENT_HOSTNAME = "hostnameView"
        private const val ARGUMENT_HTTPS = "https"
        private const val ARGUMENT_IS_CLOUD = "isCloud"

        fun newInstanceByTemplate(b: Bundle): AuthCredentialsFragment {
            return AuthCredentialsFragment().apply {
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

        fun isCloud(isCloud: Boolean): Builder {
            extraConfiguration.putBoolean(ARGUMENT_IS_CLOUD, isCloud)
            return this
        }

        fun hostname(hostname: String): Builder {
            extraConfiguration.putString(ARGUMENT_HOSTNAME, hostname)
            return this
        }

        fun https(https: Boolean): Builder {
            extraConfiguration.putBoolean(ARGUMENT_HTTPS, https)
            return this
        }

        override fun createFragment(b: Bundle): Fragment? {
            return newInstanceByTemplate(b)
        }
    }
}
