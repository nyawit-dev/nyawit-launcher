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
                color = if (isSelected) c.Primary else c.SurfaceRaised,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(
                    1.dp,
                    if (isSelected) c.Primary else c.Border,
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onSelectCategory(cat) }
                    .focusable()
                    .tvFocusHighlight(RoundedCornerShape(8.dp), 1f),
            ) {
                Row(
                    Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val label = if (cat == "__ALL__") NyawitStrings.CATEGORY_ALL else cat
                    Text(
                        label,
                        style = NyawitType.Caption,
                        color = if (isSelected) c.OnPrimary else c.Text,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    )
                    if (count != null && count > 0) {
                        Surface(
                            color = if (isSelected) c.OnPrimary.copy(alpha = 0.2f) else c.SurfaceHighlight,
                            shape = RoundedCornerShape(5.dp),
                        ) {
                            Text(
                                "$count",
                                style = NyawitType.Overline,
                                color = if (isSelected) c.OnPrimary else c.Muted,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
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
        color = if (isError) c.Danger.copy(alpha = 0.12f) else c.SurfaceRaised,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, tone.copy(alpha = 0.35f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.width(3.5.dp).height(32.dp).background(tone, RoundedCornerShape(2.dp)))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(text, style = NyawitType.Caption, color = if (isError) tone else c.Text, fontWeight = FontWeight.SemiBold)
                if (busy && progress != null) {
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(4.dp),
                        color = tone,
                        trackColor = c.Border,
                    )
                }
            }
            if (actionLabel != null && !busy) {
                Spacer(Modifier.width(8.dp))
                TextButton(onClick = onAction, modifier = Modifier.tvFocusHighlight(RoundedCornerShape(8.dp), 1f)) {
                    Text(actionLabel, style = NyawitType.Caption, color = c.Primary, fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onDismiss, modifier = Modifier.tvFocusHighlight(RoundedCornerShape(8.dp), 1f)) {
                    Text(NyawitStrings.BTN_CLOSE, style = NyawitType.Caption, color = c.Muted)
                }
            }
            if (busy && progress != null) {
                Spacer(Modifier.width(12.dp))
                TextButton(onClick = onCancel, modifier = Modifier.tvFocusHighlight(RoundedCornerShape(8.dp), 1f)) {
                    Text(NyawitStrings.BTN_CANCEL, style = NyawitType.Caption, color = c.Danger, fontWeight = FontWeight.Bold)
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
            Text(NyawitStrings.SECTION_ECOSYSTEM, style = NyawitType.Overline, color = c.Primary)
            Spacer(Modifier.height(2.dp))
            Text(
                if (category == "__ALL__" || category == "Semua" || category == "All") NyawitStrings.SECTION_ALL_APPS
                else NyawitStrings.sectionCategory(category),
                style = NyawitType.Title,
                color = c.Text,
            )
        }
        Surface(
            color = c.SurfaceRaised,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, c.Border),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    NyawitStrings.sectionAppCount(total),
                    style = NyawitType.Caption,
                    color = c.Text,
                    fontWeight = FontWeight.SemiBold,
                )
                if (updateCount > 0) {
                    Box(Modifier.size(4.dp).background(c.Warning, CircleShape))
                    Text(
                        NyawitStrings.sectionUpdateCount(updateCount),
                        style = NyawitType.Caption,
                        color = c.Warning,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
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
        Image(
            painter = painterResource(Res.drawable.empty_state),
            contentDescription = null,
            modifier = Modifier.size(180.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(Modifier.height(20.dp))
        Text(NyawitStrings.EMPTY_APPS_TITLE, style = NyawitType.Title, color = c.Text)
        Spacer(Modifier.height(6.dp))
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
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = c.Surface,
        border = BorderStroke(1.dp, c.BorderHighlight),
        modifier = Modifier.fillMaxWidth().tvFocusHighlight(RoundedCornerShape(16.dp), 1f),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = c.PrimaryMuted,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, c.Primary.copy(alpha = 0.35f)),
                    ) {
                        Text(
                            NyawitStrings.CHIP_FEATURED,
                            style = NyawitType.Overline,
                            color = c.Primary,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Surface(color = c.SurfaceRaised, shape = RoundedCornerShape(6.dp)) {
                        Text(
                            NyawitStrings.CHIP_PINNED,
                            style = NyawitType.Overline,
                            color = c.Muted,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AppIcon(
                        fullName = app.fullName,
                        fallbackName = app.name,
                        size = 56.dp,
                        shape = RoundedCornerShape(14.dp),
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(app.name, style = NyawitType.Title, color = c.Text)
                        Spacer(Modifier.height(4.dp))
                        Text(app.description, style = NyawitType.Body, color = c.Muted, maxLines = 2)
                    }
                }
                Spacer(Modifier.height(18.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = if (status == AppStatus.INSTALLED) onOpen else onInstall,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (status) {
                                AppStatus.UPDATE_AVAILABLE -> c.Warning
                                AppStatus.INSTALLED -> c.SurfaceRaised
                                else -> c.Primary
                            },
                            contentColor = when (status) {
                                AppStatus.UPDATE_AVAILABLE -> c.OnWarning
                                AppStatus.INSTALLED -> c.Text
                                else -> c.OnPrimary
                            },
                        ),
                        shape = RoundedCornerShape(11.dp),
                        modifier = Modifier.height(44.dp),
                    ) {
                        Text(
                            when (status) {
                                AppStatus.INSTALLED -> NyawitStrings.BTN_OPEN_APP
                                AppStatus.UPDATE_AVAILABLE -> NyawitStrings.BTN_UPDATE
                                else -> NyawitStrings.BTN_INSTALL
                            },
                            style = NyawitType.Caption,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    TextButton(onClick = onDetail) {
                        Text(NyawitStrings.BTN_DETAIL, style = NyawitType.Caption, color = c.Muted, fontWeight = FontWeight.SemiBold)
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
