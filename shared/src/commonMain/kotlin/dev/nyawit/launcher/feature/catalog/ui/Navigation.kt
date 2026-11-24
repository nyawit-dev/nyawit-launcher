package dev.nyawit.launcher.feature.catalog.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.core.ui.tvFocusHighlight
import dev.nyawit.launcher.theme.LocalNyawitColors
import dev.nyawit.launcher.theme.NyawitType

enum class AppPage { APPS, ABOUT }

@Composable
fun AppSidebar(
    page: AppPage,
    version: String,
    updateTag: String?,
    total: Int,
    updateCount: Int,
    selfBusy: Boolean,
    onNavigate: (AppPage) -> Unit,
    onUpdate: () -> Unit,
    modifier: Modifier = Modifier,
    onClose: (() -> Unit)? = null,
) {
    val c = LocalNyawitColors.current

    Column(
        modifier
            .fillMaxHeight()
            .background(c.Surface)
            .padding(20.dp),
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            BrandMark(40)
            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text("NYAWIT", style = NyawitType.Wordmark, color = c.Text)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(4.dp).background(c.Primary, CircleShape))
                    Spacer(Modifier.width(5.dp))
                    Text("LAUNCHER", style = NyawitType.Overline, color = c.Primary)
                }
            }
            if (onClose != null) {
                Surface(
                    color = c.SurfaceRaised,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, c.Border),
                    modifier = Modifier.size(30.dp).clickable { onClose() },
                ) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) {
                        Text("✕", color = c.Muted, style = NyawitType.Caption, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(30.dp))
        Text("MENU", style = NyawitType.Overline, color = c.Faint)
        Spacer(Modifier.height(10.dp))

        NavItem(
            label = "Aplikasi",
            selected = page == AppPage.APPS,
            onClick = { onNavigate(AppPage.APPS); onClose?.invoke() },
        )
        Spacer(Modifier.height(4.dp))
        NavItem(
            label = "Tentang",
            selected = page == AppPage.ABOUT,
            onClick = { onNavigate(AppPage.ABOUT); onClose?.invoke() },
        )

        Spacer(Modifier.weight(1f))

        Surface(
            color = c.SurfaceRaised,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, c.Border),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(14.dp)) {
                Text("STATUS EKOSISTEM", style = NyawitType.Overline, color = c.Primary)
                Spacer(Modifier.height(7.dp))
                Text("$total aplikasi terdaftar", style = NyawitType.Caption, color = c.Text, fontWeight = FontWeight.SemiBold)
                Text(
                    if (updateCount > 0) "$updateCount pembaruan tersedia"
                    else if (updateTag != null) "Pembaruan launcher $updateTag"
                    else "Semua sudah mutakhir",
                    style = NyawitType.Caption,
                    color = if (updateCount > 0 || updateTag != null) c.Warning else c.Muted,
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        Button(
            onClick = { onUpdate(); onClose?.invoke() },
            enabled = !selfBusy,
            colors = ButtonDefaults.buttonColors(
                containerColor = c.SurfaceRaised,
                contentColor = c.Text,
            ),
            shape = RoundedCornerShape(11.dp),
            modifier = Modifier.fillMaxWidth().height(40.dp).tvFocusHighlight(RoundedCornerShape(11.dp), 1f),
        ) {
            if (selfBusy) {
                CircularProgressIndicator(Modifier.size(14.dp), strokeWidth = 2.dp, color = c.Primary)
                Spacer(Modifier.width(8.dp))
            }
            Text("Cek pembaruan", style = NyawitType.Caption, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(9.dp))
        Text(
            "v$version",
            style = NyawitType.Overline,
            color = c.Faint,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )
    }
}

@Composable
private fun NavItem(label: String, selected: Boolean, onClick: () -> Unit) {
    val c = LocalNyawitColors.current
    Row(
        Modifier
            .fillMaxWidth()
            .height(42.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(if (selected) c.PrimaryMuted else androidx.compose.ui.graphics.Color.Transparent)
            .clickable(onClick = onClick)
            .focusable()
            .tvFocusHighlight(RoundedCornerShape(11.dp), 1f)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .width(3.5.dp)
                .height(18.dp)
                .background(if (selected) c.Primary else c.Border, RoundedCornerShape(2.dp)),
        )
        Spacer(Modifier.width(11.dp))
        Text(
            label,
            style = NyawitType.Caption,
            color = if (selected) c.Primary else c.Muted,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}
