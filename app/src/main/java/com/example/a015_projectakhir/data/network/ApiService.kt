package com.example.a015_projectakhir.data.network

import com.example.a015_projectakhir.data.model.Layanan
import com.example.a015_projectakhir.data.model.Pesanan
import com.example.a015_projectakhir.data.model.User
import okhttp3.ResponseBody
import retrofit2.http.*

interface ApiService {

    // 1. AUTHENTICATION
    @POST("login")
    suspend fun login(@Body loginData: Map<String, String>): LoginResponse

    // 2. MASTER LAYANAN (ADMIN)
    @GET("get_layanan")
    suspend fun getLayanan(): List<Layanan>

    // Menggunakan ResponseBody agar tidak perlu file GenericResponse
    @POST("add_layanan")
    suspend fun addLayanan(@Body data: Map<String, @JvmSuppressWildcards Any>): ResponseBody

    @POST("update_layanan")
    suspend fun updateLayanan(@Body data: Map<String, @JvmSuppressWildcards Any>): ResponseBody

    @HTTP(method = "DELETE", path = "delete_layanan", hasBody = true)
    suspend fun deleteLayanan(@Body data: Map<String, Int>): ResponseBody

    // 3. TRANSAKSI & PESANAN (STAFF)
    @GET("get_pesanan")
    suspend fun getPesanan(): List<Pesanan>

    @POST("add_pesanan")
    suspend fun addPesanan(@Body data: Map<String, @JvmSuppressWildcards Any>): ResponseBody

    @POST("update_status_pesanan")
    suspend fun updateStatus(@Body data: Map<String, @JvmSuppressWildcards Any>): ResponseBody

    @HTTP(method = "DELETE", path = "delete_pesanan", hasBody = true)
    suspend fun deletePesanan(@Body data: Map<String, @JvmSuppressWildcards Any>): ResponseBody
}

data class LoginResponse(
    val status: String,
    val message: String,
    val data: User?
)