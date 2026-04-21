package com.nav3cookbook.sample.listdetail.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemListScreen(onItemClick: (String) -> Unit, onExit: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Items") },
                navigationIcon = { TextButton(onClick = onExit) { Text("Exit") } }
            )
        }
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner),
            contentPadding = PaddingValues(16.dp)
        ) {
            items((1..30).map { "ld-$it" }) { id ->
                Card(
                    onClick = dropUnlessResumed { onItemClick(id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text("Item $id", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}
