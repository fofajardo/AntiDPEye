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
import com.fofajardo.antidpeye.utility.checkIp

@Composable
fun ProxyConnectionSettingsScreen(
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
            EditTextPreferenceItem(
                shapes = segmentedShapeTop(),
                title = stringResource(R.string.bye_dpi_proxy_ip_setting),
                value = engine.proxyIp,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(proxyIp = newValue)) }
                },
                validate = { checkIp(it) },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_proxy_port_setting),
                value = engine.proxyPort,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(proxyPort = newValue)) }
                },
                validate = { it.toIntOrNull()?.let { p -> p in 1..65535 } ?: false },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_max_connections_setting),
                value = engine.maxConnections,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(maxConnections = newValue)) }
                },
                validate = { it.toIntOrNull()?.let { c -> c in 1..Short.MAX_VALUE } ?: false },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_buffer_size_setting),
                value = engine.bufferSize,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(bufferSize = newValue)) }
                },
                validate = { it.toIntOrNull()?.let { b -> b in 1..(Int.MAX_VALUE / 4) } ?: false },
            )
            EditTextPreferenceItem(
                title = stringResource(R.string.byedpi_conn_ip_setting),
                value = engine.connIp,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(connIp = newValue)) }
                },
            )
            SwitchPreferenceItem(
                shapes = segmentedShapeBottom(),
                title = stringResource(R.string.byedpi_no_ipv6_setting),
                summary = stringResource(R.string.byedpi_no_ipv6_summary),
                checked = engine.noIpv6,
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(noIpv6 = checked)) }
                },
            )
        }

        Section(title = stringResource(R.string.byedpi_connection_category)) {
            SwitchPreferenceItem(
                shapes = segmentedShapeTop(),
                title = stringResource(R.string.byedpi_tcp_fast_open_setting),
                summary = stringResource(R.string.byedpi_tcp_fast_open_summary),
                checked = engine.tcpFastOpen,
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(tcpFastOpen = checked)) }
                },
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.byedpi_no_domain_setting),
                summary = stringResource(R.string.byedpi_no_domain_summary),
                checked = engine.noDomain,
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(noDomain = checked)) }
                },
            )
            SwitchPreferenceItem(
                title = stringResource(R.string.byedpi_wait_send_setting),
                summary = stringResource(R.string.byedpi_wait_send_summary),
                checked = engine.waitSend,
                onCheckedChange = { checked ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(waitSend = checked)) }
                },
            )
            EditTextPreferenceItem(
                shapes = segmentedShapeBottom(),
                title = stringResource(R.string.byedpi_await_int_setting),
                summary = stringResource(R.string.byedpi_await_int_summary),
                value = engine.awaitInt,
                onValueChange = { newValue ->
                    onUpdateSettings { it.copy(engine = it.engine.copy(awaitInt = newValue)) }
                },
            )
        }
    }
}
