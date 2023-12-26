package com.spotify.quavergd06.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream
import java.util.Date
import java.util.Base64
class StringListWrapper(val list: ArrayList<String>)
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
    @TypeConverter
    fun arrayListToStringWrapper(images: StringListWrapper): String? {
        return images.list.joinToString(",")
    }

    @TypeConverter
    fun stringToArrayListWrapper(images: String): StringListWrapper {
        return StringListWrapper(ArrayList(images.split(",")))
    }

}
