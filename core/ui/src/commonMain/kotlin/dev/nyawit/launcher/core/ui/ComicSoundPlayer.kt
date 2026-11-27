package dev.nyawit.launcher.core.ui

enum class ComicSound {
    CLICK,
    POP,
    SUCCESS,
    FOCUS,
}

expect object ComicSoundPlayer {
    var enabled: Boolean

    fun play(sound: ComicSound)
}
