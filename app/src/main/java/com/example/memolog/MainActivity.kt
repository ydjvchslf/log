package com.example.memolog

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.memolog.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textView.text = "바꿔주세요오오옹~"
    }
}