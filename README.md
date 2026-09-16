# 🧠 Memorise

Ubah foto catatan atau dokumen jadi flashcard siap belajar hanya dalam beberapa detik — Memorise memangkas waktu bikin bahan belajar manual, jadi kamu bisa fokus mengingat, bukan mengetik ulang.

**Fitur Utama**
- 📸 Scan catatan atau dokumen, AI langsung ubah jadi flashcard
- ✍️ Bikin & edit card manual kapan pun dibutuhkan
- 📁 Rapikan materi belajar dalam folder & deck
- 🧩 Uji pemahaman lewat quiz interaktif dengan skor & riwayat
- 🔐 Akun aman dengan verifikasi email/OTP dan reset password

## Tech Stack

| Layer | Teknologi |
|---|---|
| Bahasa | Kotlin 2.0.21 |
| UI | Jetpack Compose, Material3 |
| Dependency Injection | Hilt (Dagger 2.51) |
| Networking | Retrofit 2.9.0, OkHttp 4.12.0 |
| Navigation | Navigation Compose |
| Local Storage | EncryptedSharedPreferences, DataStore Preferences |
| Kamera | CameraX 1.3.1 |
| Backend | REST API (hosted di Railway) |
| Build Tool | Gradle 8.13, Android Gradle Plugin 8.13.1 |

## Environment Variables

Tidak ada environment variable yang perlu disetel di sisi aplikasi (client). Base URL backend sudah dikonfigurasi langsung di kode, dan tidak ada API key pihak ketiga yang diakses langsung dari client — kredensial backend (jika ada) dikelola di sisi server, bukan di repo ini.

## Cara Instalasi & Menjalankan

**Prasyarat**
- Android Studio (versi terbaru, mendukung AGP 8.13.1 / compileSdk 36)
- JDK 11
- Koneksi internet aktif (aplikasi langsung terhubung ke backend production)

```bash
# 1. Clone repository
git clone https://github.com/reynardwijaya/Memorise.git
cd Memorise
```

2. Buka folder project di Android Studio, tunggu **Gradle Sync** selesai.
3. Jalankan ke emulator (API 27+) atau device fisik lewat tombol **Run ▶**.

Build manual lewat terminal (opsional):
```bash
./gradlew assembleDebug
```
