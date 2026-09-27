@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.fofajardo.antidpeye.ui.screens.engine

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fofajardo.antidpeye.R
import com.fofajardo.antidpeye.data.AppSettings
import com.fofajardo.antidpeye.ui.components.EditTextPreferenceItem
import com.fofajardo.antidpeye.ui.components.Section
import com.fofajardo.antidpeye.ui.components.SwitchPreferenceItem
import com.fofajardo.antidpeye.ui.components.segmentedShapeBottom
import com.fofajardo.antidpeye.ui.components.segmentedShapeTop

@Composable
fun ProtocolsSettingsScreen(
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
            SwitchPreferenceItem(
                shapes = segmentedShapeTop(),
                title = stringResource(R.string.desync_http),
                checked = engine.desyncHttp,
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(desyncHttp = checked)) }
                },
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.desync_https),
                checked = engine.desyncHttps,
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(desyncHttps = checked)) }
                },
            )
            SwitchPreferenceItem(
                shapes = segmentedShapeBottom(),
                title = stringResource(R.string.desync_udp),
                checked = engine.desyncUdp,
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(desyncUdp = checked)) }
                },
            )
        }

        Section(title = stringResource(R.string.byedpi_protocol_mod_category)) {
            SwitchPreferenceItem(
                shapes = segmentedShapeTop(),
                title = stringResource(R.string.byedpi_host_mixed_case_setting),
                summary = stringResource(R.string.byedpi_host_mixed_case_summary),
                checked = engine.hostMixedCase,
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(hostMixedCase = checked)) }
                },
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.byedpi_domain_mixed_case_setting),
                summary = stringResource(R.string.byedpi_domain_mixed_case_summary),
                checked = engine.domainMixedCase,
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(domainMixedCase = checked)) }
                },
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.byedpi_host_remove_spaces_setting),
                checked = engine.hostRemoveSpaces,
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(hostRemoveSpaces = checked)) }
                },
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.byedpi_tlsrec_enabled_setting),
                summary = stringResource(R.string.byedpi_tlsrec_enabled_summary),
                checked = engine.tlsrecEnabled,
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(tlsrecEnabled = checked)) }
                },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_tlsrec_position_setting),
                summary = stringResource(R.string.byedpi_tlsrec_position_summary),
                value = engine.tlsrecPosition,
                enabled = engine.tlsrecEnabled,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(tlsrecPosition = newValue)) }
                },
                validate = { it.toIntOrNull() != null },
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.byedpi_tlsrec_at_sni_setting),
                checked = engine.tlsrecAtSni,
                enabled = engine.tlsrecEnabled,
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(tlsrecAtSni = checked)) }
                },
            )
            EditTextPreferenceItem(
                shapes = segmentedShapeBottom(),
                title = stringResource(R.string.byedpi_udp_fake_count),
                value = engine.udpFakeCount,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(udpFakeCount = newValue)) }
                },
                validate = { it.toIntOrNull()?.let { c -> c >= 0 } ?: false },
            )
        }
    }
}
