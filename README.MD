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

### Backend
- **Language:** PHP
- **Database:** MySQL
- **Auth:** JWT

## � Setup (Localhost)

### 1. Setup Backend (XAMPP)

1. Install XAMPP
2. Copy folder `backend/` ke `C:\xampp\htdocs\runrace\`
3. Buka phpMyAdmin (`http://localhost/phpmyadmin`)
4. Buat database baru: `runrace_db`
5. Import `database.sql`
6. Edit `config.php`:
   ```php
   define('DB_HOST', 'localhost');
   define('DB_NAME', 'runrace_db');
   define('DB_USER', 'root');
   define('DB_PASS', '');
   ```
7. Pastikan folder `uploads/` writable
8. Test API: `http://localhost/runrace/api/events`

### 2. Setup Android

1. Cari IP komputer Anda:
   - Buka CMD, ketik `ipconfig`
   - Catat IPv4 Address (contoh: `192.168.1.100`)

2. Edit `app/src/main/java/.../utils/Constants.kt`:
   ```kotlin
   const val BASE_URL = "http://192.168.1.100/runrace/api/"
   ```
   > ⚠️ **Ganti** `192.168.1.100` dengan IP komputer Anda

3. Pastikan HP Android dan komputer di **WiFi yang sama**
4. Sync Gradle & Run aplikasi

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
