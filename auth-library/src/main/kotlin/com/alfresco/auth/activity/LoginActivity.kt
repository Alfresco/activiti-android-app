package com.alfresco.auth.activity

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import com.alfresco.android.aims.R
import com.alfresco.auth.AuthConfig
import com.alfresco.auth.AuthType
import com.alfresco.auth.Credentials
import com.alfresco.auth.fragments.*
import com.alfresco.auth.ui.AuthenticationActivity
import com.alfresco.auth.ui.observe
import com.alfresco.common.getViewModel
import com.alfresco.ui.components.Snackbar


abstract class LoginActivity : AuthenticationActivity<LoginViewModel>() {

    override val viewModel: LoginViewModel by lazy {
        getViewModel {
            LoginViewModel.with(applicationContext)
        }
    }

    private lateinit var progressView: RelativeLayout

    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!resources.getBoolean(R.bool.isTablet)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        setContentView(R.layout.aims_activity_welcome)

        setupToolbar()

        progressView = findViewById(R.id.rlProgress)
        ViewCompat.setElevation(progressView, 10f)

        if (supportFragmentManager.findFragmentByTag(WelcomeFragment.TAG) == null) {
            WelcomeFragment.with(this).replace()
        }

        observe(viewModel.hasNavigation, ::onNavigation)
        observe(viewModel.isLoading, ::onLoading)

        observe(viewModel.onAuthType, ::onAuthType)
        observe(viewModel.startSSO, ::login)

        observe(viewModel.onShowHelp, ::showHelp)
        observe(viewModel.onShowSettings, ::showSettings)
    }

    private fun setupToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {

            R.id.aims_save_settings -> return false

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun onLoading(isLoading: Boolean) {
        progressView.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun onNavigation(hasNavigation: Boolean) {
        if (hasNavigation) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
            toolbar.setNavigationOnClickListener { onBackPressed() }

        } else {
            toolbar.setNavigationIcon(null)
            toolbar.setNavigationOnClickListener(null)
        }
    }

    abstract fun onCredentials(credentials: Credentials, endpoint: String, authConfig: AuthConfig)

    override fun onCredentials(credentials: Credentials) {
        onCredentials(credentials, viewModel.getApplicationServiceUrl(), viewModel.authConfig)
    }

    override fun onError(error: String) {
        // Hide progress view on error
        viewModel.isLoading.value = false

        val parentLayout: View = findViewById(android.R.id.content);
        Snackbar.make(parentLayout,
                Snackbar.STYLE_ERROR,
                resources.getString(R.string.auth_error_title),
                error,
                Snackbar.LENGTH_LONG).show()
    }

    protected fun onError(@StringRes messageResId: Int) {
        onError(resources.getString(messageResId))
    }

    override fun onAuthType(type: AuthType) {
        viewModel.isLoading.value = false

        when (type) {
            AuthType.SSO -> {
                SsoAuthFragment.with(this).display()
            }

            AuthType.BASIC -> {
                BasicAuthFragment.with(this).display()
            }

            AuthType.UNKNOWN -> {
                onError(R.string.auth_error_check_connect_url)
            }
        }
    }

    private fun showSettings(ignored: Int) {
        AdvancedSettingsFragment.with(this).display()
    }

    private fun showHelp(@StringRes msgResId: Int) {
        HelpFragment.with(this).message(msgResId).show()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        // Dismiss keyboard on touches outside editable fields
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}
