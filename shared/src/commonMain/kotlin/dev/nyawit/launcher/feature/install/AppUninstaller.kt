package dev.nyawit.launcher.feature.install

/** Menghapus aplikasi. Callback dipanggil di thread utama. */
expect fun uninstallApp(
    packageName: String,
    displayName: String,
    platformContext: Any?,
    onResult: (Boolean, String) -> Unit,
)
