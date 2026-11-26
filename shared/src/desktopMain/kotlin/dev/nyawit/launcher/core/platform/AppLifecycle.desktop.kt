package dev.nyawit.launcher.core.platform

import java.awt.KeyboardFocusManager
import java.beans.PropertyChangeListener

// Desktop memantau activeWindow agar ketika jendela launcher kembali aktif/fokus
// (misal setelah pengguna menyelesaikan installer atau uninstaller eksternal),
// status aplikasi di launcher langsung disegarkan secara otomatis.
actual fun observeAppResume(
    platformContext: Any?,
    onResume: () -> Unit,
): () -> Unit {
    val kfm = KeyboardFocusManager.getCurrentKeyboardFocusManager()
    val listener = PropertyChangeListener { evt ->
        if (evt.propertyName == "activeWindow" && evt.newValue != null) {
            onResume()
        }
    }
    kfm.addPropertyChangeListener("activeWindow", listener)
    return {
        kfm.removePropertyChangeListener("activeWindow", listener)
    }
}
