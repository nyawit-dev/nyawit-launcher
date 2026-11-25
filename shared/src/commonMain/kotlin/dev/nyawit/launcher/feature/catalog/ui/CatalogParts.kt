package dev.nyawit.launcher.feature.catalog.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nyawit.launcher.core.ui.ComicActionBurst
import dev.nyawit.launcher.core.ui.ComicArcadeIllustration
import dev.nyawit.launcher.core.ui.ComicBadge
import dev.nyawit.launcher.core.ui.ComicHalftonePattern
import dev.nyawit.launcher.core.ui.ComicPanel
import dev.nyawit.launcher.core.ui.ComicSpeechBubble
import dev.nyawit.launcher.core.ui.ComicSpeedLines
import dev.nyawit.launcher.core.ui.MenuIcon
import dev.nyawit.launcher.core.ui.NyawitStrings
import dev.nyawit.launcher.core.ui.SearchIcon
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.feature.catalog.data.CatalogApp
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import nyawit_launcher.shared.generated.resources.Res
import nyawit_launcher.shared.generated.resources.empty_state
import org.jetbrains.compose.resources.painterResource

/** Tanda merek yang digambar vektor, tajam di semua kepadatan layar. */
@Composable
fun BrandMark(size: Int = 46) {
    val c = LocalNyawitColors.current
    Canvas(Modifier.size(size.dp)) {
        val r = 12.dp.toPx()
        drawRoundRect(
            c.SurfaceRaised,
            size = androidx.compose.ui.geometry.Size(size.dp.toPx(), size.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r),
        )
        drawRoundRect(
            c.BorderHighlight,
            size = androidx.compose.ui.geometry.Size(size.dp.toPx(), size.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(r, r),
            style = Stroke(1.2.dp.toPx()),
        )
        val cx = size.dp.toPx() / 2f
        val cy = size.dp.toPx() / 2f
        drawCircle(c.Primary, radius = size.dp.toPx() * 0.28f, center = Offset(cx, cy))
        val nSize = size.dp.toPx() * 0.32f
        drawLine(
            c.OnPrimary,
            Offset(cx - nSize / 2, cy + nSize / 2),
            Offset(cx - nSize / 2, cy - nSize / 2),
            strokeWidth = 3.dp.toPx(),
        )
        drawLine(
            c.OnPrimary,
            Offset(cx - nSize / 2, cy - nSize / 2),
            Offset(cx + nSize / 2, cy + nSize / 2),
            strokeWidth = 3.dp.toPx(),
        )
        drawLine(
            c.OnPrimary,
            Offset(cx + nSize / 2, cy - nSize / 2),
            Offset(cx + nSize / 2, cy + nSize / 2),
            strokeWidth = 3.dp.toPx(),
        )
    }
}

@Composable
fun CatalogSearchBar(query: String, onQuery: (String) -> Unit, focusRequester: FocusRequester) {
    val c = LocalNyawitColors.current
    OutlinedTextField(
        value = query,
        onValueChange = onQuery,
        placeholder = { Text(NyawitStrings.SEARCH_PLACEHOLDER, style = NyawitType.Body, color = c.Faint) },
        leadingIcon = { SearchIcon(Modifier.size(19.dp), c.Muted) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                Surface(
                    color = c.SurfaceHighlight,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, c.BorderHighlight),
                    modifier = Modifier
                        .clickable { onQuery("") }
                        .padding(end = 12.dp),
                ) {
                    Text(
                        NyawitStrings.SYMBOL_CLOSE,
                        color = c.Text,
                        style = NyawitType.Caption,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    )
                }
            } else {
                Surface(
                    color = c.SurfaceRaised,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, c.Border),
                    modifier = Modifier.padding(end = 12.dp),
                ) {
                    Text(
                        NyawitStrings.KEY_ESC,
                        style = NyawitType.Overline,
                        color = c.Faint,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                    )
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        textStyle = NyawitType.Body,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = c.SurfaceRaised,
            unfocusedContainerColor = c.Surface,
            focusedBorderColor = c.Primary,
            unfocusedBorderColor = c.BorderHighlight,
            focusedTextColor = c.Text,
            unfocusedTextColor = c.Text,
            cursorColor = c.Primary,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .tvFocusHighlight(RoundedCornerShape(12.dp), 1f),
    )
}

@Composable
fun CategoryFilterChips(
    categories: List<String>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    counts: Map<String, Int> = emptyMap(),
    modifier: Modifier = Modifier,
) {
    val c = LocalNyawitColors.current
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(count = categories.size, key = { categories[it] }) { index ->
            val cat = categories[index]
            val isSelected = cat == selectedCategory
            val count = counts[cat]
            Surface(
                color = if (isSelected) c.Primary else c.Surface,
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(
                    2.dp,
                    if (isSelected) (if (c.isDark) c.Primary else Color(0xFF0F172A)) else (if (c.isDark) c.BorderHighlight else Color(0xFF334155)),
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onSelectCategory(cat) }
                    .focusable()
                    .tvFocusHighlight(RoundedCornerShape(6.dp), 1f),
            ) {
                Row(
                    Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val label = if (cat == "__ALL__") NyawitStrings.CATEGORY_ALL else cat
                    Text(
                        label.uppercase(),
                        style = NyawitType.Caption,
                        color = if (isSelected) c.OnPrimary else c.Text,
                        fontWeight = FontWeight.Black,
                    )
                    if (count != null && count > 0) {
                        Surface(
                            color = if (isSelected) c.OnPrimary.copy(alpha = 0.25f) else c.SurfaceHighlight,
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, if (isSelected) c.OnPrimary else c.Border),
                        ) {
                            Text(
                                "$count",
                                style = NyawitType.Overline,
                                color = if (isSelected) c.OnPrimary else c.Muted,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoticeBanner(
    text: String,
    isError: Boolean,
    busy: Boolean,
    progress: Float?,
    onCancel: () -> Unit,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
    onDismiss: () -> Unit = {},
) {
    val c = LocalNyawitColors.current
    val tone = if (isError) c.Danger else c.Primary
    Surface(
        color = if (isError) c.Danger.copy(alpha = 0.15f) else c.SurfaceRaised,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, tone),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(4.dp).height(32.dp).background(tone, RoundedCornerShape(2.dp)))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(text, style = NyawitType.Caption, color = if (isError) tone else c.Text, fontWeight = FontWeight.Bold)
                if (busy && progress != null) {
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(3.dp)),
                        color = tone,
                        trackColor = c.Border,
                    )
                }
            }
            if (actionLabel != null && !busy) {
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onAction, modifier = Modifier.tvFocusHighlight(RoundedCornerShape(6.dp), 1f)) {
                    Text(actionLabel, style = NyawitType.Caption, color = c.Primary, fontWeight = FontWeight.Black)
                }
                TextButton(onClick = onDismiss, modifier = Modifier.tvFocusHighlight(RoundedCornerShape(6.dp), 1f)) {
                    Text(NyawitStrings.BTN_CLOSE, style = NyawitType.Caption, color = c.Muted)
                }
            }
            if (busy && progress != null) {
                Spacer(Modifier.width(12.dp))
                TextButton(onClick = onCancel, modifier = Modifier.tvFocusHighlight(RoundedCornerShape(6.dp), 1f)) {
                    Text(NyawitStrings.BTN_CANCEL, style = NyawitType.Caption, color = c.Danger, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun SectionTitle(category: String, total: Int, updateCount: Int) {
    val c = LocalNyawitColors.current
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("CHAPTER // EKOSISTEM", style = NyawitType.Overline, color = c.Primary, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(2.dp))
            Text(
                if (category == "__ALL__" || category == "Semua" || category == "All") "SEMUA APLIKASI"
                else category.uppercase(),
                style = NyawitType.Title,
                fontWeight = FontWeight.Black,
                color = c.Text,
            )
        }
        ComicBadge(
            text = "$total UNIT" + if (updateCount > 0) " // $updateCount UPDATE!" else "",
            containerColor = if (updateCount > 0) c.Warning.copy(alpha = 0.2f) else c.SurfaceRaised,
            contentColor = if (updateCount > 0) c.Warning else c.Text,
            borderColor = if (updateCount > 0) c.Warning else c.BorderHighlight,
        )
    }
}

@Composable
fun LoadingPlaceholder() {
    val c = LocalNyawitColors.current
    val alpha by rememberInfiniteTransition().animateFloat(
        initialValue = 0.40f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            tween(850, easing = FastOutSlowInEasing),
            RepeatMode.Reverse,
        ),
    )
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        repeat(3) {
            Surface(
                color = c.Surface.copy(alpha = alpha),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, c.BorderHighlight),
                modifier = Modifier.fillMaxWidth().height(124.dp),
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = c.SurfaceRaised, shape = RoundedCornerShape(12.dp), modifier = Modifier.size(46.dp)) {}
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Surface(color = c.SurfaceRaised, shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxWidth(0.45f).height(14.dp)) {}
                        Spacer(Modifier.height(10.dp))
                        Surface(color = c.SurfaceRaised, shape = RoundedCornerShape(4.dp), modifier = Modifier.fillMaxWidth(0.85f).height(10.dp)) {}
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    val c = LocalNyawitColors.current
    Column(
        Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(Res.drawable.empty_state),
                contentDescription = null,
                modifier = Modifier.size(180.dp),
                contentScale = ContentScale.Fit,
            )
            ComicActionBurst(
                text = "ZAP!",
                burstColor = Color(0xFFFF4757),
                textColor = Color(0xFFFFFFFF),
                size = 54.dp,
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
        Spacer(Modifier.height(16.dp))
        ComicSpeechBubble(
            backgroundColor = if (c.isDark) c.SurfaceRaised else Color(0xFFFFFFFF),
            borderColor = if (c.isDark) c.Primary else Color(0xFF0F172A),
        ) {
            Text(
                "\"${NyawitStrings.EMPTY_APPS_TITLE.uppercase()}!\"",
                style = NyawitType.Subtitle,
                fontWeight = FontWeight.Black,
                color = c.Text,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(NyawitStrings.EMPTY_APPS_DESC, style = NyawitType.Caption, color = c.Muted)
    }
}

@Composable
fun FeaturedApp(
    app: CatalogApp,
    status: AppStatus,
    onOpen: () -> Unit,
    onInstall: () -> Unit,
    onDetail: () -> Unit,
) {
    val c = LocalNyawitColors.current
    ComicPanel(
        modifier = Modifier
            .fillMaxWidth()
            .tvFocusHighlight(RoundedCornerShape(12.dp), 1f),
        borderWidth = 2.5.dp,
        borderColor = if (c.isDark) c.Primary else Color(0xFF0F172A),
        shadowOffset = 5.dp,
        shape = RoundedCornerShape(12.dp),
    ) {
        // Comic Halftone dots & Speed lines background
        ComicHalftonePattern(
            modifier = Modifier.matchParentSize(),
            dotRadius = 1.8f,
            spacing = 16f,
            dotColor = if (c.isDark) c.Primary.copy(alpha = 0.12f) else Color(0xFF0F172A).copy(alpha = 0.06f),
        )
        ComicSpeedLines(
            modifier = Modifier.matchParentSize(),
            lineColor = if (c.isDark) c.Primary.copy(alpha = 0.16f) else Color(0xFF0F172A).copy(alpha = 0.05f),
        )

        Column(Modifier.padding(20.dp)) {
            // Header: Spotlight Classification & Comic Burst Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ComicActionBurst(
                        text = "HOT!",
                        burstColor = Color(0xFFFFDE00),
                        textColor = Color(0xFF0F172A),
                        size = 38.dp,
                    )
                    ComicBadge(
                        text = "EPISODE SPESIAL // REKOMENDASI",
                        containerColor = c.Primary,
                        contentColor = c.OnPrimary,
                    )
                }

                ComicBadge(
                    text = app.category.uppercase(),
                    containerColor = c.SurfaceRaised,
                    contentColor = c.Text,
                    borderColor = if (c.isDark) c.Primary.copy(alpha = 0.6f) else Color(0xFF0F172A),
                )
            }

            Spacer(Modifier.height(18.dp))

            // Main Core Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = c.SurfaceRaised,
                    border = BorderStroke(2.5.dp, if (c.isDark) c.Primary else Color(0xFF0F172A)),
                    modifier = Modifier.size(72.dp),
                ) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        AppIcon(
                            fullName = app.fullName,
                            fallbackName = app.name,
                            size = 60.dp,
                            shape = RoundedCornerShape(8.dp),
                        )
                    }
                }

                Spacer(Modifier.width(18.dp))

                Column(Modifier.weight(1f)) {
                    Text(
                        app.name,
                        style = NyawitType.Title,
                        fontWeight = FontWeight.Black,
                        color = c.Text,
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            app.fullName.substringBefore('/'),
                            style = NyawitType.Caption,
                            color = c.Primary,
                            fontWeight = FontWeight.Black,
                        )
                        Box(Modifier.size(4.dp).background(c.Primary, CircleShape))
                        Text(
                            app.packageName,
                            style = NyawitType.Caption,
                            color = c.Faint,
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    ComicSpeechBubble(
                        backgroundColor = if (c.isDark) c.SurfaceHighlight else Color(0xFFF8FAFC),
                        borderColor = if (c.isDark) c.Primary.copy(alpha = 0.5f) else Color(0xFF0F172A),
                    ) {
                        Text(
                            "\"${app.description}\"",
                            style = NyawitType.Body,
                            color = c.Text,
                            maxLines = 2,
                        )
                    }
                }

                // Comic Pop-Art Vector Gamepad Illustration
                Spacer(Modifier.width(12.dp))
                ComicArcadeIllustration(
                    modifier = Modifier.size(76.dp),
                    primaryColor = c.Primary,
                    accentColor = Color(0xFFFFDE00),
                )
            }

            Spacer(Modifier.height(18.dp))

            // Spotlight Action Command Deck
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                val isInstalled = status == AppStatus.INSTALLED
                val isUpdate = status == AppStatus.UPDATE_AVAILABLE
                Button(
                    onClick = if (isInstalled) onOpen else onInstall,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when {
                            isUpdate -> c.Warning
                            isInstalled -> c.Primary
                            else -> c.Primary
                        },
                        contentColor = when {
                            isUpdate -> c.OnWarning
                            else -> c.OnPrimary
                        },
                    ),
                    border = BorderStroke(
                        2.5.dp,
                        Color(0xFF0F172A),
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .tvFocusHighlight(RoundedCornerShape(8.dp), 1f),
                ) {
                    Text(
                        when {
                            isInstalled -> "JALANKAN SEKARANG! ➔"
                            isUpdate -> "PERBARUI VERSI! ➔"
                            else -> "PASANG KE PERANGKAT! ➔"
                        },
                        style = NyawitType.Caption,
                        fontWeight = FontWeight.Black,
                    )
                }

                Button(
                    onClick = onDetail,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = c.SurfaceRaised,
                        contentColor = c.Text,
                    ),
                    border = BorderStroke(2.dp, if (c.isDark) Color(0xFF64748B) else Color(0xFF0F172A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .tvFocusHighlight(RoundedCornerShape(8.dp), 1f),
                ) {
                    Text(
                        "LIHAT SPESIFIKASI",
                        style = NyawitType.Caption,
                        color = c.Text,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

/** Header atas untuk ponsel: bersih, ada tombol drawer navigasi, tanpa tombol cek update di atas. */
@Composable
fun MobileHeader(
    version: String,
    onOpenDrawer: () -> Unit,
    updateAvailable: Boolean,
    modifier: Modifier = Modifier,
) {
    val c = LocalNyawitColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            color = c.SurfaceRaised,
            shape = RoundedCornerShape(10.dp),
            border = BorderStroke(1.dp, c.Border),
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .clickable { onOpenDrawer() }
                .tvFocusHighlight(RoundedCornerShape(10.dp), 1f),
        ) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                MenuIcon(Modifier.size(18.dp), c.Text)
                if (updateAvailable) {
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(7.dp)
                            .background(c.Warning, CircleShape),
                    )
                }
            }
        }
        Spacer(Modifier.width(12.dp))
        BrandMark(36)
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(NyawitStrings.BRAND_NAME, style = NyawitType.Wordmark, color = c.Text)
                Box(Modifier.size(4.dp).background(c.Primary, CircleShape))
                Text(NyawitStrings.BRAND_SUBTITLE, style = NyawitType.Overline, color = c.Primary)
            }
            Text(
                "v$version",
                style = NyawitType.Caption,
                color = c.Muted,
            )
        }
    }
}
