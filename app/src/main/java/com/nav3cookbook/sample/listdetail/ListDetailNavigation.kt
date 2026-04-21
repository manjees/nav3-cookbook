@file:OptIn(androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi::class)

package com.nav3cookbook.sample.listdetail

import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.nav3cookbook.sample.listdetail.screens.ItemDetailScreen
import com.nav3cookbook.sample.listdetail.screens.ItemListScreen

/**
 * Uses Material3 [ListDetailSceneStrategy] to render list + detail side by side on wide screens,
 * and stacked on narrow screens. The strategy handles using the LIST entry's contentKey as the
 * scene key, preventing unwanted scene animations when only the detail pane changes.
 */
@Composable
fun ListDetailNavigation(onExit: () -> Unit) {
    val backStack = rememberNavBackStack(ItemListKey)
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    NavDisplay(
        backStack = backStack,
        onBack = {
            if (backStack.size > 1) backStack.removeLastOrNull() else onExit()
        },
        sceneStrategies = listOf(listDetailStrategy),
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<ItemListKey>(
                metadata = ListDetailSceneStrategy.listPane()
            ) {
                ItemListScreen(
                    onItemClick = { id -> backStack.add(ItemDetailKey(id)) },
                    onExit = onExit
                )
            }
            entry<ItemDetailKey>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) { key ->
                ItemDetailScreen(
                    id = key.id,
                    onBack = { backStack.removeLastOrNull() }
                )
            }
        }
    )
}
