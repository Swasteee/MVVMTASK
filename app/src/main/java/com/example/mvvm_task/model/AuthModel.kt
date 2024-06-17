package com.example.mvvm_kotlin_assignment.model

import android.os.Parcel
import android.os.Parcelable

data class AuthModel(
    val userId: String = "",
    val password: String = ""
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString()?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(userId)
        parcel.writeString(password)
    }
    override fun describeContents(): Int {
        return 0
    }


    companion object CREATOR : Parcelable.Creator<AuthModel> {
        override fun createFromParcel(parcel: Parcel): AuthModel {
            return AuthModel(parcel)
        }

        override fun newArray(size: Int): Array<AuthModel?> {
            return arrayOfNulls(size)
        }
    }

}