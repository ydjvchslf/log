package com.example.memolog

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
val currentDate: String = LocalDateTime.now().toString()