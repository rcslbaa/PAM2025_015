# 🧺 White Glove Laundry Service App

Aplikasi manajemen operasional laundry berbasis Android yang dirancang untuk mempermudah alur kerja antara **Admin** dan **Staff**. Aplikasi ini mendukung manajemen layanan, harga, dan pemantauan antrean transaksi secara real-time.

---

### 🔐 Autentikasi & Akses
Halaman masuk yang membedakan hak akses antara Administrator (manajemen data) dan Staff (operasional harian).

| Tampilan Login |
| :---: |
| <img src="https://link-gambar-kamu-Screenshot_2026-01-17_172527.png" width="280"> |
| *Halaman Masuk Pengguna* |

---

### 👨‍💼 Panel Manajemen Admin
Admin memiliki kontrol penuh untuk mengelola daftar layanan laundry, termasuk penyesuaian harga dan satuan.

| Daftar Layanan | Tambah Layanan | Update Layanan | Konfirmasi Hapus |
| :---: | :---: | :---: | :---: |
| <img src="https://link-gambar-kamu-Screenshot_2026-01-17_171417.png" width="200"> | <img src="https://link-gambar-kamu-Screenshot_2026-01-17_171557.png" width="200"> | <img src="https://link-gambar-kamu-Screenshot_2026-01-17_171629.png" width="200"> | <img src="https://link-gambar-kamu-Screenshot_2026-01-17_171647.png" width="200"> |
| *Master Data Layanan* | *Input Layanan Baru* | *Form Edit Harga* | *Dialog Validasi* |

---

### 👔 Fitur Operasional Staff
Staff bertanggung jawab menangani transaksi pelanggan mulai dari pemilihan jenis cuci hingga pemantauan status pengerjaan.

| Pilih Layanan | Transaksi Baru | Antrean Proses | Detail Antrean |
| :---: | :---: | :---: | :---: |
| <img src="https://link-gambar-kamu-Screenshot_2026-01-17_171828.png" width="200"> | <img src="https://link-gambar-kamu-Screenshot_2026-01-17_171915.png" width="200"> | <img src="https://link-gambar-kamu-Screenshot_2026-01-17_171932.png" width="200"> | <img src="https://link-gambar-kamu-Screenshot_2026-01-17_171951.png" width="200"> |
| *Katalog Layanan* | *Form Pelanggan* | *Monitoring Antrean* | *Status Pembayaran* |

---

### 🔍 Manajemen Status & Riwayat
Fitur untuk menandai pesanan yang sudah selesai atau menghapus data transaksi jika terjadi kesalahan input.

| Pesanan Selesai | Hapus Transaksi |
| :---: | :---: |
| <img src="https://link-gambar-kamu-Screenshot_2026-01-17_171951.png" width="280"> | <img src="https://link-gambar-kamu-Screenshot_2026-01-17_172009.png" width="280"> |
| *Status: Selesai* | *Dialog Hapus Data* |

---

## 🛠️ Tech Stack
* **UI Framework:** Jetpack Compose (Modern Android UI)
* **Language:** Kotlin
* **Networking:** Retrofit 2 (REST API)
* **Local Image Handling:** Coil
* **Backend:** PHP & MySQL

## ⚠️ Troubleshooting (Common Error)
Jika muncul error **"Redeclaration"** pada `GenericResponse.kt`, pastikan tidak ada data class ganda di dalam folder `data/model`. Pastikan struktur folder bersih seperti pada gambar berikut:

| Struktur Project | Error Build Output |
| :---: | :---: |
| <img src="https://link-gambar-kamu-image_ae04e0.png" width="250"> | <img src="https://link-gambar-kamu-image_ae0405.jpg" width="400"> |

---
*Dibuat untuk Project Akhir Pemrograman Mobile - 2026*
