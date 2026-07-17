package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.database.AppDatabase
import com.example.data.repository.AppRepository
import com.example.ui.*
import com.example.ui.screens.*
import com.example.ui.theme.JetBlack
import com.example.ui.theme.MetallicGold
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SurfaceBorder
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Initialize Room components locally
                val context = LocalContext.current
                val database = remember { AppDatabase.getDatabase(context) }
                val repository = remember { AppRepository(database.appDao()) }
                
                // Initialize ViewModel
                val viewModel: TournamentViewModel = viewModel(
                    factory = TournamentViewModelFactory(application, repository)
                )

                MainResponsiveLayout(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainResponsiveLayout(viewModel: TournamentViewModel) {
    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    val currentScreen = viewModel.currentScreen

    // Drawer item click handler
    val onDrawerItemClick: (Screen) -> Unit = { screen ->
        viewModel.navigateTo(screen)
        scope.launch { drawerState.close() }
    }

    if (isTablet) {
        // Desktop / Tablet Landscape Navigation Rail layout
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(JetBlack)
        ) {
            NavigationRail(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MetallicGold,
                header = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsEsports,
                            contentDescription = null,
                            tint = MetallicGold,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "VACTORX",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MetallicGold,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
            ) {
                Spacer(modifier = Modifier.weight(1f))
                
                NavigationRailItem(
                    selected = currentScreen is Screen.Home,
                    onClick = { viewModel.navigateTo(Screen.Home) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = MetallicGold,
                        indicatorColor = MetallicGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationRailItem(
                    selected = currentScreen is Screen.Tournaments || currentScreen is Screen.RegistrationForm,
                    onClick = { viewModel.navigateTo(Screen.Tournaments) },
                    icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Tournaments") },
                    label = { Text("Lobby", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = MetallicGold,
                        indicatorColor = MetallicGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationRailItem(
                    selected = currentScreen is Screen.MyRegistrations,
                    onClick = { viewModel.navigateTo(Screen.MyRegistrations) },
                    icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Dashboard") },
                    label = { Text("My Signups", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = MetallicGold,
                        indicatorColor = MetallicGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationRailItem(
                    selected = currentScreen is Screen.Results,
                    onClick = { viewModel.navigateTo(Screen.Results) },
                    icon = { Icon(Icons.Default.WorkspacePremium, contentDescription = "Results") },
                    label = { Text("Results", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = MetallicGold,
                        indicatorColor = MetallicGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationRailItem(
                    selected = currentScreen is Screen.Leaderboard,
                    onClick = { viewModel.navigateTo(Screen.Leaderboard) },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Standings") },
                    label = { Text("Hall of Fame", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = MetallicGold,
                        indicatorColor = MetallicGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationRailItem(
                    selected = currentScreen is Screen.Rules,
                    onClick = { viewModel.navigateTo(Screen.Rules) },
                    icon = { Icon(Icons.Default.Shield, contentDescription = "Rules") },
                    label = { Text("Rules", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = MetallicGold,
                        indicatorColor = MetallicGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationRailItem(
                    selected = currentScreen is Screen.Contact,
                    onClick = { viewModel.navigateTo(Screen.Contact) },
                    icon = { Icon(Icons.Default.SupportAgent, contentDescription = "Help") },
                    label = { Text("Help", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = MetallicGold,
                        indicatorColor = MetallicGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                NavigationRailItem(
                    selected = currentScreen is Screen.AdminDashboard,
                    onClick = { viewModel.navigateTo(Screen.AdminDashboard) },
                    icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
                    label = { Text("Admin", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = MetallicGold,
                        indicatorColor = MetallicGold,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            VerticalDivider(color = SurfaceBorder, modifier = Modifier.fillMaxHeight())

            // Screen Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                ScreenContent(
                    screen = currentScreen,
                    viewModel = viewModel
                )
            }
        }
    } else {
        // Mobile Layout with Bottom Navigation Bar and Side Navigation Drawer for supplementary screens
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.width(280.dp)
                ) {
                    Text(
                        text = "VACTORX TOURNAMENTS",
                        style = MaterialTheme.typography.titleMedium.copy(color = MetallicGold),
                        modifier = Modifier.padding(24.dp)
                    )
                    
                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = MetallicGold) },
                        label = { Text("Match Results", fontWeight = FontWeight.Bold) },
                        selected = currentScreen is Screen.Results,
                        onClick = { onDrawerItemClick(Screen.Results) },
                        colors = NavigationDrawerItemDefaults.colors(unselectedTextColor = Color.Gray)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.Shield, contentDescription = null, tint = MetallicGold) },
                        label = { Text("Official Rulebook", fontWeight = FontWeight.Bold) },
                        selected = currentScreen is Screen.Rules,
                        onClick = { onDrawerItemClick(Screen.Rules) },
                        colors = NavigationDrawerItemDefaults.colors(unselectedTextColor = Color.Gray)
                    )

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.SupportAgent, contentDescription = null, tint = MetallicGold) },
                        label = { Text("Contact Support", fontWeight = FontWeight.Bold) },
                        selected = currentScreen is Screen.Contact,
                        onClick = { onDrawerItemClick(Screen.Contact) },
                        colors = NavigationDrawerItemDefaults.colors(unselectedTextColor = Color.Gray)
                    )

                    Divider(color = SurfaceBorder, modifier = Modifier.padding(vertical = 8.dp, horizontal = 24.dp))

                    NavigationDrawerItem(
                        icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MetallicGold) },
                        label = { Text("Admin Dashboard", fontWeight = FontWeight.Bold) },
                        selected = currentScreen is Screen.AdminDashboard,
                        onClick = { onDrawerItemClick(Screen.AdminDashboard) },
                        colors = NavigationDrawerItemDefaults.colors(unselectedTextColor = Color.Gray)
                    )
                }
            }
        ) {
            Scaffold(
                topBar = {
                    Column {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "VactorX Bullsaí eSports†",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        color = MetallicGold,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp
                                    )
                                )
                            },
                            navigationIcon = {
                                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MetallicGold)
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                                titleContentColor = MetallicGold,
                                navigationIconContentColor = MetallicGold
                            )
                        )
                        HorizontalDivider(color = SurfaceBorder)
                    }
                },
                bottomBar = {
                    Column {
                        HorizontalDivider(color = SurfaceBorder)
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MetallicGold
                        ) {
                            NavigationBarItem(
                            selected = currentScreen is Screen.Home,
                            onClick = { viewModel.navigateTo(Screen.Home) },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                            label = { Text("Home", fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = MetallicGold,
                                indicatorColor = MetallicGold,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            )
                        )

                        NavigationBarItem(
                            selected = currentScreen is Screen.Tournaments || currentScreen is Screen.RegistrationForm,
                            onClick = { viewModel.navigateTo(Screen.Tournaments) },
                            icon = { Icon(Icons.Default.SportsEsports, contentDescription = "Tournaments") },
                            label = { Text("Lobby", fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = MetallicGold,
                                indicatorColor = MetallicGold,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            )
                        )

                        NavigationBarItem(
                            selected = currentScreen is Screen.MyRegistrations,
                            onClick = { viewModel.navigateTo(Screen.MyRegistrations) },
                            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Dashboard") },
                            label = { Text("Signups", fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = MetallicGold,
                                indicatorColor = MetallicGold,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            )
                        )

                        NavigationBarItem(
                            selected = currentScreen is Screen.Leaderboard,
                            onClick = { viewModel.navigateTo(Screen.Leaderboard) },
                            icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Standings") },
                            label = { Text("Standings", fontWeight = FontWeight.Bold) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.Black,
                                selectedTextColor = MetallicGold,
                                indicatorColor = MetallicGold,
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    ScreenContent(
                        screen = currentScreen,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenContent(
    screen: Screen,
    viewModel: TournamentViewModel
) {
    when (screen) {
        is Screen.Home -> {
            HomeScreen(
                viewModel = viewModel,
                onRegisterClick = { t -> viewModel.navigateTo(Screen.RegistrationForm(t.id)) }
            )
        }
        is Screen.Tournaments -> {
            TournamentsScreen(
                viewModel = viewModel,
                onRegisterClick = { t -> viewModel.navigateTo(Screen.RegistrationForm(t.id)) }
            )
        }
        is Screen.RegistrationForm -> {
            RegistrationFormScreen(
                viewModel = viewModel,
                tournamentId = screen.tournamentId,
                onSuccess = { viewModel.navigateTo(Screen.MyRegistrations) }
            )
        }
        is Screen.MyRegistrations -> {
            MyRegistrationsScreen(viewModel = viewModel)
        }
        is Screen.Results -> {
            ResultsScreen(viewModel = viewModel)
        }
        is Screen.Leaderboard -> {
            LeaderboardScreen(viewModel = viewModel)
        }
        is Screen.Rules -> {
            RulesScreen()
        }
        is Screen.Contact -> {
            ContactScreen()
        }
        is Screen.AdminDashboard -> {
            AdminDashboardScreen(viewModel = viewModel)
        }
    }
}
