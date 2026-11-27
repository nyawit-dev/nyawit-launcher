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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.nyawit.launcher.core.platform.AppPlatform
import dev.nyawit.launcher.core.ui.ComicActionBurst
import dev.nyawit.launcher.core.ui.ComicArcadeIllustration
import dev.nyawit.launcher.core.ui.ComicBadge
import dev.nyawit.launcher.core.ui.ComicButton
import dev.nyawit.launcher.core.ui.ComicButtonVariant
import dev.nyawit.launcher.core.ui.ComicFilterChip
import dev.nyawit.launcher.core.ui.ComicHalftonePattern
import dev.nyawit.launcher.core.ui.ComicNotice
import dev.nyawit.launcher.core.ui.ComicPanel
import dev.nyawit.launcher.core.ui.ComicSearchBar
import dev.nyawit.launcher.core.ui.ComicSpeechBubble
import dev.nyawit.launcher.core.ui.ComicSpeedLines
import dev.nyawit.launcher.core.ui.LocalResponsiveScale
import dev.nyawit.launcher.core.ui.MenuIcon
import dev.nyawit.launcher.core.ui.NyawitStrings
import dev.nyawit.launcher.core.ui.ScreenKind
import dev.nyawit.launcher.core.ui.SearchIcon
import dev.nyawit.launcher.core.ui.rememberDeviceProfile
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.feature.catalog.data.CatalogApp
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType
import nyawit_launcher.shared.generated.resources.Res
import nyawit_launcher.shared.generated.resources.empty_state
import nyawit_launcher.shared.generated.resources.nyawit_launcher
import org.jetbrains.compose.resources.painterResource

/** Tanda merek dengan ikon resmi Nyawit berpadu gaya neo-pop comic ink. */
@Composable
fun BrandMark(size: Int = 46) {
    val c = LocalNyawitColors.current
    val cornerRadius = (size * 0.22f).dp
    Box(Modifier.size(size.dp)) {
        // Comic hard drop shadow
        Box(
            Modifier
                .size(size.dp)
                .offset(2.dp, 2.dp)
                .background(
                    if (c.isDark) Color(0xFF020617) else Color(0xFF0F172A),
                    RoundedCornerShape(cornerRadius),
                ),
        )
        Surface(
            modifier = Modifier
                .size(size.dp)
                .border(
                    BorderStroke(2.dp, if (c.isDark) c.Primary.copy(alpha = 0.6f) else Color(0xFF0F172A)),
                    RoundedCornerShape(cornerRadius),
                ),
            shape = RoundedCornerShape(cornerRadius),
            color = c.SurfaceRaised,
        ) {
            Image(
                painter = painterResource(Res.drawable.nyawit_launcher),
                contentDescription = NyawitStrings.BRAND_NAME,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(cornerRadius)),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
fun CatalogSearchBar(query: String, onQuery: (String) -> Unit, focusRequester: FocusRequester) {
    ComicSearchBar(
        query = query,
        onQueryChange = onQuery,
        focusRequester = focusRequester,
        modifier = Modifier
            .fillMaxWidth()
            .tvFocusHighlight(RoundedCornerShape(8.dp), 1f),
    )
}

enum class CatalogStatusFilter(val label: String) {
    ALL("SEMUA"),
    FAVORITES("FAVORIT"),
    INSTALLED("TERPASANG"),
    UPDATE("ADA UPDATE"),
}

enum class CatalogSortOrder(val label: String) {
    FAVORITES_FIRST("FAVORIT UTAMA"),
    A_TO_Z("A ➔ Z"),
    Z_TO_A("Z ➔ A"),
    CATEGORY("KATEGORI"),
    UPDATE_FIRST("PRIORITAS UPDATE"),
}

@Composable
fun QuickFilterAndSortRow(
    statusFilter: CatalogStatusFilter,
    onStatusFilterChange: (CatalogStatusFilter) -> Unit,
    sortOrder: CatalogSortOrder,
    onSortOrderChange: (CatalogSortOrder) -> Unit,
    installedCount: Int,
    updateCount: Int,
    favoriteCount: Int = 0,
    modifier: Modifier = Modifier,
) {
    val c = LocalNyawitColors.current
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Quick Status Filters (Horizontally scrollable if space is constrained)
        Row(
            modifier = Modifier
                .weight(1f, fill = false)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ComicFilterChip(
                text = "SEMUA",
                selected = statusFilter == CatalogStatusFilter.ALL,
                onClick = { onStatusFilterChange(CatalogStatusFilter.ALL) },
            )
            ComicFilterChip(
                text = "★ FAVORIT",
                selected = statusFilter == CatalogStatusFilter.FAVORITES,
                count = if (favoriteCount > 0) favoriteCount else null,
                onClick = { onStatusFilterChange(CatalogStatusFilter.FAVORITES) },
            )
            ComicFilterChip(
                text = "TERPASANG",
                selected = statusFilter == CatalogStatusFilter.INSTALLED,
                count = installedCount,
                onClick = { onStatusFilterChange(CatalogStatusFilter.INSTALLED) },
            )
            if (updateCount > 0) {
                ComicFilterChip(
                    text = "ADA UPDATE",
                    selected = statusFilter == CatalogStatusFilter.UPDATE,
                    count = updateCount,
                    onClick = { onStatusFilterChange(CatalogStatusFilter.UPDATE) },
                )
            }
        }

        Spacer(Modifier.width(8.dp))

        // Sort Order Selector Toggle
        ComicBadge(
            text = "SORT: ${sortOrder.label}",
            containerColor = c.SurfaceRaised,
            contentColor = c.Primary,
            onClick = {
                val next = when (sortOrder) {
                    CatalogSortOrder.FAVORITES_FIRST -> CatalogSortOrder.A_TO_Z
                    CatalogSortOrder.A_TO_Z -> CatalogSortOrder.Z_TO_A
                    CatalogSortOrder.Z_TO_A -> CatalogSortOrder.UPDATE_FIRST
                    CatalogSortOrder.UPDATE_FIRST -> CatalogSortOrder.CATEGORY
                    CatalogSortOrder.CATEGORY -> CatalogSortOrder.FAVORITES_FIRST
                }
                onSortOrderChange(next)
            },
        )
    }
}

@Composable
fun CategoryFilterChips(
    categories: List<String>,
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    counts: Map<String, Int> = emptyMap(),
    modifier: Modifier = Modifier.fillMaxWidth(),
    initialFocusRequester: FocusRequester? = null,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(count = categories.size, key = { categories[it] }) { index ->
            val cat = categories[index]
            val isSelected = cat == selectedCategory
            val count = counts[cat]
            val label = if (cat == "__ALL__") NyawitStrings.CATEGORY_ALL else cat
            ComicFilterChip(
                text = label,
                selected = isSelected,
                count = count,
                onClick = { onSelectCategory(cat) },
                focusRequester = if (index == 0) initialFocusRequester else null,
            )
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
    ComicNotice(
        text = text,
        isError = isError,
        actionLabel = if (busy && progress != null) NyawitStrings.BTN_CANCEL else actionLabel,
        onAction = if (busy && progress != null) onCancel else onAction,
        onDismiss = if (!busy && actionLabel != null) onDismiss else null,
        progress = if (busy) progress else null,
        modifier = Modifier.fillMaxWidth(),
    )
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

private fun AppPlatform.badgeLabel() = when (this) {
    AppPlatform.ANDROID -> "APK"
    AppPlatform.TV -> "TV"
    AppPlatform.DESKTOP -> "PC"
}

@Composable
fun FeaturedApp(
    app: CatalogApp,
    status: AppStatus,
    isFavorite: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null,
    onOpen: () -> Unit,
    onInstall: () -> Unit,
    onDetail: () -> Unit,
) {
    val c = LocalNyawitColors.current
    val responsiveScale = LocalResponsiveScale.current
    val profile = rememberDeviceProfile()
    val isTv = profile.kind == ScreenKind.TV
    val isCompact = profile.kind == ScreenKind.PHONE || (!isTv && profile.widthDp < 768f)

    ComicPanel(
        modifier = Modifier
            .fillMaxWidth()
            .tvFocusHighlight(RoundedCornerShape(12.dp), 1f),
        borderWidth = 2.5.dp,
        borderColor = if (c.isDark) c.Primary else Color(0xFF0F172A),
        shadowOffset = if (isTv) 3.dp else 5.dp,
        shape = RoundedCornerShape(12.dp),
    ) {
        // Comic Halftone dots & Speed lines background
        ComicHalftonePattern(
            modifier = Modifier.matchParentSize(),
            dotRadius = 1.6f,
            spacing = 16f,
            dotColor = if (c.isDark) c.Primary.copy(alpha = 0.12f) else Color(0xFF0F172A).copy(alpha = 0.06f),
        )
        ComicSpeedLines(
            modifier = Modifier.matchParentSize(),
            lineColor = if (c.isDark) c.Primary.copy(alpha = 0.16f) else Color(0xFF0F172A).copy(alpha = 0.05f),
        )

        if (isTv) {
            // Android TV 10-Foot Horizontal Hero Spotlight (Ringkas, hemat vertikal ~115dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = c.SurfaceRaised,
                        border = BorderStroke(2.dp, if (c.isDark) c.Primary else Color(0xFF0F172A)),
                        modifier = Modifier.size(52.dp),
                    ) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            AppIcon(
                                fullName = app.fullName,
                                fallbackName = app.name,
                                size = 40.dp,
                                shape = RoundedCornerShape(8.dp),
                            )
                        }
                    }

                    Spacer(Modifier.width(14.dp))

                    Column(Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            ComicActionBurst(
                                text = "HOT!",
                                burstColor = Color(0xFFFFDE00),
                                textColor = Color(0xFF0F172A),
                                size = 28.dp,
                            )
                            if (onToggleFavorite != null) {
                                ComicBadge(
                                    text = if (isFavorite) "★" else "☆",
                                    containerColor = if (isFavorite) c.Warning else c.SurfaceRaised,
                                    contentColor = if (isFavorite) c.OnWarning else c.Muted,
                                    borderColor = if (isFavorite) c.Warning else c.BorderHighlight,
                                    onClick = onToggleFavorite,
                                )
                            }
                            Text(
                                app.name,
                                style = NyawitType.Subtitle,
                                fontWeight = FontWeight.Black,
                                color = c.Text,
                                maxLines = 1,
                                softWrap = false,
                            )
                            ComicBadge(
                                text = app.category.uppercase(),
                                containerColor = c.Primary,
                                contentColor = c.OnPrimary,
                            )
                            Text(
                                "${app.fullName.substringBefore('/')} • ${app.packageName}",
                                style = NyawitType.Caption,
                                color = c.Muted,
                                maxLines = 1,
                                softWrap = false,
                            )
                        }

                        Spacer(Modifier.height(5.dp))

                        Text(
                            "\"${app.description}\"",
                            style = NyawitType.Body,
                            color = c.Text,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Spacer(Modifier.width(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val isInstalled = status == AppStatus.INSTALLED
                    val isUpdate = status == AppStatus.UPDATE_AVAILABLE
                    ComicButton(
                        text = when {
                            isInstalled -> "BUKA!"
                            isUpdate -> "UPDATE!"
                            else -> "PASANG!"
                        },
                        onClick = if (isInstalled) onOpen else onInstall,
                        variant = if (isUpdate) ComicButtonVariant.WARNING else ComicButtonVariant.PRIMARY,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        textStyle = NyawitType.Caption.copy(fontWeight = FontWeight.Black),
                    )

                    ComicButton(
                        text = "DETAIL",
                        onClick = onDetail,
                        variant = ComicButtonVariant.SECONDARY,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                        textStyle = NyawitType.Caption.copy(fontWeight = FontWeight.Bold),
                    )
                }
            }
        } else {
        Column(Modifier.padding(if (isCompact) 14.dp else 20.dp)) {
            // Header: Spotlight Classification & Comic Burst Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    ComicActionBurst(
                        text = "HOT!",
                        burstColor = Color(0xFFFFDE00),
                        textColor = Color(0xFF0F172A),
                        size = if (isCompact) 30.dp else 38.dp,
                    )
                    ComicBadge(
                        text = if (isCompact) "REKOMENDASI" else "EPISODE SPESIAL // REKOMENDASI",
                        containerColor = c.Primary,
                        contentColor = c.OnPrimary,
                    )
                    if (onToggleFavorite != null) {
                        ComicBadge(
                            text = if (isFavorite) "★ FAVORIT" else "☆ FAVORITKAN",
                            containerColor = if (isFavorite) c.Warning else c.SurfaceRaised,
                            contentColor = if (isFavorite) c.OnWarning else c.Muted,
                            borderColor = if (isFavorite) c.Warning else c.BorderHighlight,
                            onClick = onToggleFavorite,
                        )
                    }
                }

                ComicBadge(
                    text = app.category.uppercase(),
                    containerColor = c.SurfaceRaised,
                    contentColor = c.Text,
                    borderColor = if (c.isDark) c.Primary.copy(alpha = 0.6f) else Color(0xFF0F172A),
                )
            }

            Spacer(Modifier.height(if (isCompact) 12.dp else 18.dp))

            if (isCompact) {
                // Mobile Portrait Layout: Clean 1-Column Stack
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = c.SurfaceRaised,
                        border = BorderStroke(2.dp, if (c.isDark) c.Primary else Color(0xFF0F172A)),
                        modifier = Modifier.size(54.dp),
                    ) {
                        Box(Modifier.fillMaxSize(), Alignment.Center) {
                            AppIcon(
                                fullName = app.fullName,
                                fallbackName = app.name,
                                size = 44.dp,
                                shape = RoundedCornerShape(8.dp),
                            )
                        }
                    }

                    Spacer(Modifier.width(12.dp))

                    Column(Modifier.weight(1f)) {
                        Text(
                            app.name,
                            style = NyawitType.Subtitle,
                            fontWeight = FontWeight.Black,
                            color = c.Text,
                            maxLines = 1,
                            softWrap = false,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "${app.fullName.substringBefore('/')} • ${app.packageName}",
                            style = NyawitType.Caption,
                            color = c.Muted,
                            maxLines = 1,
                            softWrap = false,
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                ComicSpeechBubble(
                    modifier = Modifier.fillMaxWidth(),
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

                Spacer(Modifier.height(12.dp))

                // Mobile Buttons: Side by side with proportional weights
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val isInstalled = status == AppStatus.INSTALLED
                    val isUpdate = status == AppStatus.UPDATE_AVAILABLE
                    ComicButton(
                        text = when {
                            isInstalled -> "BUKA!"
                            isUpdate -> "UPDATE!"
                            else -> "PASANG!"
                        },
                        onClick = if (isInstalled) onOpen else onInstall,
                        variant = if (isUpdate) ComicButtonVariant.WARNING else ComicButtonVariant.PRIMARY,
                        modifier = Modifier.weight(1.3f).height(42.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        textStyle = NyawitType.Caption.copy(fontWeight = FontWeight.Black),
                    )

                    ComicButton(
                        text = "DETAIL",
                        onClick = onDetail,
                        variant = ComicButtonVariant.SECONDARY,
                        modifier = Modifier.weight(1f).height(42.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        textStyle = NyawitType.Caption.copy(fontWeight = FontWeight.Bold),
                    )
                }
            } else {
                // Wide Desktop / Tablet Layout: Balanced 2-Column Hero Layout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1.2f).fillMaxWidth(),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = c.SurfaceRaised,
                                border = BorderStroke(2.5.dp, if (c.isDark) c.Primary else Color(0xFF0F172A)),
                                modifier = Modifier.size(68.dp),
                            ) {
                                Box(Modifier.fillMaxSize(), Alignment.Center) {
                                    AppIcon(
                                        fullName = app.fullName,
                                        fallbackName = app.name,
                                        size = 56.dp,
                                        shape = RoundedCornerShape(8.dp),
                                    )
                                }
                            }

                            Spacer(Modifier.width(16.dp))

                            Column {
                                Text(
                                    app.name,
                                    style = NyawitType.Title,
                                    fontWeight = FontWeight.Black,
                                    color = c.Text,
                                    maxLines = 1,
                                    softWrap = false,
                                )
                                Spacer(Modifier.height(3.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        app.fullName.substringBefore('/'),
                                        style = NyawitType.Caption,
                                        color = c.Primary,
                                        fontWeight = FontWeight.Black,
                                        maxLines = 1,
                                        softWrap = false,
                                    )
                                    Box(Modifier.size(4.dp).background(c.Primary, CircleShape))
                                    Text(
                                        app.packageName,
                                        style = NyawitType.Caption,
                                        color = c.Faint,
                                        maxLines = 1,
                                        softWrap = false,
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

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

                        Spacer(Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            val isInstalled = status == AppStatus.INSTALLED
                            val isUpdate = status == AppStatus.UPDATE_AVAILABLE
                            ComicButton(
                                text = when {
                                    isInstalled -> "BUKA SEKARANG!"
                                    isUpdate -> "PERBARUI!"
                                    else -> "PASANG!"
                                },
                                onClick = if (isInstalled) onOpen else onInstall,
                                variant = if (isUpdate) ComicButtonVariant.WARNING else ComicButtonVariant.PRIMARY,
                                modifier = Modifier.defaultMinSize(minHeight = 44.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                                textStyle = NyawitType.Caption.copy(fontWeight = FontWeight.Black),
                            )

                            ComicButton(
                                text = "DETAIL",
                                onClick = onDetail,
                                variant = ComicButtonVariant.SECONDARY,
                                modifier = Modifier.defaultMinSize(minHeight = 44.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                                textStyle = NyawitType.Caption.copy(fontWeight = FontWeight.Bold),
                            )
                        }
                    }

                    Spacer(Modifier.width(20.dp))

                    ComicPanel(
                        modifier = Modifier.weight(0.8f).defaultMinSize(minHeight = 130.dp),
                        borderWidth = 2.dp,
                        shadowOffset = 3.dp,
                        backgroundColor = if (c.isDark) c.SurfaceRaised else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Column(
                            Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                ComicArcadeIllustration(
                                    modifier = Modifier.size(86.dp),
                                    primaryColor = c.Primary,
                                    accentColor = Color(0xFFFFDE00),
                                )
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        "SPESIFIKASI MODUL",
                                        style = NyawitType.Overline,
                                        color = if (c.isDark) c.Primary else Color(0xFF0F172A),
                                        fontWeight = FontWeight.Black,
                                        maxLines = 1,
                                        softWrap = false,
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        app.platforms.forEach { platform ->
                                            ComicBadge(
                                                text = platform.badgeLabel(),
                                                containerColor = c.Surface,
                                                contentColor = c.Text,
                                                borderColor = if (c.isDark) c.Primary.copy(alpha = 0.5f) else Color(0xFF0F172A),
                                            )
                                        }
                                    }
                                    Text(
                                        "STATUS: SIAP JALAN ✦",
                                        style = NyawitType.Overline,
                                        color = c.Muted,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        softWrap = false,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}

/** Header atas untuk ponsel dengan gaya Neo-Pop Comic. */
@Composable
fun MobileHeader(
    version: String,
    onOpenDrawer: () -> Unit,
    updateAvailable: Boolean,
    modifier: Modifier = Modifier,
) {
    val c = LocalNyawitColors.current
    val responsiveScale = LocalResponsiveScale.current

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ComicPanel(
            modifier = Modifier
                .size(responsiveScale.dp(42))
                .clickable { onOpenDrawer() }
                .tvFocusHighlight(RoundedCornerShape(8.dp), 1f),
            borderWidth = 2.dp,
            shadowOffset = 2.5.dp,
            backgroundColor = c.SurfaceRaised,
            shape = RoundedCornerShape(8.dp),
        ) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                MenuIcon(Modifier.size(responsiveScale.dp(20)), c.Text)
                if (updateAvailable) {
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(8.dp)
                            .background(c.Warning, CircleShape)
                            .border(BorderStroke(1.dp, Color(0xFF0F172A)), CircleShape),
                    )
                }
            }
        }
        Spacer(Modifier.width(responsiveScale.dp(12)))
        BrandMark(36)
        Spacer(Modifier.width(responsiveScale.dp(10)))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(NyawitStrings.BRAND_NAME, style = NyawitType.Wordmark, color = c.Text)
                ComicBadge(
                    text = "EDISI PONSEL",
                    containerColor = c.Primary,
                    contentColor = c.OnPrimary,
                )
            }
            Text(
                "${NyawitStrings.BRAND_SUBTITLE.uppercase()} • v$version",
                style = NyawitType.Overline,
                color = c.Muted,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
