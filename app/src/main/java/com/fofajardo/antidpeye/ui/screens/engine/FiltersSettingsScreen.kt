@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.fofajardo.antidpeye.ui.screens.engine

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.fofajardo.antidpeye.R
import com.fofajardo.antidpeye.data.AppSettings
import com.fofajardo.antidpeye.ui.components.EditTextPreferenceItem
import com.fofajardo.antidpeye.ui.components.ListPreferenceItem
import com.fofajardo.antidpeye.ui.components.Section
import com.fofajardo.antidpeye.ui.components.segmentedShapeBottom
import com.fofajardo.antidpeye.ui.components.segmentedShapeTop

@Composable
fun FiltersSettingsScreen(
    settings: AppSettings,
    onUpdateSettings: ((AppSettings) -> AppSettings) -> Unit,
    modifier: Modifier = Modifier,
) {
    val engine = settings.engine

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        Section {
            ListPreferenceItem(
                shapes = segmentedShapeTop(),
                title = stringResource(R.string.byedpi_hosts_mode_setting),
                selectedValue = engine.hostsMode,
                entries = stringArrayResource(R.array.byedpi_hosts_modes).toList(),
                entryValues = stringArrayResource(R.array.byedpi_hosts_modes_entries).toList(),
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(hostsMode = newValue)) }
                },
            )
            if (engine.hostsMode == "blacklist") {
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_hosts_blacklist_setting),
                    value = engine.hostsBlacklist,
                    onValueChange = { newValue ->
                        onUpdateSettings { it.copy(engine = it.engine.copy(hostsBlacklist = newValue)) }
                    },
                )
            }
            if (engine.hostsMode == "whitelist") {
                EditTextPreferenceItem(
                    title = stringResource(R.string.byedpi_hosts_whitelist_setting),
                    value = engine.hostsWhitelist,
                    onValueChange = { newValue ->
                        onUpdateSettings { it.copy(engine = it.engine.copy(hostsWhitelist = newValue)) }
                    },
                )
            }
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_pf_setting),
                value = engine.pf,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(pf = newValue)) }
                },
            )
            EditTextPreferenceItem(
                shapes = segmentedShapeBottom(),
                title = stringResource(R.string.byedpi_ipset_setting),
                value = engine.ipset,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(ipset = newValue)) }
                },
            )
        }
    }
}
