@file:Suppress("DEPRECATION")
package dev.nyawit.launcher.core.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nyawit.launcher.theme.ComicDimens
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * ComicSearchBar: Kotak pencarian bergaya komik bergaris tinta tebal dengan offset bayangan,
 * feedback visual hover/fokus, tombol hapus taktil, dan pemicu aksi pencarian otomatis.
 */
@Composable
fun ComicSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = NyawitStrings.SEARCH_PLACEHOLDER,
    focusRequester: FocusRequester? = null,
    onSearch: (() -> Unit)? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
) {
    val c = LocalNyawitColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    var isFocused by remember { mutableStateOf(false) }
    val shape = RoundedCornerShape(8.dp)

    val borderColor = when {
        !enabled -> if (c.isDark) Color(0xFF334155) else Color(0xFFCBD5E1)
        isFocused -> c.Primary
        isHovered -> c.Primary.copy(alpha = 0.7f)
        else -> c.ComicInk
    }

    val shadowRim = when {
        isFocused -> c.Primary
        isHovered -> c.Primary.copy(alpha = 0.5f)
        else -> c.ComicShadowRim
    }

    Box(
        modifier = modifier.padding(bottom = ComicDimens.ShadowOffset, end = ComicDimens.ShadowOffset),
    ) {
        // Hard offset shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = ComicDimens.ShadowOffset, y = ComicDimens.ShadowOffset)
                .background(if (c.isDark) Color(0xFF000000) else Color(0xFF0F172A), shape)
                .border(2.dp, shadowRim, shape),
        )

        // Foreground input box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(if (isFocused) c.SurfaceRaised else c.Surface, shape)
                .border(2.dp, borderColor, shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled && !isFocused && !readOnly,
                    onClick = {
                        focusRequester?.requestFocus()
                    },
                )
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SearchIcon(
                modifier = Modifier.size(18.dp),
                color = if (isFocused) c.Primary else c.Muted,
            )

            Spacer(Modifier.width(8.dp))

            Box(Modifier.weight(1f), Alignment.CenterStart) {
                if (query.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = NyawitType.Body,
                        color = c.Faint,
                        maxLines = 1,
                    )
                }

                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    readOnly = readOnly,
                    enabled = enabled,
                    textStyle = NyawitType.Body.copy(
                        color = c.Text,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    cursorBrush = SolidColor(c.Primary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            ComicSoundPlayer.play(ComicSound.CLICK)
                            onSearch?.invoke()
                        },
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
                        .onFocusChanged { isFocused = it.isFocused },
                )
            }

            AnimatedVisibility(visible = query.isNotEmpty() && enabled, enter = fadeIn(), exit = fadeOut()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    if (onSearch != null) {
                        Box(
                            modifier = Modifier
                                .background(c.Primary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .border(1.dp, c.Primary, RoundedCornerShape(4.dp))
                                .clickable {
                                    ComicSoundPlayer.play(ComicSound.CLICK)
                                    onSearch()
                                }
                                .padding(horizontal = 6.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = "CARI ↵",
                                style = NyawitType.Overline,
                                color = c.Primary,
                                fontWeight = FontWeight.Black,
                                fontSize = 8.sp,
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(c.SurfaceHighlight, RoundedCornerShape(4.dp))
                            .border(1.5.dp, c.ComicInk, RoundedCornerShape(4.dp))
                            .clickable {
                                ComicSoundPlayer.play(ComicSound.POP)
                                onQueryChange("")
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        CloseIcon(modifier = Modifier.size(14.dp), color = c.Muted)
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ComicSearchBarPreview() {
    ComicPreviewTheme {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ComicSearchBar(query = "", onQueryChange = {})
            ComicSearchBar(query = "Nyawit Music Player", onQueryChange = {}, onSearch = {})
        }
    }
}
