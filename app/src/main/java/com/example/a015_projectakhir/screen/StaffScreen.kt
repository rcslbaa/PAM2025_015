package com.example.a015_projectakhir.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a015_projectakhir.data.model.Layanan
import com.example.a015_projectakhir.data.model.Pesanan
import com.example.a015_projectakhir.data.network.ApiService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffScreen(apiService: ApiService, onLogout: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val PrimaryBlue = Color(0xFF0061A4)
    val BgColor = Color(0xFFF8FAFC)
    val AccentColor = Color(0xFF6200EE)

    // --- STATE LOGIC (TETAP SAMA) ---
    var listLayanan by remember { mutableStateOf<List<Layanan>>(emptyList()) }
    var listPesanan by remember { mutableStateOf<List<Pesanan>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }

    var showDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf<Pesanan?>(null) }

    var namaPelanggan by remember { mutableStateOf("") }
    var beratInput by remember { mutableStateOf("") }
    var selectedLayanan by remember { mutableStateOf<Layanan?>(null) }
    var expanded by remember { mutableStateOf(false) }

    val totalHarga = remember(beratInput, selectedLayanan) {
        val berat = beratInput.toDoubleOrNull() ?: 0.0
        val harga = selectedLayanan?.harga ?: 0
        (berat * harga).toInt()
    }

    fun refreshData() {
        scope.launch {
            isLoading = true
            try {
                listLayanan = apiService.getLayanan()
                listPesanan = apiService.getPesanan().sortedByDescending { it.id_pesanan }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Gagal load: ${e.message}")
            } finally { isLoading = false }
        }
    }

    LaunchedEffect(Unit) { refreshData() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("White Glove Staff", fontWeight = FontWeight.Black) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White, titleContentColor = PrimaryBlue),
                actions = {
                    IconButton(onClick = onLogout) { Icon(Icons.Default.ExitToApp, "Logout", tint = Color.Red) }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    label = { Text("Layanan") },
                    icon = { Icon(Icons.Default.List, null) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryBlue, selectedTextColor = PrimaryBlue)
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    label = { Text("Antrean") },
                    icon = { Icon(Icons.Default.DateRange, null) },
                    colors = NavigationBarItemDefaults.colors(selectedIconColor = PrimaryBlue, selectedTextColor = PrimaryBlue)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    namaPelanggan = ""; beratInput = ""; selectedLayanan = null; showDialog = true
                },
                containerColor = PrimaryBlue, contentColor = Color.White, shape = CircleShape
            ) { Icon(Icons.Default.Add, "Tambah") }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(BgColor)) {

            if (isLoading) LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = PrimaryBlue)

            Box(modifier = Modifier.weight(1f)) {
                if (selectedTab == 0) {
                    // --- TAB LAYANAN ---
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(listLayanan) { item ->
                            Card(
                                onClick = { selectedLayanan = item; showDialog = true },
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                ListItem(
                                    headlineContent = { Text(item.nama_layanan, fontWeight = FontWeight.Bold) },
                                    supportingContent = { Text("Rp ${item.harga} / ${item.satuan}") },
                                    leadingContent = { Icon(Icons.Default.ShoppingCart, null, tint = PrimaryBlue) }
                                )
                            }
                        }
                    }
                } else {
                    // --- TAB ANTREAN ---
                    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(listPesanan) { pesanan ->
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text(pesanan.nama_pelanggan, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Badge(containerColor = if(pesanan.status == "Proses") Color(0xFFFFEBEE) else Color(0xFFE8F5E9)) {
                                                Text(pesanan.status, color = if(pesanan.status == "Proses") Color.Red else Color(0xFF2E7D32), modifier = Modifier.padding(4.dp))
                                            }
                                            if (pesanan.status == "Selesai") {
                                                IconButton(onClick = { showDeleteConfirm = pesanan }) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Red)
                                                }
                                            }
                                        }
                                    }
                                    Text(pesanan.tanggal_masuk, fontSize = 12.sp, color = Color.Gray)
                                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                    Text("${pesanan.nama_layanan} (${pesanan.berat} kg)")
                                    Text("Total: Rp ${pesanan.total_harga}", fontWeight = FontWeight.Black, color = AccentColor)

                                    if (pesanan.status == "Proses") {
                                        Button(
                                            onClick = {
                                                scope.launch {
                                                    try {
                                                        val updateMap = mapOf(
                                                            "id_pesanan" to pesanan.id_pesanan.toInt(),
                                                            "status" to "Selesai"
                                                        )
                                                        apiService.updateStatus(updateMap)
                                                        Toast.makeText(context, "Pesanan Selesai!", Toast.LENGTH_SHORT).show()
                                                        refreshData()
                                                    } catch (e: Exception) {
                                                        Log.e("UPDATE_ERROR", "${e.message}")
                                                        Toast.makeText(context, "Gagal Update", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                        ) {
                                            Icon(Icons.Default.Check, null)
                                            Spacer(Modifier.width(8.dp))
                                            Text("Selesaikan Pesanan")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- DIALOG TAMBAH PESANAN (TETAP SAMA) ---
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Transaksi Baru", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(value = namaPelanggan, onValueChange = { namaPelanggan = it }, label = { Text("Nama Pelanggan") }, modifier = Modifier.fillMaxWidth())
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                            OutlinedTextField(
                                value = selectedLayanan?.nama_layanan ?: "Pilih Layanan",
                                onValueChange = {}, readOnly = true, label = { Text("Layanan") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                listLayanan.forEach { item ->
                                    DropdownMenuItem(text = { Text(item.nama_layanan) }, onClick = { selectedLayanan = item; expanded = false })
                                }
                            }
                        }
                        OutlinedTextField(
                            value = beratInput, onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) beratInput = it },
                            label = { Text("Berat/Jumlah") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                        )
                        Text("Total: Rp $totalHarga", fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val currentLayanan = selectedLayanan ?: return@Button
                        scope.launch {
                            try {
                                apiService.addPesanan(mapOf(
                                    "nama_pelanggan" to namaPelanggan,
                                    "id_layanan" to currentLayanan.id_layanan,
                                    "berat" to (beratInput.toDoubleOrNull() ?: 0.0),
                                    "total_harga" to totalHarga
                                ))
                                Toast.makeText(context, "Berhasil Simpan!", Toast.LENGTH_SHORT).show()
                                showDialog = false
                                refreshData()
                                selectedTab = 1
                            } catch (e: Exception) {
                                Toast.makeText(context, "Gagal Simpan", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }, enabled = namaPelanggan.isNotBlank() && beratInput.isNotBlank()) { Text("Konfirmasi") }
                },
                dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Batal") } }
            )
        }

        // --- DIALOG HAPUS (TETAP SAMA) ---
        showDeleteConfirm?.let { pesanan ->
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = null },
                title = { Text("Hapus Data") },
                text = { Text("Hapus data pesanan ${pesanan.nama_pelanggan}?") },
                confirmButton = {
                    Button(
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        onClick = {
                            scope.launch {
                                try {
                                    apiService.deletePesanan(mapOf("id_pesanan" to pesanan.id_pesanan.toInt()))
                                    Toast.makeText(context, "Dihapus", Toast.LENGTH_SHORT).show()
                                    refreshData()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Gagal Hapus", Toast.LENGTH_SHORT).show()
                                } finally {
                                    showDeleteConfirm = null
                                }
                            }
                        }
                    ) { Text("Hapus", color = Color.White) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = null }) { Text("Batal") }
                }
            )
        }
    }
}