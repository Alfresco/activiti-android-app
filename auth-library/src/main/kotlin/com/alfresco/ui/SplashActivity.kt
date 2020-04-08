package com.alfresco.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.alfresco.android.aims.R

abstract class SplashActivity: AppCompatActivity() {

    private val handler = Handler()
    private lateinit var logoImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.alfresco_splash_activity)

        logoImageView = findViewById(R.id.ivLogo)
    }

    override fun onResume() {
        super.onResume()

        logoImageView.apply {
            animate()
                    .scaleX(1.16f)
                    .scaleY(1.16f)
                    .setDuration(ANIMATION_DURATION)
                    .setInterpolator(DecelerateInterpolator())
                    .setListener(null)
        }

        handler.postDelayed({
            goToMain()
        }, DISPLAY_TIMEOUT)
    }

    private fun goToMain() {
        val i = getMainIntent()
        startActivity(i)
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out)
    }

    abstract fun getMainIntent(): Intent

    companion object {
        private const val ANIMATION_DURATION = 2000L
        private const val DISPLAY_TIMEOUT = 2200L
    }
}