package com.hermeson.todolist

import android.app.Application
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import androidx.datastore.rxjava3.RxDataStore

class MyApplication: Application() {
    companion object {
        var dataStore: RxDataStore<Preferences>? = null
    }

    override fun onCreate() {
        super.onCreate()

        dataStore = RxPreferenceDataStoreBuilder(this,  /*name=*/"settings").build()
    }
}