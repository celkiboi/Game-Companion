package hr.ferit.tomislavcelic.gamecompanion.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun RegisterDialog(
    onDismiss: () -> Unit,
    onRegister: (name: String, email: String, pass: String) -> Unit
) {
    var name        by remember { mutableStateOf("") }
    var email       by remember { mutableStateOf("") }
    var pass        by remember { mutableStateOf("") }
    var passConfirm by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val emailValid = email.contains("@") && email.contains(".")
    val passValid  = pass.length >= 6
    val passMatch  = pass == passConfirm
    val allValid   = name.isNotBlank() && emailValid && passValid && passMatch

    val passwordVisual =
        if (showPassword) VisualTransformation.None else PasswordVisualTransformation()
    val toggleIcon =
        if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create account") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                /* ── Display name ── */
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Display name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        autoCorrect    = false,
                        keyboardType   = KeyboardType.Text
                    )
                )

                /* ── Email ── */
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    isError = email.isNotBlank() && !emailValid,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Email
                    )
                )

                /* ── Password ── */
                OutlinedTextField(
                    value = pass,
                    onValueChange = { pass = it },
                    label = { Text("Password (≥ 6)") },
                    visualTransformation = passwordVisual,
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(toggleIcon, contentDescription = null)
                        }
                    },
                    isError = pass.isNotBlank() && !passValid,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrect    = false,
                        keyboardType   = KeyboardType.Password
                    )
                )

                /* ── Confirm password ── */
                OutlinedTextField(
                    value = passConfirm,
                    onValueChange = { passConfirm = it },
                    label = { Text("Repeat password") },
                    visualTransformation = passwordVisual,
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(toggleIcon, contentDescription = null)
                        }
                    },
                    isError = passConfirm.isNotBlank() && !passMatch,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Password
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = allValid,
                onClick = {
                    scope.launch { onRegister(name.trim(), email.trim(), pass) }
                }
            ) { Text("Sign up") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

