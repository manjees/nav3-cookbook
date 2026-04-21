package com.nav3cookbook.sample.listdetail

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object ItemListKey : NavKey

@Serializable
data class ItemDetailKey(val id: String) : NavKey
