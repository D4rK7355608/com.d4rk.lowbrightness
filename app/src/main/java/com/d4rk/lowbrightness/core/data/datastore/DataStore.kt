package com.d4rk.lowbrightness.core.data.datastore

import android.content.Context
import com.d4rk.android.libs.apptoolkit.data.datastore.CommonDataStore

class DataStore(context : Context) : CommonDataStore(context) {

    companion object {
        @Volatile
        private var instance : DataStore? = null

        fun getInstance(context : Context) : DataStore {
            return instance ?: synchronized(lock = this) {
                instance ?: DataStore(context.applicationContext).also { instance = it }
            }
        }
    }
}