package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.Transaction
import com.example.data.DebtCredit
import kotlinx.coroutines.launch

import com.example.ui.theme.ThemeOption
import androidx.compose.material.icons.filled.Settings

@Composable
fun TrackerApp(
    viewModel: FinanceViewModel, 
    currentTheme: ThemeOption, 
    onThemeChange: (ThemeOption) -> Unit,
    onLogout: () -> Unit,
    onLogin: (String) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()

    if (currentUser == null) {
        AuthScreen(
            viewModel = viewModel,
            onAuthSuccess = onLogin
        )
        return
    }

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    val transactions by viewModel.transactions.collectAsState()
    val debts by viewModel.debts.collectAsState()
    val baInvestments by viewModel.baInvestments.collectAsState()
    val kuriList by viewModel.kuri.collectAsState()
    
    var showAddTxModal by remember { mutableStateOf(false) }
    var txToEdit by remember { mutableStateOf<Transaction?>(null) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface,
            ) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Tracker",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                    color = MaterialTheme.colorScheme.primary
                )
                Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                DrawerItem(
                    navController = navController,
                    route = "dashboard",
                    icon = Icons.Default.Dashboard,
                    label = "Dashboard",
                    closeDrawer = { scope.launch { drawerState.close() } }
                )
                DrawerItem(
                    navController = navController,
                    route = "transactions",
                    icon = Icons.Default.List,
                    label = "Transactions",
                    closeDrawer = { scope.launch { drawerState.close() } }
                )
                DrawerItem(
                    navController = navController,
                    route = "debts",
                    icon = Icons.Default.People,
                    label = "Debt & Credit",
                    closeDrawer = { scope.launch { drawerState.close() } }
                )
                DrawerItem(
                    navController = navController,
                    route = "investments",
                    icon = Icons.Default.TrendingUp,
                    label = "Investments & Kuri",
                    closeDrawer = { scope.launch { drawerState.close() } }
                )
                DrawerItem(
                    navController = navController,
                    route = "planning",
                    icon = Icons.Default.DateRange,
                    label = "Monthly Planning",
                    closeDrawer = { scope.launch { drawerState.close() } }
                )
                DrawerItem(
                    navController = navController,
                    route = "settings",
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    closeDrawer = { scope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

        Scaffold(
            topBar = {
                val title = when (currentRoute) {
                    "dashboard" -> "Dashboard"
                    "transactions" -> "Transactions"
                    "debts" -> "Debt & Credit"
                    "investments" -> "Investments & Kuri"
                    "planning" -> "Monthly Planning"
                    else -> "Tracker"
                }
                
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { Text(title, fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            floatingActionButton = {
                if (currentRoute != "debts" && currentRoute != "investments") {
                    FloatingActionButton(
                        onClick = { 
                            txToEdit = null
                            showAddTxModal = true 
                        },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Transaction")
                    }
                }
            },
            contentWindowInsets = WindowInsets.safeDrawing
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "dashboard",
                modifier = Modifier.padding(innerPadding).fillMaxSize()
            ) {
                composable("dashboard") {
                    DashboardScreen(transactions, debts, baInvestments, kuriList)
                }
                composable("transactions") {
                    TransactionsScreen(
                        transactions, 
                        onDelete = { viewModel.deleteTransaction(it) },
                        onEdit = { 
                            txToEdit = it
                            showAddTxModal = true
                        }
                    )
                }
                composable("debts") {
                    DebtsScreen(
                        debts = debts,
                        onAdd = { viewModel.addDebt(it) },
                        onDelete = { viewModel.deleteDebt(it) },
                        onUpdate = { viewModel.addDebt(it) }
                    )
                }
                composable("investments") {
                    InvestmentsScreen(
                        baInvestments = baInvestments,
                        onAddBAInvestment = { viewModel.addBAInvestment(it) },
                        onDeleteBAInvestment = { viewModel.deleteBAInvestment(it) },
                        onUpdateBAInvestment = { viewModel.addBAInvestment(it) },
                        kuriList = kuriList,
                        onAddKuri = { viewModel.addKuri(it) },
                        onDeleteKuri = { viewModel.deleteKuri(it) }
                    )
                }
                composable("planning") {
                    PlanningScreen(transactions)
                }
                composable("settings") {
                    SettingsScreen(currentTheme, onThemeChange, onLogout)
                }
            }
        }
    }
    
    if (showAddTxModal) {
        AddTransactionDialog(
            txToEdit = txToEdit,
            onDismiss = { 
                showAddTxModal = false
                txToEdit = null
            },
            onSave = { 
                viewModel.addTransaction(it)
                showAddTxModal = false
                txToEdit = null
            }
        )
    }
}

@Composable
private fun DrawerItem(
    navController: NavHostController,
    route: String,
    icon: ImageVector,
    label: String,
    closeDrawer: () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val selected = currentRoute == route

    val backgroundColor = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    val contentColor = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable {
                navController.navigate(route) {
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
                closeDrawer()
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = label, tint = contentColor)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, color = contentColor, fontWeight = FontWeight.Medium)
    }
}
