package dev.nyawit.launcher.core.ui

import java.io.ByteArrayInputStream
import java.util.concurrent.ConcurrentHashMap
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin

actual object ComicSoundPlayer {
    actual var enabled: Boolean = true

    private const val SAMPLE_RATE = 22050
    private val format = AudioFormat(SAMPLE_RATE.toFloat(), 16, 1, true, false)
    private val soundBuffers = ConcurrentHashMap<ComicSound, ByteArray>()

    init {
        try {
            soundBuffers[ComicSound.CLICK] = generateClick()
            soundBuffers[ComicSound.POP] = generatePop()
            soundBuffers[ComicSound.SUCCESS] = generateSuccess()
            soundBuffers[ComicSound.FOCUS] = generateFocus()
        } catch (_: Throwable) {}
    }

    actual fun play(sound: ComicSound) {
        if (!enabled) return
        val data = soundBuffers[sound] ?: return
        Thread {
            try {
                val stream = AudioInputStream(ByteArrayInputStream(data), format, (data.size / 2).toLong())
                val clip = AudioSystem.getClip()
                clip.open(stream)
                clip.start()
                clip.addLineListener { event ->
                    if (event.type == javax.sound.sampled.LineEvent.Type.STOP) {
                        clip.close()
                    }
                }
            } catch (_: Throwable) {}
        }.apply {
            isDaemon = true
            priority = Thread.MIN_PRIORITY
            start()
        }
    }

    private fun shortToBytes(value: Short, target: ByteArray, offset: Int) {
        target[offset] = (value.toInt() and 0xFF).toByte()
        target[offset + 1] = ((value.toInt() shr 8) and 0xFF).toByte()
    }

    private fun generateClick(): ByteArray {
        val durationMs = 24
        val totalSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val bytes = ByteArray(totalSamples * 2)
        var phase = 0.0

        for (i in 0 until totalSamples) {
            val progress = i.toDouble() / totalSamples
            val freq = 920.0 - (580.0 * progress)
            phase += 2.0 * PI * freq / SAMPLE_RATE
            val env = (1.0 - progress).pow(2.2)
            val sample = (sin(phase) * env * 24000.0).toInt().coerceIn(-32767, 32767).toShort()
            shortToBytes(sample, bytes, i * 2)
        }
        return bytes
    }

    private fun generatePop(): ByteArray {
        val durationMs = 42
        val totalSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val bytes = ByteArray(totalSamples * 2)
        var phase = 0.0

        for (i in 0 until totalSamples) {
            val progress = i.toDouble() / totalSamples
            val freq = 480.0 + (420.0 * progress)
            phase += 2.0 * PI * freq / SAMPLE_RATE
            val env = sin(PI * progress)
            val sample = (sin(phase) * env * 22000.0).toInt().coerceIn(-32767, 32767).toShort()
            shortToBytes(sample, bytes, i * 2)
        }
        return bytes
    }

    private fun generateSuccess(): ByteArray {
        // Retro 4-note arcade fanfare: C5 (523Hz), E5 (659Hz), G5 (784Hz), C6 (1046Hz)
        val noteDurationsMs = listOf(65, 65, 65, 130)
        val noteFrequencies = listOf(523.25, 659.25, 783.99, 1046.50)
        val totalSamples = noteDurationsMs.sumOf { (SAMPLE_RATE * (it / 1000.0)).toInt() }
        val bytes = ByteArray(totalSamples * 2)

        var sampleIndex = 0
        for (n in noteFrequencies.indices) {
            val freq = noteFrequencies[n]
            val noteSamples = (SAMPLE_RATE * (noteDurationsMs[n] / 1000.0)).toInt()
            var phase = 0.0

            for (i in 0 until noteSamples) {
                val progress = i.toDouble() / noteSamples
                phase += 2.0 * PI * freq / SAMPLE_RATE
                // 8-bit pulse square wave + warm sine wave
                val pulse = if (sin(phase) >= 0) 0.6 else -0.6
                val sine = sin(phase)
                val isLastNote = n == noteFrequencies.lastIndex
                val env = if (isLastNote) (1.0 - progress).pow(1.3) else 0.85
                val mixed = (pulse * 0.45 + sine * 0.55) * env
                val sample = (mixed * 22000.0).toInt().coerceIn(-32767, 32767).toShort()

                if (sampleIndex * 2 + 1 < bytes.size) {
                    shortToBytes(sample, bytes, sampleIndex * 2)
                    sampleIndex++
                }
            }
        }
        return bytes
    }

    private fun generateFocus(): ByteArray {
        val durationMs = 15
        val totalSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt()
        val bytes = ByteArray(totalSamples * 2)
        var phase = 0.0

        for (i in 0 until totalSamples) {
            val progress = i.toDouble() / totalSamples
            phase += 2.0 * PI * 1150.0 / SAMPLE_RATE
            val env = (1.0 - progress).pow(1.8)
            val sample = (sin(phase) * env * 17000.0).toInt().coerceIn(-32767, 32767).toShort()
            shortToBytes(sample, bytes, i * 2)
        }
        return bytes
    }
}
