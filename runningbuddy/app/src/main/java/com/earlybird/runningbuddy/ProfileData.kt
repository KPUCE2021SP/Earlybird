package com.earlybird.runningbuddy

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class ProfileData(
    val date: String,
    val time: String,
    val distance: String,
)
