package com.example.memolog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.memolog.adapter.event.ImageEvent
import com.example.memolog.databinding.ActivityMainBinding
import org.greenrobot.eventbus.EventBus

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // navigation
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        val bottomNavigationView = binding.bottomNavi
        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        // custom toast
        //val layout = layoutInflater.inflate(R.layout.toast_layout, binding.root, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("MemoDebug", "MainActivity::onActivityResult-()")
        Log.d("MemoDebug", "MainActivity::requestCode: $requestCode, resultCode: $resultCode, data: $data")
        Log.d("MemoDebug", "MainActivity:: data?.clipData: ${data?.clipData}")

        EventBus.getDefault().post(ImageEvent(requestCode, resultCode, data))
    }
}