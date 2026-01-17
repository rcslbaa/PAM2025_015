package com.example.a015_projectakhir.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a015_projectakhir.data.model.Layanan
import com.example.a015_projectakhir.data.network.ApiService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(apiService: ApiService, onLogout: () -> Unit) {
    var listLayanan by remember { mutableStateOf(listOf<Layanan>()) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Warna Tema Konsisten
    val PrimaryBlue = Color(0xFF0061A4)
    val BgColor = Color(0xFFF8FAFC)
    val OrangeEdit = Color(0xFFF57C00)

    var showDialog by remember { mutableStateOf(false) }
    var isEditMode by remember { mutableStateOf(false) }
    var selectedId by remember { mutableStateOf(0) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<Layanan?>(null) }

    var namaInput by remember { mutableStateOf("") }
    var hargaInput by remember { mutableStateOf("") }
    var satuanInput by remember { mutableStateOf("") }

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
                title = { Text("Panel Management Admin", fontWeight = FontWeight.Black) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, "Logout", tint = Color.Red)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = PrimaryBlue
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = CircleShape,
                onClick = {
                    isEditMode = false
                    namaInput = ""; hargaInput = ""; satuanInput = ""
                    showDialog = true
                }
            ) { Icon(Icons.Default.Add, "Tambah Layanan") }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(BgColor)
        ) {
            // Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Daftar Harga & Layanan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(listLayanan) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    item.nama_layanan,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = PrimaryBlue
                                )
                                Text(
                                    "Harga: Rp ${item.harga} / ${item.satuan}",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }

                            // Edit Button (Blue Elegant)
                            IconButton(
                                modifier = Modifier.background(PrimaryBlue.copy(alpha = 0.1f), CircleShape),
                                onClick = {
                                    selectedId = item.id_layanan
                                    namaInput = item.nama_layanan
                                    hargaInput = item.harga.toString()
                                    satuanInput = item.satuan.replace("kg", "").replace("pcs", "").trim()
                                    isEditMode = true
                                    showDialog = true
                                }) { Icon(Icons.Default.Edit, "Edit", tint = PrimaryBlue) }

                            Spacer(Modifier.width(8.dp))

                            // Delete Button (Soft Red)
                            IconButton(
                                modifier = Modifier.background(Color.Red.copy(alpha = 0.1f), CircleShape),
                                onClick = {
                                    itemToDelete = item
                                    showDeleteConfirm = true
                                }) { Icon(Icons.Default.Delete, "Hapus", tint = Color.Red) }
                        }
                    }
                }
            }
        }

        // --- DIALOGS (Tambah/Edit & Hapus) ---
        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                shape = RoundedCornerShape(24.dp),
                title = { Text("Konfirmasi Hapus") },
                text = { Text("Yakin ingin menghapus layanan '${itemToDelete?.nama_layanan}'? Tindakan ini tidak dapat dibatalkan.") },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = RoundedCornerShape(12.dp),
                        onClick = {
                            scope.launch {
                                try {
                                    apiService.deleteLayanan(mapOf("id_layanan" to (itemToDelete?.id_layanan ?: 0)))
                                    showDeleteConfirm = false
                                    refreshData()
                                    snackbarHostState.showSnackbar("Berhasil dihapus")
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Gagal menghapus data")
                                }
                            }
                        }
                    ) { Text("Hapus Permanen", color = Color.White) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) { Text("Batal") }
                }
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                shape = RoundedCornerShape(28.dp),
                title = {
                    Text(
                        if (isEditMode) "Perbarui Layanan" else "Tambah Layanan Baru",
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = namaInput,
                            onValueChange = { namaInput = it },
                            label = { Text("Nama Layanan") },
                            placeholder = { Text("Contoh: Cuci Kering") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = hargaInput,
                            onValueChange = { if (it.all { c -> c.isDigit() }) hargaInput = it },
                            label = { Text("Harga Per Satuan") },
                            prefix = { Text("Rp ") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = satuanInput,
                            onValueChange = { satuanInput = it },
                            label = { Text("Satuan (kg / pcs)") },
                            placeholder = { Text("kg") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(12.dp),
                        onClick = {
                            scope.launch {
                                try {
                                    val hargaInt = hargaInput.toIntOrNull() ?: 0
                                    val data = mutableMapOf<String, Any>(
                                        "nama_layanan" to namaInput,
                                        "harga" to hargaInt,
                                        "satuan" to satuanInput
                                    )
                                    if (isEditMode) {
                                        data["id_layanan"] = selectedId
                                        apiService.updateLayanan(data)
                                    } else {
                                        apiService.addLayanan(data)
                                    }
                                    showDialog = false
                                    refreshData()
                                    snackbarHostState.showSnackbar("Berhasil disimpan")
                                } catch (e: Exception) {
                                    snackbarHostState.showSnackbar("Gagal koneksi server")
                                }
                            }
                        }) { Text("Simpan Data") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Batal") }
                }
            )
        }
    }
}