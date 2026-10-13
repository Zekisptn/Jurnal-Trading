# Style Guide — Jurnal Trading

Panduan gaya visual aplikasi. Semua nilai diambil dari kode di `app/src/main/java/com/example/jurnaltreding/ui/theme` dan `ui/components` pada commit `012205e`.

Cara membaca dokumen ini:

- **"Langsung"** berarti nilai itu dipanggil sendiri di kode layar atau komponen (mis. `MaterialTheme.colorScheme.error`).
- **"Bawaan Material 3"** berarti kode aplikasi tidak menyebut nilai itu; komponen Material 3 yang mengambilnya dari theme. Pemetaannya mengikuti spesifikasi Material 3, bukan tertulis di kode proyek.
- Theme mengikuti mode sistem (`isSystemInDarkTheme()`); warna dinamis (Material You) tidak dipakai, sehingga palet selalu sama di semua perangkat.

---

## 1. Palet warna

Sumber: `ui/theme/Color.kt` (nilai hex) dan `ui/theme/Theme.kt` (pemetaan ke peran).

### Warna utama

| Peran | Terang | Gelap | Kegunaan di aplikasi |
|---|---|---|---|
| `primary` | `#146C43` | `#8BD6A9` | Langsung: ikon logo di Login, judul bagian (kartu Detail, form). Bawaan: tombol utama (Masuk, Daftar, Simpan), teks `TextButton`, indikator loading |
| `onPrimary` | `#FFFFFF` | `#003822` | Bawaan: teks di atas tombol utama |
| `primaryContainer` | `#B7F1CE` | `#005231` | Langsung: latar avatar Profil. Bawaan: tombol "+" (FAB) |
| `onPrimaryContainer` | `#002112` | `#B7F1CE` | Langsung: huruf inisial avatar. Bawaan: ikon "+" |
| `secondary` | `#4E6356` | `#B5CCBB` | Didefinisikan, belum dipakai langsung |
| `onSecondary` | `#FFFFFF` | `#203529` | Didefinisikan, belum dipakai langsung |
| `secondaryContainer` | `#D0E8D7` | `#374B3F` | Langsung: badge emosi. Bawaan: chip filter terpilih, penanda tab aktif di bottom bar, tombol "Edit Profil" |
| `onSecondaryContainer` | `#0B1F15` | `#D0E8D7` | Langsung: teks badge emosi |
| `tertiary` | `#7A5900` | `#F4BF48` | Didefinisikan, belum dipakai langsung |
| `onTertiary` | `#FFFFFF` | `#402D00` | Didefinisikan, belum dipakai langsung |
| `tertiaryContainer` | `#FFDEA0` | `#5C4300` | Langsung: badge status **Open** |
| `onTertiaryContainer` | `#261A00` | `#FFDEA0` | Langsung: teks badge Open |
| `error` | `#BA1A1A` | `#FFB4AB` | Langsung: ikon `ErrorState`, pesan error emosi di form, teks tombol Keluar dan tombol konfirmasi hapus. Bawaan: garis dan pesan error text field |
| `onError` | `#FFFFFF` | `#690005` | Didefinisikan, belum dipakai langsung |
| `errorContainer` | `#FFDAD6` | `#93000A` | Langsung: latar `ErrorBanner` |
| `onErrorContainer` | `#410002` | `#FFDAD6` | Langsung: teks dan ikon `ErrorBanner` |

### Latar dan permukaan

| Peran | Terang | Gelap | Kegunaan di aplikasi |
|---|---|---|---|
| `background` | `#F8FAF6` | `#111412` | Bawaan: latar setiap layar |
| `onBackground` | `#191C1A` | `#E1E3DE` | Bawaan: teks di atas latar |
| `surface` | `#F8FAF6` | `#111412` | Bawaan: latar `Scaffold` dan top bar |
| `onSurface` | `#191C1A` | `#E1E3DE` | Teks utama. Langsung: nilai untung/rugi yang impas (Rp0), teks evaluasi |
| `surfaceVariant` | `#DCE5DC` | `#414942` | Langsung: badge status **Closed** |
| `onSurfaceVariant` | `#414942` | `#C0C9C0` | Langsung: teks sekunder (keterangan, label, tanggal, pesan state), teks badge Closed. Paling sering dipakai: 16 kali |
| `outline` | `#717971` | `#8A938A` | Langsung: ikon `EmptyState`. Bawaan: garis text field |
| `outlineVariant` | `#C0C9C0` | `#414942` | Bawaan: garis pemisah (`HorizontalDivider`) |
| `surfaceContainerLowest` | `#FFFFFF` | `#0C0F0D` | Didefinisikan, belum dipakai langsung |
| `surfaceContainerLow` | `#F2F5F0` | `#191C1A` | Langsung: latar semua kartu biasa (kartu jurnal, statistik, Detail, Profil) |
| `surfaceContainer` | `#ECEFEA` | `#1D201E` | Bawaan: latar bottom bar |
| `surfaceContainerHigh` | `#E6E9E4` | `#282B28` | Langsung: kartu "Total untung/rugi" di Dashboard. Bawaan: latar dialog |
| `surfaceContainerHighest` | `#E1E3DE` | `#323633` | Bawaan: lintasan `Switch` saat mati |

### Warna untung/rugi

Di luar `ColorScheme` Material 3; disediakan lewat `MaterialTheme.tradingColors` (`TradingColors` di `Theme.kt`) dan dipakai oleh `profitLossColor()` di `ui/components/JournalEntryCard.kt`.

| Nama | Terang | Gelap | Kegunaan |
|---|---|---|---|
| `profit` | `#17692F` | `#6FDD8B` | Nilai untung (positif) |
| `loss` | `#C62828` | `#FF8A80` | Nilai rugi (negatif) |

Nilai impas memakai `onSurface`.

---

## 2. Tipografi

Sumber: `ui/theme/Type.kt`. Semua gaya memakai `FontFamily.Default` (font sistem; Roboto di sebagian besar perangkat Android). Gaya Material 3 yang tidak tercantum di bawah (`display*`, `headlineLarge`) tidak diubah dan tidak dipakai.

| Gaya | Ukuran | Ketebalan | Tinggi baris | Jarak huruf | Contoh penggunaan |
|---|---|---|---|---|---|
| `headlineMedium` | 28sp | Bold | 36sp | – | Judul "Jurnal Trading" di Login, kode saham di Detail, angka "Total untung/rugi", inisial avatar |
| `headlineSmall` | 24sp | Bold | 32sp | – | Sapaan "Halo, …", angka di kartu statistik, nilai untung/rugi di Detail |
| `titleLarge` | 22sp | SemiBold | 28sp | – | Nama user di Profil. Bawaan: judul top bar |
| `titleMedium` | 16sp | SemiBold | 24sp | 0,15sp | Kode saham dan untung/rugi di kartu jurnal, "Catatan terbaru", judul `EmptyState` dan `ErrorState` |
| `titleSmall` | 14sp | SemiBold | 20sp | 0,1sp | Judul bagian form ("Pembelian", "Penjualan"), persentase untung/rugi di Detail |
| `bodyLarge` | 16sp | Normal | 24sp | 0,5sp | Alasan dan evaluasi di Detail, nilai di kartu Profil. Bawaan: teks yang diketik di text field |
| `bodyMedium` | 14sp | Normal | 20sp | 0,25sp | Teks isi umum: baris detail, deskripsi, pesan state, isi `ErrorBanner`. Paling sering dipakai: 16 kali |
| `bodySmall` | 12sp | Normal | 16sp | 0,4sp | Keterangan kecil: tanggal di kartu, keterangan kartu statistik, label di kartu Profil. Bawaan: pesan error text field |
| `labelLarge` | 14sp | SemiBold | 20sp | 0,1sp | Label kartu statistik, judul kartu di Detail. Bawaan: teks tombol |
| `labelMedium` | 12sp | Medium | 16sp | 0,5sp | Teks badge (Open, Closed, emosi). Bawaan: label tab bottom bar |
| `labelSmall` | 11sp | Medium | 16sp | 0,5sp | Didefinisikan, belum dipakai langsung |

Aturan yang berlaku di kode: layar tidak menetapkan `fontSize` atau `fontWeight` sendiri; semuanya lewat `MaterialTheme.typography`.

---

## 3. Spacing dan bentuk

### Spacing

Semua jarak memakai kelipatan 4dp. Nilai yang dipakai di `ui/` (di luar `ui/theme`):

| Nilai | Dipakai untuk |
|---|---|
| 4dp | Jarak rapat antar teks dalam satu blok; padding vertikal badge |
| 8dp | Jarak antar field form, antar elemen berdekatan, padding horizontal badge. Dipakai 27 kali |
| 12dp | Jarak antar kartu, padding `ErrorBanner` |
| 16dp | Padding tepi layar dan padding isi kartu. Dipakai 28 kali |
| 20dp | Jarak sebelum tombol aksi di `EmptyState`/`ErrorState`; jarak setelah teks pembuka di Register |
| 24dp | Padding horizontal Login dan Register; jarak antar blok di Profil |
| 32dp | Padding `EmptyState`, `LoadingState`, `ErrorState`; jarak blok di Login |
| 88dp | Padding bawah daftar supaya kartu terakhir tidak tertutup tombol "+" |

Ukuran elemen (bukan jarak):

| Nilai | Dipakai untuk |
|---|---|
| 2dp | Ketebalan garis indikator loading di dalam tombol |
| 18dp (`ButtonDefaults.IconSize`) | Ikon dan indikator loading di dalam tombol |
| 56dp | Ikon logo di Login |
| 64dp | Ikon `EmptyState` dan `ErrorState` |
| 88dp | Avatar Profil |

### Bentuk

Sumber: `ui/theme/Shape.kt`.

| Nama | Radius sudut | Dipakai oleh |
|---|---|---|
| `extraSmall` | 6dp | Langsung: badge status dan emosi. Bawaan: text field |
| `small` | 10dp | Langsung: `ErrorBanner`. Bawaan: chip filter dan chip emosi |
| `medium` | 14dp | Bawaan: semua kartu |
| `large` | 20dp | Bawaan: tombol "+" (FAB) |
| `extraLarge` | 28dp | Bawaan: dialog konfirmasi dan date picker |

Di luar `Shapes`:

- **Avatar Profil** memakai `CircleShape` (lingkaran penuh), karena `Shapes` tidak punya bentuk lingkaran.
- **Tombol** (`Button`, `FilledTonalButton`, `OutlinedButton`, `TextButton`) memakai bentuk pil bawaan Material 3, tidak diubah.

---

## 4. Komponen

### Komponen reusable di `ui/components`

| Komponen | Kegunaan | File |
|---|---|---|
| `EmptyState` | Tampilan saat belum ada data: ikon, judul, pesan, tombol aksi opsional | `StateViews.kt` |
| `LoadingState` | Indikator loading di tengah layar dengan pesan (bawaan "Memuat data...") | `StateViews.kt` |
| `ErrorState` | Tampilan saat data gagal dimuat, dengan tombol "Coba lagi" opsional | `StateViews.kt` |
| `ConfirmDialog` | Dialog konfirmasi untuk hapus catatan dan logout; tombol konfirmasi berwarna `error` jika `isDestructive` | `ConfirmDialog.kt` |
| `AppTextField` | Text field bergaris dengan label, pesan error per field, teks bantuan, dan awalan (mis. "Rp") | `FormFields.kt` |
| `PasswordField` | Text field password dengan tombol tampilkan/sembunyikan | `FormFields.kt` |
| `LoadingButton` | Tombol utama selebar layar; menampilkan indikator dan nonaktif selama proses | `FormFields.kt` |
| `ErrorBanner` | Pesan error umum form (login gagal, gagal menyimpan) | `FormFields.kt` |
| `DateField` | Field tanggal yang membuka date picker; tanggal setelah hari ini tidak bisa dipilih | `DateField.kt` |
| `JournalEntryCard` | Kartu ringkasan satu catatan; dipakai di Dashboard dan Daftar Jurnal | `JournalEntryCard.kt` |
| `StatusBadge` | Penanda Open / Closed | `JournalEntryCard.kt` |
| `EmotionBadge` | Penanda emosi (Yakin, Ragu, FOMO, Takut) | `JournalEntryCard.kt` |
| `ProfitLossText` | Nilai untung/rugi dalam Rupiah bertanda, berwarna sesuai `profitLossColor()` | `JournalEntryCard.kt` |

`PreviewData.kt` di folder yang sama hanya berisi data contoh untuk `@Preview`, bukan komponen.

### Komponen di luar `ui/components`

| Komponen | Kegunaan | File |
|---|---|---|
| `BottomBar` | Bottom navigation tiga tab: Dashboard · Jurnal · Profil | `ui/navigation/BottomBar.kt` |
| `StatCard` (private) | Kartu statistik Dashboard: label, angka, keterangan | `ui/dashboard/DashboardScreen.kt` |
| `SectionCard` (private) | Kartu berjudul di Detail Jurnal | `ui/journal/JournalDetailScreen.kt` |

### Komponen Material 3 yang dipakai langsung

| Jenis | Komponen | Dipakai untuk |
|---|---|---|
| Tombol | `Button` (lewat `LoadingButton`, dan aksi di `EmptyState`) | Aksi utama: Masuk, Daftar, Simpan, Tambah catatan |
| Tombol | `FilledTonalButton` | "Edit Profil" |
| Tombol | `OutlinedButton` | "Keluar", "Coba lagi" |
| Tombol | `TextButton` | Tautan (Daftar, Masuk, Lihat semua) dan tombol dialog |
| Tombol | `FloatingActionButton` | Tombol "+" di Dashboard dan Daftar Jurnal |
| Tombol | `IconButton` | Kembali, edit, hapus, hapus pencarian, tampilkan password |
| Input | `OutlinedTextField` | Semua text field, termasuk kolom pencarian |
| Input | `FilterChip` | Filter Semua/Open/Closed dan pilihan emosi |
| Input | `Switch` | "Sudah dijual" di form jurnal |
| Wadah | `Card` | Semua kartu, dengan latar `surfaceContainerLow` atau `surfaceContainerHigh` |
| Navigasi | `TopAppBar`, `NavigationBar` | Judul layar dan bottom bar |
| Dialog | `AlertDialog`, `DatePickerDialog` | Konfirmasi dan pemilih tanggal |

---

## 5. Aksesibilitas

### Rasio kontras

Dihitung dengan rumus WCAG 2.x dari nilai hex di `Color.kt`. Batas minimum: 4,5:1 untuk teks biasa, 3:1 untuk teks besar dan elemen non-teks.

| Warna depan | Di atas latar | Terang | Gelap | Contoh |
|---|---|---|---|---|
| `onSurface` | `surface` | 16,36 | 14,34 | Teks utama di layar |
| `onSurface` | `surfaceContainerLow` | 15,62 | 13,29 | Teks utama di kartu |
| `onSurfaceVariant` | `surface` | 8,86 | 10,91 | Teks sekunder di layar |
| `onSurfaceVariant` | `surfaceContainerLow` | 8,46 | 10,11 | Teks sekunder di kartu |
| `onSurfaceVariant` | `surfaceContainerHigh` | 7,60 | 8,43 | Keterangan di kartu "Total untung/rugi" |
| `onSurfaceVariant` | `surfaceContainer` | 8,02 | 9,67 | Label tab tidak aktif di bottom bar |
| `onSurfaceVariant` | `surfaceVariant` | 7,22 | 5,48 | Badge Closed |
| `primary` | `surface` | 6,14 | 10,85 | Teks `TextButton`, judul bagian form |
| `primary` | `surfaceContainerLow` | 5,86 | 10,05 | Judul kartu di Detail |
| `onPrimary` | `primary` | 6,45 | 7,74 | Teks tombol utama |
| `onPrimaryContainer` | `primaryContainer` | 13,44 | 7,32 | Avatar, ikon "+" |
| `onSecondaryContainer` | `secondaryContainer` | 13,27 | 7,23 | Badge emosi, chip terpilih |
| `onTertiaryContainer` | `tertiaryContainer` | 13,18 | 7,18 | Badge Open |
| `error` | `surface` | 6,15 | 10,92 | Pesan error field |
| `onErrorContainer` | `errorContainer` | 13,26 | 7,24 | `ErrorBanner` |
| `profit` | `surfaceContainerLow` | 6,16 | 10,14 | Untung di kartu jurnal dan Detail |
| `profit` | `surfaceContainerHigh` | 5,53 | 8,45 | Untung di kartu "Total untung/rugi" |
| `loss` | `surfaceContainerLow` | 5,11 | 7,53 | Rugi di kartu jurnal dan Detail |
| `loss` | `surfaceContainerHigh` | 4,59 | 6,27 | Rugi di kartu "Total untung/rugi" |
| `outline` | `surface` | 4,28 | 5,85 | Ikon `EmptyState`, garis text field (non-teks) |

Hasil:

- Semua pasangan teks memenuhi 4,5:1 di kedua mode.
- Nilai terendah untuk teks adalah `loss` di atas `surfaceContainerHigh` mode terang (4,59:1). Di posisi itu teksnya 28sp Bold, yang tergolong teks besar.
- `outline` di atas `surface` mode terang (4,28:1) hanya dipakai untuk ikon dan garis, yang batasnya 3:1.
- Teks pada komponen nonaktif (mis. field Email di Edit Profil) memakai warna redup bawaan Material 3 dan berada di bawah 4,5:1. WCAG mengecualikan komponen nonaktif dari aturan kontras.

### Ukuran sentuh

- **Minimum 48dp × 48dp.** Kode aplikasi tidak menetapkan angka ini sendiri: semua elemen yang bisa diklik memakai komponen Material 3 (`Button`, `TextButton`, `IconButton`, `FilterChip`, `Switch`, `FloatingActionButton`, `Card` dengan `onClick`, `NavigationBarItem`), yang secara bawaan memperluas area sentuhnya ke minimal 48dp.
- **Tidak ada `Modifier.clickable` pada elemen kecil** seperti teks atau ikon lepas, sehingga tidak ada area sentuh yang lebih kecil dari bawaan tersebut.
- **Field tanggal** (`DateField`) bisa disentuh di seluruh area text field.

### Hal lain

- **Deskripsi konten:** ikon yang berfungsi sebagai tombol punya `contentDescription` berbahasa Indonesia (mis. "Kembali", "Tambah catatan", "Hapus catatan", "Tampilkan password"). Ikon hiasan memakai `null` supaya dilewati pembaca layar.
- **Warna bukan satu-satunya penanda:** untung/rugi juga dibedakan dengan tanda `+` dan `-`; status dibedakan dengan teks "Open"/"Closed"; error field disertai pesan teks.
- **Belum diuji:** aplikasi belum pernah dijalankan dengan TalkBack atau dengan ukuran font sistem yang diperbesar, dan mode gelap belum pernah dilihat di emulator.
