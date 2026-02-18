package com.vocabreading.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vocabreading.app.model.AppSettings
import com.vocabreading.app.model.SupportedLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: AppSettings,
    onSettingsChanged: (AppSettings) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("<", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Source language picker
            Text(
                text = "Source Language",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LanguagePicker(
                selected = settings.sourceLanguage,
                onSelected = { onSettingsChanged(settings.copy(sourceLanguage = it)) }
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // LibreTranslate URL
            Text(
                text = "LibreTranslate Server",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = settings.libreTranslateUrl,
                onValueChange = {
                    onSettingsChanged(settings.copy(libreTranslateUrl = it))
                },
                label = { Text("Server URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Text(
                text = "Default: https://libretranslate.com",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 4.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            // AnkiDroid deck name
            Text(
                text = "AnkiDroid Settings",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            OutlinedTextField(
                value = settings.ankiDeckName,
                onValueChange = {
                    onSettingsChanged(settings.copy(ankiDeckName = it))
                },
                label = { Text("Deck Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Text(
                text = "The AnkiDroid deck to add flashcards to",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 4.dp)
            )

            OutlinedTextField(
                value = settings.ankiModelName,
                onValueChange = {
                    onSettingsChanged(settings.copy(ankiModelName = it))
                },
                label = { Text("Note Type (Model)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                singleLine = true
            )
            Text(
                text = "Default: Basic (Front/Back)",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
private fun LanguagePicker(
    selected: SupportedLanguage,
    onSelected: (SupportedLanguage) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = selected.displayName,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            enabled = false
        )

        // Make the whole field tappable
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SupportedLanguage.entries.forEach { language ->
                DropdownMenuItem(
                    text = { Text(language.displayName) },
                    onClick = {
                        onSelected(language)
                        expanded = false
                    }
                )
            }
        }
    }
}
