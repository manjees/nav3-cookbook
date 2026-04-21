package com.nav3cookbook.sample.multitab

import androidx.navigation3.runtime.NavKey

/**
 * Thin wrapper exposing navigation operations over [NavigationState].
 * Keeps UI code free of back-stack manipulation details.
 */
class Navigator(private val state: NavigationState) {
    fun push(key: NavKey) {
        state.currentBackStack.add(key)
    }

    fun pop(): Boolean {
        val stack = state.currentBackStack
        return if (stack.size > 1) {
            stack.removeAt(stack.lastIndex)
            true
        } else {
            false
        }
    }

    fun selectTab(tab: NavKey) = state.switchTab(tab)
}
