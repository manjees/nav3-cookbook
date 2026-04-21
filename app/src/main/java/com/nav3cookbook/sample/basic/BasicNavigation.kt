package com.nav3cookbook.sample.basic

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nav3cookbook.sample.basic.screens.BasicDetailScreen
import com.nav3cookbook.sample.basic.screens.BasicHomeScreen

@Composable
fun BasicNavigation(onExit: () -> Unit) {
    val backStack = rememberNavBackStack(BasicHomeKey)
    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) backStack.removeLastOrNull() else onExit()
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<BasicHomeKey> {
                BasicHomeScreen(
                    onItemClick = { id -> backStack.add(BasicDetailKey(id)) },
                    onExit = onExit
                )
            }
            entry<BasicDetailKey> { key ->
                BasicDetailScreen(
                    id = key.id,
                    onBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
