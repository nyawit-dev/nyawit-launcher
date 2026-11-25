package dev.nyawit.launcher.core.platform

/**
 * Mendaftarkan callback saat aplikasi kembali ke foreground.
 * Dipakai untuk menyegarkan status aplikasi setelah pengguna menyelesaikan
 * konfirmasi pasang/hapus di dialog sistem.
 *
 * @return fungsi untuk membatalkan pendaftaran.
 */
expect fun observeAppResume(
    platformContext: Any?,
    onResume: () -> Unit,
): () -> Unit
