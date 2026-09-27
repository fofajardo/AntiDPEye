@file:OptIn(androidx.compose.material3.ExperimentalMaterial3ExpressiveApi::class)

package com.fofajardo.antidpeye.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import com.fofajardo.antidpeye.R
import com.fofajardo.antidpeye.data.AppSettings
import com.fofajardo.antidpeye.data.EngineSettings
import com.fofajardo.antidpeye.ui.components.ListPreferenceItem
import com.fofajardo.antidpeye.ui.components.PreferenceItem
import com.fofajardo.antidpeye.ui.components.Section
import com.fofajardo.antidpeye.ui.components.segmentedShapeBottom
import com.fofajardo.antidpeye.ui.components.segmentedShapeTop
import com.fofajardo.antidpeye.ui.fragments.CmdSettingsScreen
import com.fofajardo.antidpeye.ui.fragments.VisualEngineSettingsScreen
import com.fofajardo.antidpeye.utility.ByeDpiArgsConverter

@Composable
fun EngineSettingsScreen(
    settings: AppSettings,
    onUpdateSettings: ((AppSettings) -> AppSettings) -> Unit,
    onNavigateToProxy: () -> Unit,
    onNavigateToDesync: () -> Unit,
    onNavigateToProtocols: () -> Unit,
    onNavigateToFilters: () -> Unit,
    onNavigateToAuto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val engine = settings.engine
    val isCmd = engine.enableCmdSettings
    val editorMode = if (isCmd) "cmd" else "ui"

    val engineHeaderContent: @Composable ColumnScope.() -> Unit = {
        Section(
            title = stringResource(R.string.general_category),
        ) {
            ListPreferenceItem(
                title = stringResource(R.string.editor_mode),
                shapes = segmentedShapeTop(),
                selectedValue = editorMode,
                entries = stringArrayResource(R.array.byedpi_editor_modes).toList(),
                entryValues = stringArrayResource(R.array.byedpi_editor_modes_entries).toList(),
                onValueChange = { newMode ->
                    val enableCmd = newMode == "cmd"
                    onUpdateSettings { it.copy(engine = it.engine.copy(enableCmdSettings = enableCmd)) }
                },
            )

            if (isCmd) {
                PreferenceItem(
                    title = stringResource(R.string.sync_from_visual),
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    shapes = segmentedShapeBottom(),
                    onClick = {
                        val generated = ByeDpiArgsConverter.engineSettingsToCmdArgs(engine)
                        onUpdateSettings { it.copy(engine = it.engine.copy(cmdArgs = generated)) }
                        Toast.makeText(context, R.string.sync_applied, Toast.LENGTH_SHORT).show()
                    },
                )
            } else {
                PreferenceItem(
                    title = stringResource(R.string.sync_from_cli),
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    shapes = segmentedShapeBottom(),
                    onClick = {
                        val updatedEngine = ByeDpiArgsConverter.applyCmdArgsToEngineSettings(engine.cmdArgs, engine)
                        onUpdateSettings { it.copy(engine = updatedEngine) }
                        Toast.makeText(context, R.string.sync_applied, Toast.LENGTH_SHORT).show()
                    },
                )
            }
        }
    }

    if (isCmd) {
        CmdSettingsScreen(
            cmdArgs = engine.cmdArgs,
            onCmdArgsChange = { newArgs ->
                onUpdateSettings { it.copy(engine = it.engine.copy(cmdArgs = newArgs)) }
            },
            headerContent = engineHeaderContent,
            modifier = modifier.fillMaxSize(),
        )
    } else {
        VisualEngineSettingsScreen(
            onNavigateToProxy = onNavigateToProxy,
            onNavigateToDesync = onNavigateToDesync,
            onNavigateToProtocols = onNavigateToProtocols,
            onNavigateToFilters = onNavigateToFilters,
            onNavigateToAuto = onNavigateToAuto,
            headerContent = engineHeaderContent,
            modifier = modifier.fillMaxSize(),
        )
    }
}
