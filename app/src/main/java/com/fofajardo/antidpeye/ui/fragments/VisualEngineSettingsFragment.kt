@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.fofajardo.antidpeye.ui.fragments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fofajardo.antidpeye.R
import com.fofajardo.antidpeye.ui.components.PreferenceItem
import com.fofajardo.antidpeye.ui.components.Section
import com.fofajardo.antidpeye.ui.components.segmentedShapeBottom
import com.fofajardo.antidpeye.ui.components.segmentedShapeTop

@Composable
fun VisualEngineSettingsScreen(
    onNavigateToProxy: () -> Unit,
    onNavigateToDesync: () -> Unit,
    onNavigateToProtocols: () -> Unit,
    onNavigateToFilters: () -> Unit,
    onNavigateToAuto: () -> Unit,
    modifier: Modifier = Modifier,
    headerContent: (@Composable ColumnScope.() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        if (headerContent != null) {
            headerContent()
        }

        Section {
            PreferenceItem(
                title = stringResource(R.string.byedpi_proxy),
                shapes = segmentedShapeTop(),
                onClick = onNavigateToProxy,
            )
            PreferenceItem(
                title = stringResource(R.string.byedpi_desync),
                onClick = onNavigateToDesync,
            )
            PreferenceItem(
                title = stringResource(R.string.byedpi_protocols_category),
                onClick = onNavigateToProtocols,
            )
            PreferenceItem(
                title = stringResource(R.string.byedpi_filter_category),
                onClick = onNavigateToFilters,
            )
            PreferenceItem(
                title = stringResource(R.string.byedpi_auto_category),
                shapes = segmentedShapeBottom(),
                onClick = onNavigateToAuto,
            )
        }
    }
}
