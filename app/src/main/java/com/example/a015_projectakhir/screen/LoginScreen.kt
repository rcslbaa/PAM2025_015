package com.example.a015_projectakhir.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.a015_projectakhir.data.network.ApiService
import com.example.a015_projectakhir.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    apiService: ApiService,
    onNavigateToAdmin: () -> Unit,
    onNavigateToStaff: () -> Unit
) {
    // Inisialisasi ViewModel secara manual menggunakan Factory agar bisa memasukkan apiService
    val loginViewModel: LoginViewModel = viewModel(
        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(apiService) as T
            }
        }
    )

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Laundry App",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Silakan masuk ke akun Anda",
            color = MaterialTheme.colorScheme.outline,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Input Username
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Password
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true
        )

        // Menampilkan Pesan Error dari ViewModel
        if (loginViewModel.loginStatus.isNotEmpty()) {
            Text(
                text = loginViewModel.loginStatus,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tombol Login
        Button(
            onClick = {
                if (username.isNotEmpty() && password.isNotEmpty()) {
                    loginViewModel.loginUser(username, password) { role ->
                        if (role.lowercase() == "admin") {
                            onNavigateToAdmin()
                        } else {
                            onNavigateToStaff()
                        }
                    }
                } else {
                    loginViewModel.loginStatus = "Username & Password wajib diisi!"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Login")
        }
    }
}