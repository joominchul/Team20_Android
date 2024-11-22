package com.example.potatoservice

import android.content.Intent
import android.content.pm.PackageManager // PackageManager import 추가
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.potatoservice.databinding.ActivitySplashBinding
import com.example.potatoservice.ui.sign.SignInActivity
import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginImage.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}
