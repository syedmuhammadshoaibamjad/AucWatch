package com.shoaib.aucwatch.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.shoaib.aucwatch.R
import com.shoaib.aucwatch.ui.auth.LoginActivity


class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(mainLooper).postDelayed(
            {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }, 2000
        )
    }
}