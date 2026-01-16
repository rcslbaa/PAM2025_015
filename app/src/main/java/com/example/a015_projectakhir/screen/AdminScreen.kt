package com.example.a015_projectakhir.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.a015_projectakhir.data.model.Layanan
import com.example.a015_projectakhir.data.network.ApiService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(apiService: ApiService, onLogout: () -> Unit) {
    var listLayanan by remember { mutableStateOf(listOf<Layanan>()) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // State untuk Dialog Input (Tambah/Edit)
    var showDialog by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var selectedId by remember { mutableStateOf(0) }

    // State untuk Dialog Konfirmasi Hapus
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<Layanan?>(null) }

    var namaInput by remember { mutableStateOf("") }
    var hargaInput by remember { mutableStateOf("") }
    var satuanInput by remember { mutableStateOf("") }

    // Fungsi untuk mengambil data terbaru
    fun refreshData() {
        scope.launch {
            try {
                listLayanan = apiService.getLayanan()
            } catch (e: Exception) {
                Log.e("ERROR_LOAD", "Gagal load data: ${e.message}")
            }
        }
    }

    LaunchedEffect(Unit) { refreshData() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Panel Admin", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, "Logout", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                onClick = {
                    isEditMode = false
                    namaInput = ""; hargaInput = ""; satuanInput = ""
                    showDialog = true
                }
            ) { Icon(Icons.Default.Add, "Tambah") }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                "Manajemen Harga & Layanan",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.outline
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(listLayanan) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.nama_layanan, fontWeight = FontWeight.Bold)
                                Text("Rp ${item.harga} / ${item.satuan}")
                            }

                            IconButton(onClick = {
                                selectedId = item.id_layanan
                                namaInput = item.nama_layanan
                                hargaInput = item.harga.toString()
                                satuanInput = item.satuan.replace("kg", "")
                                isEditMode = true
                                showDialog = true
                            }) { Icon(Icons.Default.Edit, "Edit", tint = Color(0xFFFFA500)) }

                            IconButton(onClick = {
                                itemToDelete = item
                                showDeleteConfirm = true
                            }) { Icon(Icons.Default.Delete, "Hapus", tint = Color.Red) }
                        }
                    }
                }
            }
        }

        // --- 1. DIALOG KONFIRMASI HAPUS (LOGIKA TERBARU) ---
        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Hapus Layanan?") },
                text = { Text("Apakah Anda yakin ingin menghapus '${itemToDelete?.nama_layanan}'?") },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        onClick = {
                            scope.launch {
                                try {
                                    val idHapus = itemToDelete?.id_layanan ?: 0
                                    Log.d("DEBUG_HAPUS", "Mengirim ID: $idHapus")

                                    val data = mapOf("id_layanan" to idHapus)
                                    apiService.deleteLayanan(data)

                                    showDeleteConfirm = false
                                    refreshData()
                                    snackbarHostState.showSnackbar("Layanan berhasil dihapus")
                                } catch (e: Exception) {
                                    Log.e("ERROR_HAPUS", "Pesan: ${e.message}")
                                    snackbarHostState.showSnackbar("Gagal: Terjadi kesalahan koneksi")
                                }
                            }
                        }
                    ) { Text("Hapus", color = Color.White) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) { Text("Batal") }
                }
            )
        }

        // --- 2. DIALOG TAMBAH/EDIT ---
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text(if (isEditMode) "Edit Layanan" else "Tambah Layanan") },
                text = {
                    Column {
                        OutlinedTextField(value = namaInput, onValueChange = { namaInput = it }, label = { Text("Nama") }, modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = hargaInput, onValueChange = { if (it.all { c -> c.isDigit() }) hargaInput = it }, label = { Text("Harga") }, prefix = { Text("Rp ") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(value = satuanInput, onValueChange = { if (it.all { c -> c.isDigit() }) satuanInput = it }, label = { Text("Satuan") }, suffix = { Text("kg") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        scope.launch {
                            try {
                                val hargaInt = hargaInput.toIntOrNull() ?: 0
                                val satuanFinal = if (satuanInput.isEmpty()) "kg" else "${satuanInput}kg"
                                val data = mutableMapOf<String, Any>(
                                    "nama_layanan" to namaInput,
                                    "harga" to hargaInt,
                                    "satuan" to satuanFinal
                                )
                                if (isEditMode) {
                                    data["id_layanan"] = selectedId
                                    apiService.updateLayanan(data)
                                } else {
                                    apiService.addLayanan(data)
                                }
                                showDialog = false
                                refreshData()
                                snackbarHostState.showSnackbar("Data berhasil disimpan")
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Gagal menyimpan data")
                            }
                        }
                    }) { Text("Simpan") }
                },
                dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Batal") } }
            )
        }
    }
}