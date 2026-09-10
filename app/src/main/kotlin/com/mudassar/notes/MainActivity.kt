package com.mudassar.notes

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.mudassar.notes.common.navigation.RealNavigator
import com.mudassar.notes.navigation.Deeplink
import com.mudassar.notes.navigation.NavigationEvent
import com.mudassar.notes.navigation.PopBackStack
import com.mudassar.notes.navigation.FragmentDestinationEvent
import com.mudassar.notes.sync.SingleScheduleFetch
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var navigator: RealNavigator

    @Inject
    lateinit var singleScheduleFetch: SingleScheduleFetch

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val controller =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        handleNavigation(controller.navController)
        if (savedInstanceState == null) {
            // trigger a refresh
            lifecycleScope.launch { singleScheduleFetch.schedule() }
        }
    }

    private fun handleNavigation(controller: NavController) {
        lifecycleScope.launch {
            navigator.flow.collect {
                controller.handleNavigationEvent(it)
            }
        }
    }

    private fun NavController.handleNavigationEvent(event: NavigationEvent) {
        when (event) {

            is FragmentDestinationEvent -> navigate(event.direction)

            is Deeplink -> navigate(event.uri.toUri())

            is PopBackStack -> {
                val success = if (event.upto != null) {
                    popBackStack(event.upto!!, event.inclusive)
                } else {
                    popBackStack()
                }

                if (!success) {
                    finish()
                }
            }
        }
    }
}
