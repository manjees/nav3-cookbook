package com.nav3cookbook.sample.multitab

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object TabHomeKey : NavKey

@Serializable
data object TabSearchKey : NavKey

@Serializable
data object TabProfileKey : NavKey

@Serializable
data class TabDetailKey(val tabName: String, val id: String) : NavKey

val TOP_LEVEL_TABS: List<NavKey> = listOf(TabHomeKey, TabSearchKey, TabProfileKey)
