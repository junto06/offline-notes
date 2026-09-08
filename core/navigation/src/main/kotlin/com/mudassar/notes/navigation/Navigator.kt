package com.mudassar.notes.navigation

import androidx.navigation.NavDirections

interface Navigator {
    fun navigate(event: NavigationEvent)
}

fun Navigator.direction(direction: NavDirections) =
    navigate(FragmentDestinationEvent(direction))

fun Navigator.openDeeplink(uri: String) =
    navigate(Deeplink(uri))

fun Navigator.popBackStack(
    upto: Int? = null,
    inclusive: Boolean = false
) = navigate(PopBackStack(upto, inclusive))