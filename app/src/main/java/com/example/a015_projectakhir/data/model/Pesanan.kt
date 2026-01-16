package com.example.a015_projectakhir.data.model

data class Pesanan(
    val id_pesanan: Int,
    val nama_pelanggan: String,
    val nama_layanan: String,
    val berat: Double,
    val total_harga: Int,
    val status: String,
    val tanggal_masuk: String
)