package dev.nyawit.launcher

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.nyawit.launcher.data.CatalogApp
import dev.nyawit.launcher.data.GitHubCatalog
import dev.nyawit.launcher.theme.LauncherTheme
import dev.nyawit.launcher.tv.tvFocusHighlight

@Composable
fun LauncherApp(modifier: Modifier = Modifier) {
    LauncherTheme {
        var apps by remember { mutableStateOf<List<CatalogApp>>(emptyList()) }
        var loading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            try {
                apps = GitHubCatalog().loadApps()
            } catch (t: Throwable) {
                error = t.message ?: "Gagal memuat katalog GitHub"
            } finally {
                loading = false
            }
        }

        Box(
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
        ) {
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Text("Nyawit Launcher", style = MaterialTheme.typography.headlineMedium)
                Text("Katalog dari github.com/storytold", style = MaterialTheme.typography.bodyMedium)
                if (loading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (error != null) {
                    Text("Error: $error", color = MaterialTheme.colorScheme.error)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(apps) { app ->
                            Card(
                                modifier = Modifier.fillMaxWidth().tvFocusHighlight(),
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(app.name, style = MaterialTheme.typography.titleLarge)
                                    Text(app.description, style = MaterialTheme.typography.bodyMedium)
                                    Text(app.releaseUrl, style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
