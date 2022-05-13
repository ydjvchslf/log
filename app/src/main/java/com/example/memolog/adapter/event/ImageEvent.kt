package com.example.memolog.adapter.event

import android.content.Intent

data class ImageEvent(
    var requestCode: Int,
    var resultCode: Int,
    var data: Intent?
    ){
    constructor() : this(0,0, null)
}
