package com.nav3cookbook.sample.multitab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack

/**
 * Multi-tab navigation state. Each top-level tab owns an independent [NavBackStack],
 * so switching tabs preserves each tab's own back history.
 */
@Stable
class NavigationState(
    val startTab: NavKey,
    val tabs: List<NavKey>,
    internal val backStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    var topLevelTab: NavKey by mutableStateOf(startTab)
        private set

    val currentBackStack: NavBackStack<NavKey>
        get() = backStacks.getValue(topLevelTab)

    fun switchTab(tab: NavKey) {
        require(tab in tabs) { "Unknown tab: $tab" }
        if (topLevelTab == tab) {
            // Re-tap current tab: pop to root of that tab.
            val stack = backStacks.getValue(tab)
            while (stack.size > 1) stack.removeAt(stack.lastIndex)
        } else {
            topLevelTab = tab
        }
    }
}

@Composable
fun rememberNavigationState(startTab: NavKey, tabs: List<NavKey>): NavigationState {
    // Each tab gets its own rememberNavBackStack so state is saved/restored independently.
    val backStacks: Map<NavKey, NavBackStack<NavKey>> = tabs.associateWith { tab ->
        rememberNavBackStack(tab)
    }
    return remember(startTab, tabs) {
        NavigationState(startTab, tabs, backStacks)
    }
}
