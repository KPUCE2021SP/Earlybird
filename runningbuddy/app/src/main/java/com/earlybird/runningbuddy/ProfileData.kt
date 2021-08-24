package com.earlybird.runningbuddy

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProfileData( val date : String, val information : Int, val img : Int) : Parcelable
