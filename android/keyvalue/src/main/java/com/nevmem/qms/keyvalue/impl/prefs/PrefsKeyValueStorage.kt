package com.nevmem.qms.keyvalue.impl.prefs

import android.content.SharedPreferences
import com.nevmem.qms.keyvalue.KeyValueStorage

internal class PrefsKeyValueStorage(private val prefs: SharedPreferences) : KeyValueStorage {
    override fun hasKey(key: String): Boolean = prefs.contains(key)

    override fun getValue(key: String): String? {
        if (!hasKey(key)) {
            return null
        }
        return prefs.getString(key, null)!!
    }

    override fun getValue(key: String, notFound: String): String = prefs.getString(key, notFound)!!

    override fun setValue(key: String, value: String) {
        check(prefs.edit()
            .putString(key, value)
            .commit()) { "Commit to shared preferences not succeeded" }
    }

    override fun keys(): List<String> = prefs.all.keys.toList()
}
