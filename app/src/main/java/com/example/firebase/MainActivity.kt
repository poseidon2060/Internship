package com.example.firebase

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnFirebase.setOnClickListener {
            startActivity(Intent(this@MainActivity, FirebaseActivity::class.java))
        }

        btnSharedPref.setOnClickListener {
            startActivity(Intent(this@MainActivity, SharedPreferencesActivity::class.java))
        }
    }
}