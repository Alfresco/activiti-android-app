package com.activiti.android.app.activity

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import androidx.core.animation.addListener
import androidx.fragment.app.FragmentActivity
import com.activiti.android.app.R
import com.activiti.android.platform.account.ActivitiAccountManager
import kotlinx.android.synthetic.main.fragment_splash.*


/**
 * Created by Bogdan Roatis on 11/6/2019.
 */
class SplashActivity : FragmentActivity(R.layout.fragment_splash) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_splash)

        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                ivLogo,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f))
        scaleDown.duration = 1310
        scaleDown.addListener(onEnd = {
            if (!ActivitiAccountManager.getInstance(this).hasAccount()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }

            finish()
        })
        scaleDown.start()
    }
}
