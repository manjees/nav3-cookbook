package com.nav3cookbook.sample.multitab

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nav3cookbook.sample.multitab.screens.TabDetailScreen
import com.nav3cookbook.sample.multitab.screens.TabHomeScreen
import com.nav3cookbook.sample.multitab.screens.TabProfileScreen
import com.nav3cookbook.sample.multitab.screens.TabSearchScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MultitabNavigation(onExit: () -> Unit) {
    val state = rememberNavigationState(startTab = TabHomeKey, tabs = TOP_LEVEL_TABS)
    val navigator = remember(state) { Navigator(state) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Multi-Tab") },
                navigationIcon = { TextButton(onClick = onExit) { Text("Exit") } }
            )
        },
        bottomBar = {
            NavigationBar {
                TOP_LEVEL_TABS.forEach { tab ->
                    NavigationBarItem(
                        selected = state.topLevelTab == tab,
                        onClick = dropUnlessResumed { navigator.selectTab(tab) },
                        icon = {
                            when (tab) {
                                TabHomeKey -> Icon(Icons.Default.Home, contentDescription = "home")
                                TabSearchKey -> Icon(Icons.Default.Search, contentDescription = "search")
                                TabProfileKey -> Icon(Icons.Default.Person, contentDescription = "profile")
                            }
                        },
                        label = {
                            when (tab) {
                                TabHomeKey -> Text("Home")
                                TabSearchKey -> Text("Search")
                                TabProfileKey -> Text("Profile")
                            }
                        }
                    )
                }
            }
        }
    ) { inner ->
        NavDisplay(
            backStack = state.currentBackStack,
            onBack = {
                if (!navigator.pop()) onExit()
            },
            modifier = Modifier.padding(inner),
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<TabHomeKey> {
                    TabHomeScreen(onDetail = { id ->
                        navigator.push(TabDetailKey("Home", id))
                    })
                }
                entry<TabSearchKey> {
                    TabSearchScreen(onDetail = { id ->
                        navigator.push(TabDetailKey("Search", id))
                    })
                }
                entry<TabProfileKey> {
                    TabProfileScreen(onDetail = { id ->
                        navigator.push(TabDetailKey("Profile", id))
                    })
                }
                entry<TabDetailKey> { key ->
                    TabDetailScreen(
                        tabName = key.tabName,
                        id = key.id,
                        onBack = { navigator.pop() }
                    )
                }
            }
        )
    }
}
