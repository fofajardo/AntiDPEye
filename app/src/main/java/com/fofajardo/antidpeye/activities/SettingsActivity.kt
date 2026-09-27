package com.fofajardo.antidpeye.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.fofajardo.antidpeye.R
import com.fofajardo.antidpeye.data.AppSettings
import com.fofajardo.antidpeye.data.SettingsRepository
import com.fofajardo.antidpeye.ui.screens.EngineSettingsScreen
import com.fofajardo.antidpeye.ui.screens.MainSettingsScreen
import com.fofajardo.antidpeye.ui.screens.VpnAppsFilterScreen
import com.fofajardo.antidpeye.ui.screens.engine.AutoSettingsScreen
import com.fofajardo.antidpeye.ui.screens.engine.DesyncTacticsSettingsScreen
import com.fofajardo.antidpeye.ui.screens.engine.FiltersSettingsScreen
import com.fofajardo.antidpeye.ui.screens.engine.ProtocolsSettingsScreen
import com.fofajardo.antidpeye.ui.screens.engine.ProxyConnectionSettingsScreen
import com.fofajardo.antidpeye.ui.theme.ByeDpiTheme
import com.fofajardo.antidpeye.utility.getSettingsRepository
import kotlinx.coroutines.launch

enum class SettingsDestination {
    MAIN,
    ENGINE_SETTINGS,
    ENGINE_PROXY,
    ENGINE_DESYNC,
    ENGINE_PROTOCOLS,
    ENGINE_FILTERS,
    ENGINE_AUTO,
    VPN_APPS_FILTER,
}

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = getSettingsRepository()

        setContent {
            val settings by repository.settingsFlow.collectAsState(initial = AppSettings())

            ByeDpiTheme(appTheme = settings.theme, amoledTheme = settings.amoledTheme) {
                SettingsApp(
                    settings = settings,
                    repository = repository,
                    onFinish = { finish() },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsApp(
    settings: AppSettings,
    repository: SettingsRepository,
    onFinish: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val navBackStack = remember { mutableStateListOf(SettingsDestination.MAIN) }
    val currentDestination = navBackStack.last()
    var isPop by remember { mutableStateOf(false) }

    var showResetDialog by remember { mutableStateOf(false) }

    val onUpdateSettings: ((AppSettings) -> AppSettings) -> Unit = { transform ->
        coroutineScope.launch {
            repository.update(transform)
        }
    }

    val navigateTo: (SettingsDestination) -> Unit = { dest ->
        isPop = false
        navBackStack.add(dest)
    }

    val navigateUp: () -> Unit = {
        if (navBackStack.size > 1) {
            isPop = true
            navBackStack.removeAt(navBackStack.lastIndex)
        } else {
            onFinish()
        }
    }

    BackHandler(enabled = true) {
        navigateUp()
    }

    val title = when (currentDestination) {
        SettingsDestination.MAIN -> stringResource(R.string.title_settings)
        SettingsDestination.ENGINE_SETTINGS -> stringResource(R.string.engine_settings)
        SettingsDestination.ENGINE_PROXY -> stringResource(R.string.byedpi_proxy)
        SettingsDestination.ENGINE_DESYNC -> stringResource(R.string.byedpi_desync)
        SettingsDestination.ENGINE_PROTOCOLS -> stringResource(R.string.byedpi_protocols_category)
        SettingsDestination.ENGINE_FILTERS -> stringResource(R.string.byedpi_filter_category)
        SettingsDestination.ENGINE_AUTO -> stringResource(R.string.byedpi_auto_category)
        SettingsDestination.VPN_APPS_FILTER -> stringResource(R.string.vpn_filtered_apps)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    if (currentDestination == SettingsDestination.MAIN) {
                        IconButton(onClick = { showResetDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.reset_settings),
                            )
                        }
                    } else if (currentDestination == SettingsDestination.ENGINE_SETTINGS) {
                        val docsUrl = stringResource(R.string.byedpi_docs)
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(docsUrl))
                                context.startActivity(intent)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = stringResource(R.string.documentation),
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
    ) { innerPadding ->
        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                title = { Text(stringResource(R.string.reset_settings)) },
                text = { Text("Reset all settings to default?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            coroutineScope.launch {
                                repository.resetToDefault()
                            }
                            showResetDialog = false
                        }
                    ) {
                        Text(stringResource(R.string.reset_settings))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
        AnimatedContent(
            targetState = currentDestination,
            transitionSpec = {
                if (isPop) {
                    (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> width } + fadeOut()
                    )
                } else {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width } + fadeOut()
                    )
                }
            },
            label = "settings_nav",
            modifier = Modifier.padding(innerPadding),
        ) { destination ->
            when (destination) {
                SettingsDestination.MAIN -> {
                    MainSettingsScreen(
                        settings = settings,
                        onUpdateSettings = onUpdateSettings,
                        onNavigateToEngineSettings = { navigateTo(SettingsDestination.ENGINE_SETTINGS) },
                        onNavigateToVpnAppsFilter = { navigateTo(SettingsDestination.VPN_APPS_FILTER) },
                    )
                }

                SettingsDestination.ENGINE_SETTINGS -> {
                    EngineSettingsScreen(
                        settings = settings,
                        onUpdateSettings = onUpdateSettings,
                        onNavigateToProxy = { navigateTo(SettingsDestination.ENGINE_PROXY) },
                        onNavigateToDesync = { navigateTo(SettingsDestination.ENGINE_DESYNC) },
                        onNavigateToProtocols = { navigateTo(SettingsDestination.ENGINE_PROTOCOLS) },
                        onNavigateToFilters = { navigateTo(SettingsDestination.ENGINE_FILTERS) },
                        onNavigateToAuto = { navigateTo(SettingsDestination.ENGINE_AUTO) },
                    )
                }

                SettingsDestination.ENGINE_PROXY -> {
                    ProxyConnectionSettingsScreen(
                        settings = settings,
                        onUpdateSettings = onUpdateSettings,
                    )
                }

                SettingsDestination.ENGINE_DESYNC -> {
                    DesyncTacticsSettingsScreen(
                        settings = settings,
                        onUpdateSettings = onUpdateSettings,
                    )
                }

                SettingsDestination.ENGINE_PROTOCOLS -> {
                    ProtocolsSettingsScreen(
                        settings = settings,
                        onUpdateSettings = onUpdateSettings,
                    )
                }

                SettingsDestination.ENGINE_FILTERS -> {
                    FiltersSettingsScreen(
                        settings = settings,
                        onUpdateSettings = onUpdateSettings,
                    )
                }

                SettingsDestination.ENGINE_AUTO -> {
                    AutoSettingsScreen(
                        settings = settings,
                        onUpdateSettings = onUpdateSettings,
                    )
                }

                SettingsDestination.VPN_APPS_FILTER -> {
                    VpnAppsFilterScreen(
                        checkedPackages = settings.vpnFilteredApps,
                        onTogglePackage = { pkg, checked ->
                            onUpdateSettings { current ->
                                val updated = current.vpnFilteredApps.toMutableSet().apply {
                                    if (checked) {
                                        add(pkg)
                                    } else {
                                        remove(pkg)
                                    }
                                }
                                current.copy(vpnFilteredApps = updated)
                            }
                        },
                    )
                }
            }
        }
    }
}
