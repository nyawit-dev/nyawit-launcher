package dev.nyawit.launcher.feature.install

/** Data yang dibutuhkan untuk mengunduh dan memasang satu paket aplikasi. */
data class InstallRequest(
    val url: String,
    val fileName: String,
    /** Versi dari rilis GitHub; dipakai sebagai versi terpasang di registry. */
    val version: String,
    val registryKey: String,
    val platformContext: Any? = null,
    /** true hanya untuk pembaruan launcher sendiri (proses lama akan keluar). */
    val restartAfterInstall: Boolean = false,
    /** Path installer yang sudah terunduh; bila ada, unduhan dilewati (dipakai tombol Lanjutkan). */
    val filePath: String? = null,
    val appName: String? = null,
)

/** Path tempat installer disimpan. Dipakai UI untuk melanjutkan pemasangan tanpa mengunduh ulang. */
expect fun downloadedInstallerPath(request: InstallRequest): String

/** true bila sistem mengizinkan pemasangan paket (Android: izin "Install unknown apps"). */
expect fun canInstallPackages(platformContext: Any?): Boolean

/** Membuka pengaturan izin pemasangan; true bila Activity pengaturan berhasil dibuka. */
expect fun openInstallPermissionSettings(platformContext: Any?): Boolean

/** All callbacks are invoked on the main thread. */
expect fun installApp(
    request: InstallRequest,
    onProgress: (Float) -> Unit,
    shouldCancel: () -> Boolean,
    onResult: (Boolean, String) -> Unit,
)

/** Membersihkan sisa installer atau berkas sementara yang sudah selesai digunakan. */
expect fun cleanupDownloadedFiles(platformContext: Any? = null)

/** Mengembalikan total ukuran berkas download/installer sementara dalam byte. */
expect fun getDownloadedFilesSize(platformContext: Any? = null): Long

/** Menghapus seluruh berkas unduhan di direktori unduhan; mengembalikan jumlah byte yang dibebaskan. */
expect fun clearAllDownloadedFiles(platformContext: Any? = null): Long

/** Format byte ke format teks yang ramah pengguna (mis. "12.4 MB"). */
fun formatByteSize(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    var size = bytes.toDouble()
    var unitIndex = 0
    while (size >= 1024.0 && unitIndex < units.size - 1) {
        size /= 1024.0
        unitIndex++
    }
    val rounded = (size * 10).toLong() / 10.0
    return "$rounded ${units[unitIndex]}"
}
