package com.nevmem.qms.keyvalue

import android.content.SharedPreferences
import com.nevmem.qms.keyvalue.impl.prefs.PrefsKeyValueStorage

fun createKeyValueStorage(prefs: SharedPreferences): KeyValueStorage = PrefsKeyValueStorage(prefs)
