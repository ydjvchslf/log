package com.example.memolog

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
fun getCurrentTime():String {
    return LocalDateTime.now().toString()
}