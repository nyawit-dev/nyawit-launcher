package dev.nyawit.launcher.core.platform

/** Membaca catalog.json lokal jika tersedia (dipakai saat pengembangan desktop). */
expect fun localCatalogOverride(): String?
