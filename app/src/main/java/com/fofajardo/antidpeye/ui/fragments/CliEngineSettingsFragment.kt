package com.fofajardo.antidpeye.ui.fragments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fofajardo.antidpeye.R
import com.fofajardo.antidpeye.ui.components.Section

@Composable
fun CmdSettingsScreen(
    cmdArgs: String,
    onCmdArgsChange: (String) -> Unit,
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

        OutlinedTextField(
            value = cmdArgs,
            onValueChange = onCmdArgsChange,
            label = { Text(stringResource(R.string.command_line_arguments)) },
            placeholder = { Text("-s1 -q1 -Y") },
            minLines = 4,
            maxLines = 10,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
        )
    }
}
