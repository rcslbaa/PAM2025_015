package com.example.a015_projectakhir.viewmodel

import android.util.Log
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
                val data = mapOf("id_pesanan" to idPesanan, "status" to statusBaru)
                // Cukup panggil fungsinya. Jika tidak error, berarti sukses.
                apiService.updateStatus(data)
                onRefresh()
            } catch (e: Exception) {
                Log.e("LOGIN_VM", "Gagal update: ${e.message}")
            }
        }
    }
}