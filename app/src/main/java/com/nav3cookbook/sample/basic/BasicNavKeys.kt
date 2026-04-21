package com.nav3cookbook.sample.basic

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object BasicHomeKey : NavKey

@Serializable
data class BasicDetailKey(val id: String) : NavKey
