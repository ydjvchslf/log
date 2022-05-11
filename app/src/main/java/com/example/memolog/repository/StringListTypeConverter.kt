package com.example.memolog.repository

import android.util.Log
import androidx.room.ProvidedTypeConverter
import com.google.gson.Gson

@ProvidedTypeConverter
class StringListTypeConverter(private val gson: Gson) {

    @androidx.room.TypeConverter
    fun listToJson(value: List<String>?): String? {
        return value?.let{ gson.toJson(value) }
    }

    @androidx.room.TypeConverter
    fun jsonToList(value: String?): List<String> {
        return if(value?.isEmpty() == true){ // "" 공백값
            Log.d("MemoDebug", "value==$value==")
            emptyList()
        }else{
            gson.fromJson(value, Array<String>::class.java).toList()
        }
    }

}