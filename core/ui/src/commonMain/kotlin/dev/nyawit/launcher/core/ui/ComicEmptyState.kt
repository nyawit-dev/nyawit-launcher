@file:Suppress("DEPRECATION")
package dev.nyawit.launcher.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * ComicEmptyState: Tampilan status kosong (empty state) bergaya panel komik.
 */
@Composable
fun ComicEmptyState(
    modifier: Modifier = Modifier,
    title: String = NyawitStrings.EMPTY_APPS_TITLE,
    description: String = NyawitStrings.EMPTY_APPS_DESC,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    val c = LocalNyawitColors.current

    ComicPanel(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 24.dp),
        backgroundColor = c.Surface,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            ComicArcadeIllustration(
                size = 90.dp,
                primaryColor = c.Primary,
            )

            Spacer(Modifier.height(18.dp))

            ComicSpeechBubble(
                tailDirection = BubbleTailDirection.BOTTOM_LEFT,
                backgroundColor = c.SurfaceHighlight,
                borderColor = c.Primary,
            ) {
                Text(
                    text = title.uppercase(),
                    style = NyawitType.Title,
                    fontWeight = FontWeight.Black,
                    color = c.Text,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = description,
                style = NyawitType.Body,
                color = c.Muted,
                textAlign = TextAlign.Center,
            )

            if (actionLabel != null && onAction != null) {
                Spacer(Modifier.height(20.dp))
                ComicButton(
                    text = actionLabel,
                    onClick = onAction,
                    variant = ComicButtonVariant.PRIMARY,
                )
            }
        }
    }
}

@Preview
@Composable
private fun ComicEmptyStatePreview() {
    ComicPreviewTheme {
        ComicEmptyState(
            title = "BELUM ADA APLIKASI",
            description = "Tidak ada aplikasi yang sesuai dengan kriteria pencarian ini.",
            actionLabel = "JELAJAHI KATALOG",
            onAction = {},
        )
    }
}
