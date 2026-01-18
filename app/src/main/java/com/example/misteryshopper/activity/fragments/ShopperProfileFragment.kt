package com.example.misteryshopper.activity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.misteryshopper.R
import com.example.misteryshopper.models.ShopperModel

class ShopperProfileFragment : Fragment() {

    private var shopperModel: ShopperModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            shopperModel = it.getSerializable(ARG_PARAM1) as? ShopperModel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                shopperModel?.let {
                    ShopperProfileScreen(it)
                }
            }
        }
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        @JvmStatic
        fun newInstance(model: ShopperModel) =
            ShopperProfileFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, model)
                }
            }
    }
}

@Composable
fun ShopperProfileScreen(shopper: ShopperModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp, vertical = 15.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProfileLabel(stringResource(id = R.string.complete_name))
        Row {
            ProfileInfo(shopper.name ?: "")
            Spacer(modifier = Modifier.width(5.dp))
            ProfileInfo(shopper.surname ?: "")
        }

        Spacer(modifier = Modifier.height(8.dp))
        ProfileLabel(stringResource(id = R.string.complete_address))
        Row {
            ProfileInfo(shopper.address ?: "")
            ProfileInfo(", ")
            ProfileInfo(shopper.city ?: "")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Column(modifier = Modifier.weight(1f)) {
                ProfileLabel(stringResource(id = R.string.prompt_email))
                ProfileInfo(shopper.email ?: "")
            }
            Column(modifier = Modifier.weight(1f)) {
                ProfileLabel(stringResource(id = R.string.cf))
                ProfileInfo(shopper.cf ?: "")
            }
        }
    }
}

@Composable
fun ProfileLabel(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = MaterialTheme.colors.onSurface
    )
}

@Composable
fun ProfileInfo(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colors.onSurface
    )
}
