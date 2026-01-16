package com.example.a015_projectakhir.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.a015_projectakhir.data.model.Layanan
import com.example.a015_projectakhir.data.model.Pesanan
import com.example.a015_projectakhir.data.network.ApiService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffScreen(apiService: ApiService, onLogout: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var listLayanan by remember { mutableStateOf<List<Layanan>>(emptyList()) }
    var listPesanan by remember { mutableStateOf<List<Pesanan>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableIntStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }

    var namaPelanggan by remember { mutableStateOf("") }
    var beratInput by remember { mutableStateOf("") }
    var selectedLayanan by remember { mutableStateOf<Layanan?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val totalHarga = remember(beratInput, selectedLayanan) {
        val berat = beratInput.toDoubleOrNull() ?: 0.0
        val harga = selectedLayanan?.harga ?: 0
        (berat * harga).toInt()
    }

    // Fungsi Refresh Data
    fun refreshData() {
        scope.launch {
            isLoading = true
            try {
                listLayanan = apiService.getLayanan()
                listPesanan = apiService.getPesanan()
            } catch (e: Exception) {
                Log.e("ERROR_STAFF", "Gagal load data: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { refreshData() }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("Panel Kasir Laundry", fontWeight = FontWeight.Bold) },
                    actions = {
                        IconButton(onClick = onLogout) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = Color.Red)
                        }
                    }
                )
                TabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = (selectedTab == 0), onClick = { selectedTab = 0 }) {
                        Text("Daftar Layanan", modifier = Modifier.padding(16.dp))
                    }
                    Tab(selected = (selectedTab == 1), onClick = { selectedTab = 1 }) {
                        Text("Riwayat Pesanan", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                onClick = {
                    namaPelanggan = ""
                    beratInput = ""
                    selectedLayanan = null
                    showDialog = true
                }
            ) { Icon(Icons.Default.Add, contentDescription = "Tambah") }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (isLoading && listLayanan.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                if (selectedTab == 0) {
                    // TAB LAYANAN
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(listLayanan) { item ->
                            ListItem(
                                headlineContent = { Text(item.nama_layanan, fontWeight = FontWeight.Bold) },
                                supportingContent = { Text("Rp ${item.harga} / ${item.satuan}") },
                                trailingContent = {
                                    Button(onClick = {
                                        selectedLayanan = item
                                        showDialog = true
                                    }) { Text("Pilih") }
                                }
                            )
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        }
                    }
                } else {
                    // TAB RIWAYAT
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(listPesanan) { pesanan ->
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(pesanan.nama_pelanggan, fontWeight = FontWeight.Bold)

                                        // PERBAIKAN: Menggunakan AssistChip untuk Update Status
                                        AssistChip(
                                            onClick = {
                                                if (pesanan.status == "Proses") {
                                                    scope.launch {
                                                        try {
                                                            val data = mapOf(
                                                                "id_pesanan" to pesanan.id_pesanan,
                                                                "status" to "Selesai"
                                                            )
                                                            val response = apiService.updateStatus(data)

                                                            if (response.status == "success") {
                                                                Toast.makeText(context, "Pesanan Selesai!", Toast.LENGTH_SHORT).show()
                                                                refreshData()
                                                            } else {
                                                                Toast.makeText(context, "Gagal: ${response.message}", Toast.LENGTH_SHORT).show()
                                                            }
                                                        } catch (e: Exception) {
                                                            Log.e("UPDATE_ERROR", "Pesan: ${e.message}")
                                                            Toast.makeText(context, "Kesalahan Koneksi", Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                }
                                            },
                                            label = {
                                                Text(
                                                    text = pesanan.status,
                                                    color = if (pesanan.status == "Proses") Color.Blue else Color(0xFF2E7D32)
                                                )
                                            },
                                            colors = AssistChipDefaults.assistChipColors(
                                                containerColor = if (pesanan.status == "Proses") Color(0xFFE3F2FD) else Color(0xFFE8F5E9)
                                            ),
                                            enabled = pesanan.status == "Proses"
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${pesanan.nama_layanan} (${pesanan.berat} kg)")
                                    Text("Total: Rp ${pesanan.total_harga}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    Text(pesanan.tanggal_masuk, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }

        // DIALOG INPUT
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Input Pesanan Baru") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = namaPelanggan,
                            onValueChange = { namaPelanggan = it },
                            label = { Text("Nama Pelanggan") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedLayanan?.nama_layanan ?: "Pilih Layanan",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Layanan") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                listLayanan.forEach { item ->
                                    DropdownMenuItem(
                                        text = { Text(item.nama_layanan) },
                                        onClick = {
                                            selectedLayanan = item
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = beratInput,
                            onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) beratInput = it },
                            label = { Text("Berat / Jumlah") },
                            suffix = { Text(selectedLayanan?.satuan ?: "kg") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (selectedLayanan != null) {
                            Text(
                                "Total: Rp $totalHarga",
                                modifier = Modifier.padding(top = 8.dp),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        enabled = (namaPelanggan.isNotBlank() && selectedLayanan != null && beratInput.isNotBlank()),
                        onClick = {
                            val currentLayanan = selectedLayanan ?: return@Button
                            scope.launch {
                                try {
                                    val data = mapOf(
                                        "nama_pelanggan" to namaPelanggan,
                                        "id_layanan" to currentLayanan.id_layanan,
                                        "berat" to (beratInput.toDoubleOrNull() ?: 0.0),
                                        "total_harga" to totalHarga
                                    )
                                    val res = apiService.addPesanan(data)

                                    if (res.status == "success") {
                                        Toast.makeText(context, "Pesanan Berhasil Disimpan", Toast.LENGTH_SHORT).show()
                                        showDialog = false
                                        refreshData()
                                        selectedTab = 1
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Kesalahan Jaringan", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    ) { Text("Simpan Transaksi") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Batal") }
                }
            )
        }
    }
}