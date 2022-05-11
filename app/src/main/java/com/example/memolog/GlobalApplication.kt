package com.example.memolog

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.memolog.repository.StringListTypeConverter
import com.example.memolog.repository.db.AppDatabase
import com.google.gson.Gson

class GlobalApplication: Application() {
    companion object{
        lateinit var appInstance: GlobalApplication
            private set

        lateinit var databaseInstance: AppDatabase
            private set

        fun provideGson(): Gson {
            return Gson()
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE 'memo' ADD COLUMN 'image' TEXT NOT NULL DEFAULT ''")
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        appInstance = this

        databaseInstance = Room.databaseBuilder(
            appInstance.applicationContext,
            AppDatabase::class.java, "exampleApp.db"
        )
            //.fallbackToDestructiveMigration() // 이전에 생성한 데이터베이스를 삭제한 후 새로운 데이터베이스를 생성
            .allowMainThreadQueries()
            .addTypeConverter(StringListTypeConverter(provideGson()))
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}