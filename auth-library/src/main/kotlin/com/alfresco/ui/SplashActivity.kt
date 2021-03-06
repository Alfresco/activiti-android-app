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

        setContentView(R.layout.activity_alfresco_splash)

        logoImageView = findViewById(R.id.ivLogo)
    }

    override fun onPause() {
        super.onPause()

        // On configuration change and on background cancel previous handler
        handler.removeCallbacksAndMessages(null)
    }

    override fun onResume() {
        super.onResume()

        logoImageView.scaleX = 0.8f
        logoImageView.scaleY = 0.8f

        logoImageView.apply {
            animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(ANIMATION_DURATION)
                    .setInterpolator(DecelerateInterpolator())
                    .setListener(null)
        }

        // Transition after delay
        handler.postDelayed({
            goToMain()
        }, DISPLAY_TIMEOUT)
    }

    private fun goToMain() {
        val i = getMainIntent()
        startActivity(i)
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out)
        finish()
    }

    abstract fun getMainIntent(): Intent

    companion object {
        private const val ANIMATION_DURATION = 2000L
        private const val DISPLAY_TIMEOUT = 2200L
    }
}