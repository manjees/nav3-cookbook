package com.nav3cookbook.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nav3cookbook.sample.basic.BasicNavigation
import com.nav3cookbook.sample.listdetail.ListDetailNavigation
import com.nav3cookbook.sample.multitab.MultitabNavigation
import com.nav3cookbook.sample.ui.theme.Nav3CookbookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Nav3CookbookTheme {
                var selected by rememberSaveable { mutableStateOf<Sample?>(null) }
                when (selected) {
                    Sample.Basic -> BasicNavigation(onExit = { selected = null })
                    Sample.Multitab -> MultitabNavigation(onExit = { selected = null })
                    Sample.ListDetail -> ListDetailNavigation(onExit = { selected = null })
                    null -> SampleChooser(onSelect = { selected = it })
                }
            }
        }
    }
}

enum class Sample { Basic, Multitab, ListDetail }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleChooser(onSelect: (Sample) -> Unit) {
    Scaffold(topBar = { TopAppBar(title = { Text("Nav3 Cookbook") }) }) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(onClick = { onSelect(Sample.Basic) }) {
                Column(Modifier.padding(16.dp)) {
                    Text("Basic Navigation", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "NavKey + entryProvider + dropUnlessResumed",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Card(onClick = { onSelect(Sample.Multitab) }) {
                Column(Modifier.padding(16.dp)) {
                    Text("Multi-Tab Navigation", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "BottomNavigationBar + per-tab back stack",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Card(onClick = { onSelect(Sample.ListDetail) }) {
                Column(Modifier.padding(16.dp)) {
                    Text("List-Detail Adaptive", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Material3 ListDetailSceneStrategy",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
