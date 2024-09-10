package com.example.intenttesttool

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.intenttesttool.ui.theme.IntentTestToolTheme

class MainActivity : ComponentActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("IntentTestToolPrefs", Context.MODE_PRIVATE)

        enableEdgeToEdge()
        setContent {
            IntentTestToolTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val context = LocalContext.current

                        var packageName by remember { mutableStateOf("com.example.app") }
                        var className by remember { mutableStateOf("com.example.app.MainActivity") }
                        var action by remember { mutableStateOf("") }
                        val selectedFlags = remember { mutableStateListOf<Int>() }

                        Spacer(modifier = Modifier.height(16.dp))

                        ParamInput(
                            label = "Package Name",
                            value = packageName,
                            onValueChange = { packageName = it }
                        )

                        ParamInput(
                            label = "Class Name",
                            value = className,
                            onValueChange = { className = it }
                        )

                        ParamInput(
                            label = "Action",
                            value = action,
                            onValueChange = { action = it }
                        )

                        FlagSelector(selectedFlags = selectedFlags)

                        SelectedFlags(selectedFlags = selectedFlags)

                        LaunchButton(
                            label = "Launch Intent",
                            onClick = {
                                launch(
                                    context,
                                    packageName,
                                    className,
                                    action = action,
                                    flags = selectedFlags
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun FlagSelector(selectedFlags: MutableList<Int>) {
    var expanded by remember { mutableStateOf(false) }
    val flags = listOf(
        Intent.FLAG_ACTIVITY_NEW_TASK,
        Intent.FLAG_ACTIVITY_CLEAR_TASK,
        Intent.FLAG_ACTIVITY_SINGLE_TOP,
        Intent.FLAG_ACTIVITY_CLEAR_TOP
    )

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = "Add Intent Flag",
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            flags.forEach { flag ->
                DropdownMenuItem(
                    text = { Text(getFlagName(flag)) },
                    onClick = {
                        if (flag !in selectedFlags) {
                            selectedFlags.add(flag)
                        }
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun SelectedFlags(selectedFlags: MutableList<Int>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.heightIn(max = 200.dp)
    ) {
        items(selectedFlags) { flag ->
            OutlinedCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(getFlagName(flag))
                    IconButton(onClick = { selectedFlags.remove(flag) }) {
                        Icon(Icons.Filled.Close, contentDescription = "Remove flag")
                    }
                }
            }
        }
    }
}

@Composable
fun LaunchButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label)
    }
}

@Composable
fun ParamInput(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth()
    )
}

fun launch(
    context: Context,
    packageName: String,
    className: String,
    action: String? = null,
    flags: List<Int>? = null
) {
    val intent = Intent().apply {
        setClassName(packageName, className)
        action?.takeIf { it.isNotBlank() }?.let { setAction(it) }
        flags?.forEach { addFlags(it) }
    }

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
    }
}

fun getFlagName(flag: Int): String {
    return when (flag) {
        Intent.FLAG_ACTIVITY_NEW_TASK -> "FLAG_ACTIVITY_NEW_TASK"
        Intent.FLAG_ACTIVITY_CLEAR_TASK -> "FLAG_ACTIVITY_CLEAR_TASK"
        Intent.FLAG_ACTIVITY_SINGLE_TOP -> "FLAG_ACTIVITY_SINGLE_TOP"
        Intent.FLAG_ACTIVITY_CLEAR_TOP -> "FLAG_ACTIVITY_CLEAR_TOP"
        else -> "Unknown Flag"
    }
}
