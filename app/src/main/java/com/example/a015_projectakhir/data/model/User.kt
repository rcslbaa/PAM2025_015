package com.example.a015_projectakhir.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id_user")
    val id_user: Int,

    @SerializedName("username")
    val username: String,

    @SerializedName("role")
    val role: String
)