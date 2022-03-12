package com.example.memolog

import android.app.Application
import androidx.room.Room
import com.example.memolog.repository.db.Database

class GlobalApplication: Application() {
    companion object{
        lateinit var appInstance: GlobalApplication
            private set

        lateinit var databaseInstance: Database
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appInstance = this

        databaseInstance = Room.databaseBuilder(
            appInstance.applicationContext,
            Database::class.java, "database.db"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }
}