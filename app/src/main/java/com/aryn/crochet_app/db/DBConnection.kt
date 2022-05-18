package com.aryn.crochet_app.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aryn.crochet_app.db.entity.Part
import com.aryn.crochet_app.db.entity.Project

@Database(entities = [Project::class, Part::class], version = 9, exportSchema = false)
abstract class DBConnection : RoomDatabase() {
    abstract val databaseDAO: DAO

    //allows clients to access methods for creating or getting the DB without instantiating the class
    companion object {

        //starts at null, once there is one, instance will keep track of it
        //Volatile variables will never be cached, changes by one thread are visible immediatly to all other threads
        @Volatile
        private var INSTANCE: DBConnection? = null

        fun getInstance(context: Context): DBConnection {
            //only one thread can open this DB at a time, makes it only initialised once
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    //string is the database name

                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        DBConnection::class.java,
                        "crochet"
                    )
                        .fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                    //TODO .allowMainTread is a workaround as I didnt want to do corountines
                    ///https://stackoverflow.com/questions/44167111/android-room-simple-select-query-cannot-access-database-on-the-main-thread/47773708

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}