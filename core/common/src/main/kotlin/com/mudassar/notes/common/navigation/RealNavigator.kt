package com.mudassar.notes.common.navigation

import com.mudassar.notes.navigation.NavigationEvent
import com.mudassar.notes.navigation.Navigator
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RealNavigator @Inject constructor() : Navigator {
    val flow = MutableSharedFlow<NavigationEvent>(
        extraBufferCapacity = 1
    )
    override fun navigate(event: NavigationEvent) {
        flow.tryEmit(event)
    }
}