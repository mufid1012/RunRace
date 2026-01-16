# 🏃 RunRace

Aplikasi Android untuk mencari dan mendaftar event lari/marathon.

![Kotlin](https://img.shields.io/badge/Kotlin-1.9-blue)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Material3-green)
![PHP](https://img.shields.io/badge/Backend-PHP-purple)

## ✨ Fitur

### Untuk User
- 🔐 **Autentikasi** - Login & Register
- 🏃 **Event** - Lihat daftar event lari, filter by status
- 📝 **Pendaftaran** - Daftar event (H-7 sebelum event)
- 📰 **Berita** - Baca berita komunitas lari
- 👤 **Profil** - Edit profil & upload foto

### Untuk Admin
- 📊 **Dashboard** - Overview statistik
- 🎯 **Kelola Event** - CRUD event + lihat peserta
- 📰 **Kelola Berita** - CRUD berita
- 🖼️ **Upload Gambar** - Upload banner/foto dari file

## 🛠️ Tech Stack

### Android
- **Language:** Kotlin
- **UI:** Jetpack Compose + Material 3
- **Architecture:** MVVM + Clean Architecture
- **DI:** Hilt
- **Networking:** Retrofit + OkHttp
- **Image Loading:** Coil
- **Navigation:** Navigation Compose

### Backend
- **Language:** PHP
- **Database:** MySQL
- **Auth:** JWT

## 📁 Struktur Project

```
app/src/main/java/com/example/runrace_finalproject/
├── data/
│   ├── model/          # Data class (Event, User, News, etc.)
│   ├── remote/         # API interfaces
│   └── repository/     # Repository pattern
├── di/                 # Dependency Injection
├── navigation/         # Navigation setup
├── ui/
│   ├── admin/          # Admin screens
│   ├── auth/           # Login, Register, Splash
│   ├── components/     # Reusable components
│   ├── event/          # Event list & detail
│   ├── home/           # Home screen
│   ├── myevent/        # My registered events
│   ├── news/           # News screens
│   └── profile/        # Profile screens
├── utils/              # Constants, helpers
└── viewmodel/          # ViewModels
```

```
backend/
├── api/
│   ├── endpoints/
│   │   ├── auth.php      # Login, Register
│   │   ├── events.php    # Event CRUD + registration
│   │   ├── news.php      # News CRUD
│   │   ├── users.php     # User profile
│   │   └── uploads.php   # Image upload
│   ├── index.php         # Router
│   └── .htaccess
├── uploads/              # Uploaded images
├── config.php            # DB & JWT config
├── jwt.php               # JWT helper
└── database.sql          # Database schema
```

## 🚀 Setup

### Backend
1. Import `database.sql` ke MySQL
2. Edit `config.php`:
   ```php
   define('DB_HOST', 'localhost');
   define('DB_NAME', 'runrace_db');
   define('DB_USER', 'your_user');
   define('DB_PASS', 'your_pass');
   ```
3. Upload folder `backend/` ke hosting
4. Pastikan folder `uploads/` writable (chmod 755)

### Android
1. Edit `Constants.kt`:
   ```kotlin
   const val BASE_URL = "https://your-domain.com/api/"
   ```
2. Sync Gradle
3. Run aplikasi

## 📋 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/login` | Login user |
| POST | `/auth/register` | Register user |
| GET | `/events` | Get all events |
| GET | `/events?status=X` | Filter by status |
| GET | `/events/{id}` | Get event detail |
| POST | `/events` | Create event (Admin) |
| PUT | `/events/{id}` | Update event (Admin) |
| DELETE | `/events/{id}` | Delete event (Admin) |
| POST | `/events/{id}/register` | Register for event |
| DELETE | `/events/{id}/unregister` | Unregister |
| GET | `/events/{id}/participants` | Get participants (Admin) |
| GET | `/news` | Get all news |
| POST | `/news` | Create news (Admin) |
| GET | `/users/profile` | Get my profile |
| PUT | `/users/profile` | Update profile |
| POST | `/uploads` | Upload image |

## 📅 Status Event

| Status | Deskripsi | Pendaftaran |
|--------|-----------|-------------|
| **Akan Datang** | > 7 hari sebelum event | ✅ Bisa daftar |
| **Berlangsung** | ≤ 7 hari sebelum event | ❌ Ditutup |
| **Selesai** | Event sudah lewat | ❌ Tidak bisa |

## 👤 Default Admin

```
Email: admin@runrace.com
Password: admin123
```

## 📄 License

MIT License - Dibuat oleh **mufid1012**
