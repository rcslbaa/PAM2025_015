package com.example.a015_projectakhir.data.model

import com.google.gson.annotations.SerializedName

data class Layanan(
    @SerializedName("id_layanan")
    val id_layanan: Int,

    @SerializedName("nama_layanan")
    val nama_layanan: String,

    @SerializedName("harga")
    val harga: Int,

    @SerializedName("satuan")
    val satuan: String
)

// Untuk respon standar (berhasil/gagal) dari API add/update/delete
data class GenericResponse(
    @SerializedName("status")
    val status: String,

    @SerializedName("message")
    val message: String
)