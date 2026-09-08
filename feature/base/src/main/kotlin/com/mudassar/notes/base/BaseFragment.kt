package com.mudassar.notes.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.mudassar.notes.design.NotesTheme

abstract class BaseFragment : Fragment() {

    @Composable
    abstract fun UiContent()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent {
            NotesTheme {
                UiContent()
            }
        }
    }
}