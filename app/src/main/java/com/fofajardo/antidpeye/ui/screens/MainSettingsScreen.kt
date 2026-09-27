@file:OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)

package com.fofajardo.antidpeye.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.fofajardo.antidpeye.BuildConfig
import com.fofajardo.antidpeye.R
import com.fofajardo.antidpeye.activities.MainActivity
import com.fofajardo.antidpeye.data.AppSettings
import com.fofajardo.antidpeye.ui.components.EditTextPreferenceItem
import com.fofajardo.antidpeye.ui.components.ListPreferenceItem
import com.fofajardo.antidpeye.ui.components.PreferenceItem
import com.fofajardo.antidpeye.ui.components.Section
import com.fofajardo.antidpeye.ui.components.SwitchPreferenceItem
import com.fofajardo.antidpeye.ui.components.segmentedShapeBottom
import com.fofajardo.antidpeye.ui.components.segmentedShapeTop
import com.fofajardo.antidpeye.ui.fragments.ServiceActiveWarningCardFragment
import com.fofajardo.antidpeye.utility.checkNotLocalIp

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.rememberCoroutineScope
import com.fofajardo.antidpeye.utility.saveLogsToUri
import kotlinx.coroutines.launch

@Composable
fun MainSettingsScreen(
    settings: AppSettings,
    onUpdateSettings: ((AppSettings) -> AppSettings) -> Unit,
    onNavigateToEngineSettings: () -> Unit,
    onNavigateToVpnAppsFilter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isVpnMode = settings.mode == "vpn"

    val saveLogsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain"),
    ) { uri ->
        if (uri != null) {
            coroutineScope.launch {
                saveLogsToUri(context, uri)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        ServiceActiveWarningCardFragment()

        Section(title = stringResource(R.string.appearance_category)) {
            ListPreferenceItem(
                shapes = segmentedShapeTop(),
                title = stringResource(R.string.theme_settings),
                selectedValue = settings.theme,
                entries = stringArrayResource(R.array.themes).toList(),
                entryValues = stringArrayResource(R.array.themes_entries).toList(),
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(theme = newValue) }
                    MainActivity.applyAppTheme(newValue)
                },
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.amoled_theme_setting),
                checked = settings.amoledTheme,
                shapes = segmentedShapeBottom(),
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(amoledTheme = checked) }
                },
            )
        }

        Section(title = stringResource(R.string.general_category)) {
            ListPreferenceItem(
                shapes = segmentedShapeTop(),
                title = stringResource(R.string.mode_setting),
                selectedValue = settings.mode,
                entries = stringArrayResource(R.array.byedpi_modes).toList(),
                entryValues = stringArrayResource(R.array.byedpi_modes_entries).toList(),
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(mode = newValue) }
                },
            )
            PreferenceItem(
                title = stringResource(R.string.engine_settings),
                summary = if (settings.engine.enableCmdSettings) {
                    stringResource(R.string.tab_cli)
                } else {
                    stringResource(R.string.tab_visual)
                },
                onClick = onNavigateToEngineSettings,
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.autostart_setting),
                checked = settings.autostart,
                shapes = segmentedShapeBottom(),
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(autostart = checked) }
                },
            )
        }

        if (isVpnMode) {
            Section(title = stringResource(R.string.vpn_category)) {
                EditTextPreferenceItem(
                    shapes = segmentedShapeTop(),
                    title = stringResource(R.string.dbs_ip_setting),
                    value = settings.dnsIp,
                    onValueChange = { newValue ->
                        onUpdateSettings { it.copy(dnsIp = newValue) }
                    },
                    validate = { it.isBlank() || checkNotLocalIp(it) },
                )
                SwitchPreferenceItem(
                    title = stringResource(R.string.ipv6_setting),
                    summary = stringResource(R.string.ipv6_setting_summary),
                    checked = settings.ipv6Enable,
                    onCheckedChange = { checked ->
                        onUpdateSettings { it.copy(ipv6Enable = checked) }
                    },
                )
                ListPreferenceItem(
                    title = stringResource(R.string.vpn_filter_mode),
                    selectedValue = settings.vpnFilterMode,
                    entries = stringArrayResource(R.array.vpn_filtering_modes).toList(),
                    entryValues = stringArrayResource(R.array.vpn_filtering_modes_entries).toList(),
                    onValueChange = { newValue ->
                        onUpdateSettings { it.copy(vpnFilterMode = newValue) }
                    },
                )
                PreferenceItem(
                    title = stringResource(R.string.vpn_filtered_apps),
                    shapes = segmentedShapeBottom(),
                    onClick = onNavigateToVpnAppsFilter,
                )
            }
        }

        Section(title = stringResource(R.string.about_category)) {
            PreferenceItem(
                shapes = segmentedShapeTop(),
                title = stringResource(R.string.version),
                summary = BuildConfig.VERSION_NAME,
            )
            PreferenceItem(
                title = stringResource(R.string.source_code_link),
                onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://github.com/fofajardo/AntiDPEye"),
                    )
                    context.startActivity(intent)
                },
            )
            PreferenceItem(
                title = stringResource(R.string.save_logs),
                shapes = segmentedShapeBottom(),
                onClick = {
                    saveLogsLauncher.launch("byedpi.log")
                },
            )
        }
    }
}
