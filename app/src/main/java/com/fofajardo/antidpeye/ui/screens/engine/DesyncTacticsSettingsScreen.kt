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
import com.fofajardo.antidpeye.ui.components.SwitchPreferenceItem
import com.fofajardo.antidpeye.ui.components.segmentedShapeBottom
import com.fofajardo.antidpeye.ui.components.segmentedShapeTop

@Composable
fun DesyncTacticsSettingsScreen(
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
                title = stringResource(R.string.byedpi_desync_method_setting),
                selectedValue = engine.desyncMethod,
                entries = stringArrayResource(R.array.byedpi_desync_methods).toList(),
                entryValues = stringArrayResource(R.array.byedpi_desync_methods_entries).toList(),
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(desyncMethod = newValue)) }
                },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_split_position_setting),
                value = engine.splitPosition,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(splitPosition = newValue)) }
                },
                validate = { it.toIntOrNull() != null },
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.byedpi_split_at_host_setting),
                checked = engine.splitAtHost,
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(splitAtHost = checked)) }
                },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_default_ttl_setting),
                summary = stringResource(R.string.byedpi_default_ttl_summary),
                value = engine.defaultTtl,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(defaultTtl = newValue)) }
                },
                validate = { it.toIntOrNull()?.let { t -> t in 0..255 } ?: false },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_fake_ttl_setting),
                summary = stringResource(R.string.byedpi_fake_ttl_summary),
                value = engine.fakeTtl,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(fakeTtl = newValue)) }
                },
                validate = { it.toIntOrNull()?.let { t -> t in 1..255 } ?: false },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_fake_offset_setting),
                value = engine.fakeOffset,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(fakeOffset = newValue)) }
                },
                validate = { it.toIntOrNull() != null },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.sni_of_fake_packet),
                summary = stringResource(R.string.sni_of_fake_packet_summary),
                value = engine.fakeSni,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(fakeSni = newValue)) }
                },
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.byedpi_md5sig_setting),
                summary = stringResource(R.string.byedpi_md5sig_summary),
                checked = engine.md5sig,
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(md5sig = checked)) }
                },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_fake_data_setting),
                value = engine.fakeData,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(fakeData = newValue)) }
                },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_fake_tls_mod_setting),
                value = engine.fakeTlsMod,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(fakeTlsMod = newValue)) }
                },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_tlsminor_setting),
                value = engine.tlsminor,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(tlsminor = newValue)) }
                },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_round_setting),
                summary = stringResource(R.string.byedpi_round_summary),
                value = engine.round,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(round = newValue)) }
                },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.oob_data),
                value = engine.oobData,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(oobData = newValue)) }
                },
                validate = { it.length <= 1 },
            )
            SwitchPreferenceItem(
                shapes = segmentedShapeBottom(),
                title = stringResource(R.string.byedpi_drop_sack_setting),
                checked = engine.dropSack,
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(dropSack = checked)) }
                },
            )
        }
    }
}
