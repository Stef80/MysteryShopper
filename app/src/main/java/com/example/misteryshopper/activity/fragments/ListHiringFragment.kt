package com.example.misteryshopper.activity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.misteryshopper.utils.SharedPrefConfig

class ListHiringFragment : Fragment() {

    private lateinit var prefConfig: SharedPrefConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefConfig = SharedPrefConfig(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val userEmail = prefConfig.readLoggedUser().email
                if (userEmail != null) {
                    ListHiringScreen(userEmail = userEmail)
                }
            }
        }
    }

    companion object {
        private const val ARG_COLUMN_COUNT = "column-count"
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ListHiringFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}
