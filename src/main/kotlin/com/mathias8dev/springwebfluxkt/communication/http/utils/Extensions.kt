package com.mathias8dev.springwebfluxkt.communication.http.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mathias8dev.springwebfluxkt.utils.SpringApplicationContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


fun <T> T.toRequestBody(): RequestBody {
    if (this is String) {
        val bytes = toByteArray(Charsets.UTF_8)
        return bytes.toRequestBody("text/plain; charset=utf-8".toMediaTypeOrNull(), 0, bytes.size)
    }
    if (this is File)
        return this.asRequestBody("application/octet-stream".toMediaType())
    return this.toGson().toRequestBody("application/json".toMediaType())
}


fun <T> T.toGson(): String {
    val gson = SpringApplicationContext.getBean(Gson::class.java)
    return if (this is String || this is CharSequence) this.toString()
    else gson.toJson(this)
}


inline fun <reified I, reified O> I.gconvert(): O {
    val gson: Gson = SpringApplicationContext.getBean(Gson::class.java)
    return gson.fromJson(this.toGson(), object : TypeToken<O>() {}.type)
}