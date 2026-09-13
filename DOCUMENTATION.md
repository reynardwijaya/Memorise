# Memorise — Dokumentasi Teknis

Dokumen ini menjelaskan arsitektur, struktur folder/file, dan cara setup project Memorise secara detail. Untuk penjelasan singkat & cara install cepat, lihat [README.md](README.md).

## Daftar Isi

1. [Tentang Project](#tentang-project)
2. [Tech Stack](#tech-stack)
3. [Instalasi & Setup Lengkap](#instalasi--setup-lengkap)
4. [Arsitektur](#arsitektur)
5. [Struktur Folder & Fungsi Tiap File](#struktur-folder--fungsi-tiap-file)
6. [Daftar Screen per Fitur](#daftar-screen-per-fitur)
7. [Catatan & Known Issues](#catatan--known-issues)

## Tentang Project

Memorise adalah aplikasi Android native untuk membuat dan belajar flashcard. User bisa:

- Membuat **folder** dan **deck** untuk mengorganisir materi.
- Menambahkan **card** secara manual, atau otomatis lewat **AI**: foto/scan dokumen via kamera (CameraX) → backend generate draft card → user review/edit sebelum disimpan.
- Mengerjakan **quiz** dari deck yang sudah dibuat untuk menguji pemahaman.
- Mengelola akun: sign up, verifikasi via OTP/email, login, lupa password, edit profil & password.

Repo ini hanya berisi **aplikasi client (Android)**. Backend (REST API) ada di repo/service terpisah, di-hosting di Railway.

## Tech Stack

| Layer | Teknologi |
|---|---|
| Bahasa | Kotlin 2.0.21 |
| UI | Jetpack Compose + Material3 (tidak ada XML layout untuk screen) |
| Dependency Injection | Hilt / Dagger 2.51 |
| Networking | Retrofit 2.9.0 + OkHttp 4.12.0 + Gson |
| Navigation | Navigation Compose |
| Local storage | `EncryptedSharedPreferences` (androidx.security), DataStore Preferences |
| Kamera | CameraX 1.3.1 |
| Image loading | Coil |
| Animasi | Lottie |
| Testing | JUnit, Espresso, MockWebServer, MockK, kotlinx-coroutines-test |
| Build tool | Gradle 8.13, Android Gradle Plugin 8.13.1 |
| SDK | minSdk 27, targetSdk 36, compileSdk 36, Java 11 |

Tidak ada database lokal (Room). Semua data deck/card/quiz/home datang dari backend; state lokal cuma dipakai untuk token, data user, dan avatar.

## Instalasi & Setup Lengkap

### Prasyarat
- Android Studio (versi yang support AGP 8.13.1 & compileSdk 36)
- JDK 11
- Android SDK Platform 36 (ditawarkan otomatis oleh Android Studio saat project dibuka)
- Koneksi internet — app langsung connect ke backend production, **tidak ada mode offline/mock**

### Langkah

```
git clone https://github.com/reynardwijaya/Memorise.git
cd Memorise
```

1. Buka folder project di Android Studio → tunggu Gradle sync (dependency di-download otomatis dari `gradle/libs.versions.toml`).
2. `local.properties` akan dibuat otomatis oleh Android Studio (isinya cuma path Android SDK) — **tidak perlu isi manual apapun**, tidak ada API key/secret yang wajib disetel karena base URL backend sudah hard-coded di kode (lihat [Catatan & Known Issues](#catatan--known-issues)).
3. Jalankan lewat tombol Run di Android Studio, pilih emulator (API 27+) atau device fisik.

Build manual via terminal (tanpa buka Android Studio):
```
./gradlew assembleDebug
```
APK hasil build ada di `app/build/outputs/apk/debug/`.

### Permission yang Dipakai
Didefinisikan di `app/src/main/AndroidManifest.xml`:
- `CAMERA` — untuk fitur scan dokumen ke AI card
- `INTERNET`, `ACCESS_NETWORK_STATE` — komunikasi ke backend

## Arsitektur

Project mengikuti pola **layered architecture** (mirip clean architecture versi ringan) dengan 3 layer utama:

```
UI (Compose Screen + ViewModel)
        ↓ panggil interface
domain/repository (kontrak/interface)
        ↑ diimplementasikan oleh
data/repository (implementasi nyata — panggil API)
        ↓ pakai
data/remote/api (Retrofit service)
```

- **ViewModel** di `ui/screen/.../SomeViewModel.kt` tidak pernah memanggil Retrofit langsung — selalu lewat `domain/repository` (interface), yang diinject via Hilt dan diimplementasikan oleh class di `data/repository/`.
- **DI (Hilt)** menyambungkan semuanya: `di/RepositoryModule.kt` bind interface repository ke implementasinya, `di/NetworkModule.kt` bikin instance Retrofit/OkHttp yang dipakai implementasi repository untuk hit API.
- Konversi bentuk data dari backend (DTO/JSON) ke model yang dipakai UI (domain model) dilakukan di `data/mapper/ContentMapper.kt`.

## Struktur Folder & Fungsi Tiap File

Root package: `app/src/main/java/com/mobile/memorise/`

```
com.mobile.memorise/
├── MemoriseApp.kt
├── data/
├── di/
├── domain/
├── navigation/
├── ui/
└── util/
```

### `MemoriseApp.kt`
Application class, anotasi `@HiltAndroidApp` — entry point yang menginisialisasi Hilt dependency graph saat app start.

### `data/` — Layer Data

| File/Folder | Fungsi |
|---|---|
| `local/token/TokenStore.kt` | Simpan & baca access/refresh token secara terenkripsi |
| `local/user/UserStore.kt` | Simpan data user yang sedang login (cache lokal) |
| `local/avatar/AvatarStore.kt` | Simpan URI avatar lokal (avatar tidak diupload ke backend, disimpan di device) |
| `mapper/ContentMapper.kt` | Mapping DTO (bentuk JSON backend) ⇄ model domain yang dipakai UI |
| `remote/AuthApi.kt` | Endpoint Retrofit untuk login, register |
| `remote/AuthInterceptor.kt` | OkHttp interceptor — otomatis nyisipin access token ke header tiap request |
| `remote/TokenAuthenticator.kt` | OkHttp authenticator — otomatis refresh token kalau request gagal karena token expired (401), lalu retry |
| `remote/api/ApiService.kt` | Endpoint utama aplikasi: home, folder, deck, card, AI draft, quiz, file upload |
| `remote/api/DeckApi.kt`, `UserApi.kt` | Endpoint tambahan (lihat catatan: `DeckApi` tidak terpakai, lihat bagian Known Issues) |
| `remote/dto/auth/`, `dto/common/`, `dto/content/` | Data class request/response (bentuk JSON) per kategori |
| `repository/AuthRepositoryImpl.kt` | Implementasi login/register/logout, simpan token & user setelah auth sukses |
| `repository/ContentRepositoryImpl.kt` | Implementasi CRUD folder/deck/card, start/submit quiz, request AI draft — yang paling banyak dipakai ViewModel |
| `repository/UserRepositoryImpl.kt` | Implementasi get/update profil, ganti password |
| `repository/HomeRepositoryImpl.kt` | **Tidak terpakai** (lihat Known Issues) |

### `di/` — Hilt Modules

| File | Fungsi |
|---|---|
| `NetworkModule.kt` | Bangun instance `Retrofit`, `OkHttpClient` (dengan `AuthInterceptor` & `TokenAuthenticator`), `Gson` converter. **Base URL backend didefinisikan di sini.** |
| `AppModule.kt` | Provide dependency umum (context, data store, dll) |
| `RepositoryModule.kt` | `@Binds` — menghubungkan interface di `domain/repository` ke implementasi konkret di `data/repository` |

### `domain/` — Model & Kontrak (tidak tahu soal Retrofit/Hilt sama sekali)

| File/Folder | Fungsi |
|---|---|
| `model/ContentModels.kt` | Model untuk folder/deck/card/home secara umum |
| `model/card/CardModels.kt` | Model spesifik card |
| `model/deck/DeckModels.kt` | Model spesifik deck |
| `model/quiz/QuizModels.kt` | Model spesifik quiz (soal, jawaban, hasil) |
| `model/User.kt` | Model user/profil |
| `model/EmailVerificationStatus.kt` | Enum/status verifikasi email |
| `repository/*.kt` | Interface kontrak (`AuthRepository`, `ContentRepository`, `UserRepository`, `AiRepository`, `HomeRepository`) — inilah yang di-inject ke ViewModel, bukan implementasinya langsung |

### `navigation/`

| File | Fungsi |
|---|---|
| `AppNavGraph.kt` + `AppRoute.kt` | Graph navigasi level atas — flow sebelum login (splash, onboarding, auth) |
| `MainNavGraph.kt` + `MainRoute.kt` | Graph navigasi setelah login (home, deck, card, quiz, profile, dst) |

### `ui/`

| Folder | Fungsi |
|---|---|
| `component/` | Composable yang dipakai berulang: `DeleteConfirmDialog.kt`, `StatusDialog.kt`, `OnboardingDots.kt` |
| `model/AuthViewModel.kt` | ViewModel auth (dipakai di flow sign in/sign up) |
| `model/UserProfil.kt` | Model UI-state untuk profil |
| `screen/` | Semua halaman aplikasi, dikelompokkan per fitur — lihat tabel di bawah |
| `theme/` | Warna, typography, shape — tema Compose aplikasi |

### `util/`

| File | Fungsi |
|---|---|
| `Resource.kt` | Sealed class wrapper `Success` / `Error` / `Loading` — dipakai di seluruh Repository & ViewModel untuk represent hasil pemanggilan API |
| `CryptoManager.kt` | Helper enkripsi/dekripsi (dipakai bareng `EncryptedSharedPreferences`) |
| `DateUtils.kt` | Format & parsing tanggal |
| `FileUtils.kt` | Helper konversi file/gambar hasil kamera sebelum dikirim ke backend (untuk fitur AI scan) |

## Daftar Screen per Fitur

| Folder di `ui/screen/` | Fitur |
|---|---|
| `splash/` | Splash screen |
| `landing/`, `onboarding/` | Halaman intro sebelum login |
| `sigin/` | Sign in *(catatan: nama folder typo dari "signin")* |
| `signup/`, `signup/linksentverif/`, `signup/successverif/` | Sign up + alur verifikasi email |
| `password/forgot/`, `password/sent/`, `password/newpassword/`, `password/SucsessUpdatePassword/` | Alur lupa & reset password |
| `home/` | Halaman utama setelah login |
| `createnew/folder/`, `createnew/deck/`, `createnew/card/` | Bikin folder/deck/card secara manual |
| `create/ai/` | Bikin card via AI — `Camera.kt` (ambil foto), `AiDetailCardScreen.kt` / `Draft.kt` (lihat hasil draft AI), `EditAiCardScreen.kt` / `Form.kt` (edit sebelum simpan), `AiViewModel.kt` |
| `cards/` | Lihat & kelola card dalam satu deck |
| `profile/` | Edit profil & edit password |
| `main/MainActivity.kt` | Activity utama (container semua Compose screen) |

> Endpoint quiz sudah ada di `ApiService.kt`, tapi belum ditemukan folder `ui/screen/quiz/` yang jelas — kemungkinan UI quiz menyatu di folder lain atau belum selesai digarap. Perlu dicek ulang oleh yang paling familiar dengan kode ini.

## Catatan & Known Issues

- **Base URL backend** hard-coded di `di/NetworkModule.kt`: `https://memorise-backend-production.up.railway.app/api/`. Untuk ganti ke environment lain (staging/lokal), edit konstanta ini langsung — belum ada mekanisme build variant/flavor untuk switch environment otomatis.
- **Kode/file yang sudah tidak dipakai** (aman untuk dibersihkan kapan-kapan):
  - `data/repository/HomeRepositoryImpl.kt` — seluruh isi file di-comment, fungsinya sudah digantikan `ContentRepositoryImpl`.
  - `data/remote/api/DeckApi.kt` (dan `CardApi` di dalamnya) — didefinisikan tapi tidak pernah di-provide di module Hilt manapun; semua request deck/card aktual lewat `ApiService`.
  - `data/local/hai.txt` — file nyasar, isinya bukan kode, kemungkinan sisa testing.
  - Beberapa deklarasi endpoint lama yang di-comment di `ApiService.kt` (versi lama signature quiz) — tidak berpengaruh, sudah digantikan deklarasi aktif di bawahnya.
- Tidak ada CI/CD pipeline maupun file `local.properties.example` di repo ini — setup project sepenuhnya standar Android Studio.
