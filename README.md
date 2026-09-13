# 🧠 Memorise

**Memorise** adalah aplikasi Android untuk membuat dan mempelajari flashcard secara efisien. Materi dapat ditambahkan secara manual atau otomatis melalui AI — cukup memotret atau memindai dokumen, dan sistem akan menghasilkan draf flashcard secara otomatis. Materi diorganisir dalam folder dan deck, kemudian pemahaman dapat diuji melalui fitur quiz interaktif.

**Fitur utama**
- 📁 Folder & deck untuk mengorganisir materi belajar
- ✍️ Pembuatan card secara manual maupun otomatis via AI (scan kamera → draf card)
- 🧩 Quiz untuk latihan dan evaluasi pemahaman
- 🔐 Manajemen akun lengkap — registrasi, verifikasi OTP/email, reset password
- 👤 Profil dan pengaturan akun pengguna

Dibangun menggunakan Kotlin dan Jetpack Compose, serta terhubung ke backend REST API yang di-hosting di Railway.

## Instalasi

### Prasyarat
- [Android Studio](https://developer.android.com/studio) versi terbaru (mendukung AGP 8.13.1 / compileSdk 36)
- JDK 11
- Android SDK Platform 36 (akan ditawarkan otomatis oleh Android Studio saat project dibuka)
- Koneksi internet aktif — aplikasi terhubung langsung ke backend production, belum mendukung mode offline

### Langkah-langkah

1. Clone repository:
   ```bash
   git clone https://github.com/reynardwijaya/Memorise.git
   ```
2. Buka folder hasil clone menggunakan **Android Studio**.
3. Tunggu proses **Gradle Sync** selesai — seluruh dependency akan terunduh otomatis.
4. Pilih target perangkat (emulator dengan API level 27+ atau perangkat fisik) pada toolbar Android Studio.
5. Jalankan aplikasi dengan menekan tombol **Run ▶**.

Tidak diperlukan konfigurasi API key atau file environment tambahan — base URL backend sudah terkonfigurasi di dalam kode.

Jika ingin build melalui terminal tanpa membuka Android Studio:
```bash
./gradlew assembleDebug
```
Berkas APK hasil build dapat ditemukan pada `app/build/outputs/apk/debug/`.

---
Untuk penjelasan lebih mendalam mengenai arsitektur dan struktur kode, lihat [DOCUMENTATION.md](DOCUMENTATION.md).
