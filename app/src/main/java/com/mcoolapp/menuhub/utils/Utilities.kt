package com.mcoolapp.menuhub.utils

import android.content.Context
import android.net.ConnectivityManager

fun Context.isConnectedToNetwork(): Boolean {
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting() ?: false
}