@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.fofajardo.antidpeye.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alorma.compose.settings.ui.expressive.SettingsGroup
import com.alorma.compose.settings.ui.expressive.SettingsMenuLink
import com.alorma.compose.settings.ui.expressive.SettingsSwitch
import com.fofajardo.antidpeye.R

@Composable
fun segmentedShapeTop(): ListItemShapes = ListItemDefaults.segmentedShapes(0, 3)

@Composable
fun segmentedShapeMiddle(): ListItemShapes = ListItemDefaults.segmentedShapes(1, 3)

@Composable
fun segmentedShapeBottom(): ListItemShapes = ListItemDefaults.segmentedShapes(2, 3)

@Composable
fun segmentedShapeSingle(): ListItemShapes = ListItemDefaults.segmentedShapes(0, 1)

@Composable
fun defaultSegmentedColors(): ListItemColors {
    return ListItemDefaults.segmentedColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
    )
}

@Composable
fun Section(
    title: String? = null,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    SettingsGroup(
        modifier = modifier,
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
        enabled = enabled,
        title = title?.let { titleText ->
            { Text(text = titleText, fontWeight = FontWeight.Bold) }
        },
        content = content,
    )
}

@Composable
fun PreferenceItem(
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    enabled: Boolean = true,
    shapes: ListItemShapes = segmentedShapeMiddle(),
    colors: ListItemColors = defaultSegmentedColors(),
    onClick: (() -> Unit)? = null,
) {
    SettingsMenuLink(
        modifier = modifier,
        icon = if (icon != null) {
            {
                androidx.compose.material3.Icon(
                    imageVector = icon,
                    contentDescription = null,
                )
            }
        } else {
            null
        },
        title = { Text(text = title) },
        subtitle = if (!summary.isNullOrEmpty()) {
            { Text(text = summary) }
        } else {
            null
        },
        enabled = enabled,
        colors = colors,
        shapes = shapes,
        onClick = {
            if (onClick != null) {
                onClick()
            }
        },
    )
}

@Composable
fun SwitchPreferenceItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
    shapes: ListItemShapes = segmentedShapeMiddle(),
    colors: ListItemColors = defaultSegmentedColors(),
) {
    SettingsSwitch(
        modifier = modifier,
        state = checked,
        title = { Text(text = title) },
        subtitle = if (!summary.isNullOrEmpty()) {
            { Text(text = summary) }
        } else {
            null
        },
        enabled = enabled,
        colors = colors,
        shapes = shapes,
        onCheckedChange = onCheckedChange,
    )
}

@Composable
fun ListPreferenceItem(
    title: String,
    selectedValue: String,
    entries: List<String>,
    entryValues: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    enabled: Boolean = true,
    shapes: ListItemShapes = segmentedShapeMiddle(),
    colors: ListItemColors = defaultSegmentedColors(),
) {
    var showDialog by remember { mutableStateOf(false) }
    val selectedIndex = entryValues.indexOf(selectedValue).coerceAtLeast(0)
    val displaySelected = entries.getOrNull(selectedIndex) ?: selectedValue
    val subtitleText = if (!summary.isNullOrEmpty()) {
        "$summary: $displaySelected"
    } else {
        displaySelected
    }

    SettingsMenuLink(
        modifier = modifier,
        title = { Text(text = title) },
        subtitle = { Text(text = subtitleText) },
        enabled = enabled,
        colors = colors,
        shapes = shapes,
        onClick = { showDialog = true },
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    entries.forEachIndexed { idx, entryText ->
                        val value = entryValues[idx]
                        val isSelected = value == selectedValue
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(MaterialTheme.shapes.medium)
                                .selectable(
                                    selected = isSelected,
                                    onClick = {
                                        onValueChange(value)
                                        showDialog = false
                                    },
                                    role = Role.RadioButton,
                                )
                                .padding(horizontal = 8.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = null,
                            )
                            Text(
                                text = entryText,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp),
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = stringResource(R.string.dialog_cancel))
                }
            },
        )
    }
}

@Composable
fun EditTextPreferenceItem(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    summary: String? = null,
    defaultValue: String? = null,
    enabled: Boolean = true,
    shapes: ListItemShapes = segmentedShapeMiddle(),
    colors: ListItemColors = defaultSegmentedColors(),
    validate: ((String) -> Boolean)? = null,
) {
    var showDialog by remember { mutableStateOf(false) }
    var textInput by remember(showDialog) { mutableStateOf(value) }
    var isError by remember { mutableStateOf(false) }

    val displaySubtitle = when {
        value.isNotEmpty() -> value
        !defaultValue.isNullOrEmpty() -> defaultValue
        else -> stringResource(R.string.not_set)
    }

    SettingsMenuLink(
        modifier = modifier,
        title = { Text(text = title) },
        subtitle = { Text(text = displaySubtitle) },
        enabled = enabled,
        colors = colors,
        shapes = shapes,
        onClick = { showDialog = true },
    )

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                )
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (!summary.isNullOrEmpty()) {
                        Text(
                            text = summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 16.dp),
                        )
                    }
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = {
                            textInput = it
                            isError = if (validate != null) {
                                !validate(it)
                            } else {
                                false
                            }
                        },
                        isError = isError,
                        supportingText = if (isError) {
                            { Text(text = stringResource(R.string.invalid_value)) }
                        } else {
                            null
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val valid = if (validate != null) {
                            validate(textInput)
                        } else {
                            true
                        }
                        if (valid) {
                            onValueChange(textInput)
                            showDialog = false
                        } else {
                            isError = true
                        }
                    },
                ) {
                    Text(text = stringResource(R.string.dialog_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = stringResource(R.string.dialog_cancel))
                }
            },
        )
    }
}
