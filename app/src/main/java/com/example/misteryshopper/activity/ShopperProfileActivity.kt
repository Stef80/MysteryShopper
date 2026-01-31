package com.example.misteryshopper.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.misteryshopper.MainActivity
import com.example.misteryshopper.R
import com.example.misteryshopper.activity.fragments.ListHiringScreen
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper
import com.example.misteryshopper.models.ShopperModel
import com.example.misteryshopper.utils.SharedPrefConfig
import com.example.misteryshopper.viewmodels.ShopperProfileUiState
import com.example.misteryshopper.viewmodels.ShopperProfileViewModel
import com.example.misteryshopper.viewmodels.ShopperProfileViewModelFactory
import kotlinx.coroutines.launch

class ShopperProfileActivity : ComponentActivity() {

    private val EMAIL = "email"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val config = SharedPrefConfig(applicationContext)
        val loggedUserEmail = config.readLoggedUser()?.email
        val profileEmail = intent.getStringExtra(EMAIL)

        setContent {
            ShopperProfileScreen(
                loggedUserEmail = loggedUserEmail,
                profileEmail = profileEmail
            )
        }
    }
}

@Composable
fun ShopperProfileScreen(
    loggedUserEmail: String?,
    profileEmail: String?,
    viewModel: ShopperProfileViewModel = viewModel(factory = ShopperProfileViewModelFactory())
) {
    val uiState by viewModel.uiState.collectAsState()
    val isCurrentUser = loggedUserEmail == profileEmail
    val context = LocalContext.current

    LaunchedEffect(key1 = profileEmail) {
        viewModel.loadShopper(profileEmail)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shopper Profile") },
                navigationIcon = if (!isCurrentUser) {
                    {
                        IconButton(onClick = { /* Navigate back */
                            val backIntent = Intent(context, ShopperListActivity::class.java)
                            backIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            context.startActivity(backIntent)
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                } else null,
                actions = {
                    if (isCurrentUser) {
                        IconButton(onClick = {
                            FirebaseDBHelper.instance.signOut(context)
                            context.startActivity(Intent(context, MainActivity::class.java))
                        }) {
                            Icon(Icons.Default.ExitToApp, contentDescription = "Logout")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            when (val state = uiState) {
                is ShopperProfileUiState.Loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is ShopperProfileUiState.Error -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = Color.Red)
                    }
                }
                is ShopperProfileUiState.Success -> {
                    val shopper = state.shopper
                    ProfileContent(shopper = shopper, isCurrentUser = isCurrentUser)
                }
            }
        }
    }
}

@Composable
fun ProfileContent(shopper: ShopperModel, isCurrentUser: Boolean) {
    Column {
        ProfileHeader(shopper = shopper)
        if (isCurrentUser) {
            TotalAmount(amount = shopper.totalAmount)
            CurrentUserPager(shopper = shopper)
        } else {
            OtherUserProfile(shopper = shopper)
        }
    }
}


@Composable
fun ProfileHeader(shopper: ShopperModel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.3f),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = shopper.imageUri,
            contentDescription = "Profile Image",
            modifier = Modifier.fillMaxSize().padding(20.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun TotalAmount(amount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Total", fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        Text("$amount €", fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CurrentUserPager(shopper: ShopperModel) {
    val tabTitles = listOf("Profile Info", "Hirings")
    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val scope = rememberCoroutineScope()

    Column {
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(title) }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> ProfileInfoScreen(shopper = shopper)
                1 -> ListHiringScreen(userEmail = shopper.email ?: "")
            }
        }
    }
}


@Composable
fun OtherUserProfile(shopper: ShopperModel) {
    ProfileInfoScreen(shopper = shopper)
}

@Composable
fun ProfileInfoScreen(shopper: ShopperModel) {
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


// Previews
@Preview(showBackground = true, name = "Current User Profile")
@Composable
fun CurrentShopperProfilePreview() {
    val mockShopper = ShopperModel(
        name = "John",
        surname = "Doe",
        city = "New York",
        address = "123 Main St",
        cf = "ABCDEF12G34H567I",
        totalAmount = 1250.75
    ).apply {
        email = "john.doe@example.com"
    }
    MaterialTheme {
        ProfileContent(shopper = mockShopper, isCurrentUser = true)
    }
}

@Preview(showBackground = true, name = "Other User Profile")
@Composable
fun OtherShopperProfilePreview() {
    val mockShopper = ShopperModel(
        name = "Jane",
        surname = "Smith",
        city = "London",
        address = "456 Baker St",
        cf = "ZYXWVU98T76S543R"
    ).apply {
        email = "jane.smith@example.com"
    }
    MaterialTheme {
        ProfileContent(shopper = mockShopper, isCurrentUser = false)
    }
}
