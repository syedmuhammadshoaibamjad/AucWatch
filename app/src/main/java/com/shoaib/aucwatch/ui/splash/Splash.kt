package com.shoaib.aucwatch.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.shoaib.aucwatch.R
import com.shoaib.aucwatch.ui.main.MainActivity

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(mainLooper).postDelayed(
            {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 2000
        )
    }
}