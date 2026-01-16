package com.example.a015_projectakhir.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.a015_projectakhir.data.network.ApiService
import kotlinx.coroutines.launch

class LoginViewModel(private val apiService: ApiService) : ViewModel() {

    var loginStatus by mutableStateOf("")
    var isLoading by mutableStateOf(false)

    fun loginUser(username: String, password: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                // DATA DIRUBAH: Menggunakan Map untuk dikirim sebagai JSON Body
                val data = mapOf("username" to username, "password" to password)

                // Memanggil apiService dengan objek Map
                val response = apiService.login(data)

                if (response.status == "success") {
                    loginStatus = ""
                    onSuccess(response.data?.role ?: "staff")
                } else {
                    loginStatus = response.message
                }
            } catch (e: Exception) {
                loginStatus = "Kesalahan Koneksi: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // FUNGSI TAMBAHAN: Untuk update status pesanan (bisa ditaruh di sini atau di StaffViewModel)
    fun updateStatusPesanan(idPesanan: Int, statusBaru: String, onRefresh: () -> Unit) {
        viewModelScope.launch {
            try {
                // DATA DIRUBAH: Menggunakan Map untuk JSON Body
                val data = mapOf("id_pesanan" to idPesanan, "status" to statusBaru)

                val response = apiService.updateStatus(data)
                if (response.status == "success") {
                    onRefresh() // Panggil refresh data setelah berhasil
                }
            } catch (e: Exception) {
                // Handle error update status jika perlu
            }
        }
    }
}