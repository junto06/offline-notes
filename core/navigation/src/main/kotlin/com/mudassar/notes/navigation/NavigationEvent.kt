package com.mudassar.notes.navigation

import androidx.navigation.NavDirections

sealed interface NavigationEvent

data class FragmentDestinationEvent(
    val direction: NavDirections
) : NavigationEvent

data class Deeplink(
    val uri: String
) : NavigationEvent

data class PopBackStack(
    val upto: Int?,
    val inclusive: Boolean
) : NavigationEvent