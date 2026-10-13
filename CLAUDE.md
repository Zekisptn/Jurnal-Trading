# Jurnal Trading — Konteks Proyek

Aplikasi Android untuk mencatat dan mengevaluasi transaksi saham investor pemula (proyek UTS Mobile Programming 2026).
Bukan aplikasi untuk membeli saham: ini "buku harian investor" — apa yang dibeli, kenapa, emosinya saat itu, dan hasilnya.
Bahasa UI: Indonesia. Fokus saham Indonesia (1 lot = 100 lembar). Semua data lokal, tanpa server.
Package: `com.example.jurnaltreding` (ejaan "treding" disengaja, jangan diubah). Nama tampilan aplikasi (`app_name`): "Jurnal Trading".

Tim: Putra Rama Gustiantoro (01085210011), Zacky Septian Nazarudin (01085230005).

## Stack (wajib, sesuai rubrik)
- Kotlin, single Activity, **Jetpack Compose + Material 3**
- **MVVM + Repository**: Composable → ViewModel → Repository → DAO. UI/Composable **tidak boleh** mengakses DAO atau database langsung.
- **Room** untuk data pengguna dan jurnal; **DataStore (Preferences)** untuk sesi login (id user yang sedang login)
- **Kotlin Flow / StateFlow** — dashboard dan daftar harus reaktif (ter-update otomatis saat data berubah)
- **Navigation Compose** dengan bottom navigation bar
- Dependency injection: manual (AppContainer di Application class) — jangan tambah Hilt kecuali diminta
- Password disimpan sebagai hash (SHA-256 + salt), tidak pernah plain text

## Struktur package
```
data/
  local/        Room: AppDatabase, entity, DAO
  datastore/    SessionManager (DataStore)
  repository/   UserRepository, JournalRepository
ui/
  auth/         LoginScreen, RegisterScreen + ViewModel
  dashboard/    DashboardScreen + ViewModel
  journal/      JournalListScreen, JournalDetailScreen, JournalFormScreen + ViewModel
  profile/      ProfileScreen, EditProfileScreen + ViewModel
  navigation/   NavGraph, rute, BottomBar
  components/   komponen reusable (EmptyState, LoadingState, ErrorState, dialog konfirmasi)
  theme/        Color, Type, Theme (Material 3)
util/           validasi input, format Rupiah/tanggal, perhitungan untung/rugi
```

## Model data
**User**: id, name, email (unik), passwordHash, salt, investingStyle (opsional), createdAt

**JournalEntry** (satu catatan = satu posisi trading, dari beli sampai jual):
id, userId (FK → User), stockCode (mis. BBCA, huruf besar), buyDate, buyPrice, lots,
sellDate (nullable), sellPrice (nullable), reason, emotion (enum: YAKIN, RAGU, FOMO, TAKUT),
evaluation (nullable), createdAt, updatedAt

- Status **Open** jika sellPrice/sellDate kosong, **Closed** jika terisi (dihitung, bukan disimpan terpisah)
- Untung/rugi = (sellPrice − buyPrice) × lots × 100 (hanya untuk Closed)
- Semua query jurnal difilter per userId yang sedang login

## Fitur & kriteria rubrik (target skor maksimal)
- **Login/Register (D1)**: validasi kosong & format email, pesan error jelas per field, sesi tersimpan (tetap login setelah app ditutup), logout berfungsi
- **Dashboard (D2)**: data nyata dari Room — total transaksi, win rate (Closed untung / total Closed), total untung/rugi, jumlah posisi Open, catatan terbaru, emosi paling sering; reaktif via Flow
- **Navigasi (D3)**: bottom bar Dashboard · Jurnal · Profil; back stack benar (tidak bisa kembali ke Login setelah login/ke dalam app setelah logout); state tidak hilang saat rotasi (state di ViewModel, `rememberSaveable` untuk input)
- **Profil (D4)**: tampil data user login, bisa diedit dan tersimpan, tombol logout dengan konfirmasi
- **CRUD Jurnal (D5)**: create, read (list + detail), update, delete; validasi form (field wajib, harga > 0, lot ≥ 1, tanggal jual tidak sebelum tanggal beli); dialog konfirmasi hapus; data persisten
- Daftar jurnal: pencarian kode saham + filter Semua/Open/Closed
- Setiap layar punya state **kosong, loading, dan error**
- **Arsitektur (D6)**: MVVM + Repository konsisten, kode rapi, commit Git teratur

## Aturan kerja
- Bangun bertahap per fitur; pastikan build sukses sebelum lanjut: `gradlew.bat assembleDebug` (Windows)
- Logika bisnis (validasi, perhitungan) di ViewModel/util, bukan di Composable
- UiState sebagai data class/sealed class yang diekspos lewat StateFlow
- Teks UI dalam bahasa Indonesia; format uang Rupiah (Rp1.250.000)
- Commit kecil dan sering dengan pesan jelas (mis. `feat: tambah form jurnal dengan validasi`)
- Jangan menambah fitur di luar daftar di atas tanpa diminta
