package gourmetgo.client.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun InputPopUp(
    showDialog: MutableState<Boolean>,
    title: String = "Ingresa información",
    body: String,
    inputLabel: String = "",
    inputValue: String,
    onInputChange: (String) -> Unit,
    confirmText: String = "Aceptar",
    onConfirm: (String) -> Unit,
    dismissText: String = "Cancelar",
    onDismiss: (() -> Unit)? = null
) {
    if (showDialog.value) {
        var localInput by remember { mutableStateOf(inputValue) }
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
                onDismiss?.invoke()
            },
            title = {
                Text(text = title)
            },
            text = {
                Column {
                    Text(text = body)
                    OutlinedTextField(
                        value = localInput,
                        onValueChange = {
                            localInput = it
                            onInputChange(it)
                        },
                        label = { Text(inputLabel) },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false
                    onConfirm(localInput)
                }) {
                    Text(confirmText)
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog.value = false
                    onDismiss?.invoke()
                }) {
                    Text(dismissText)
                }
            }
        )
    }
}
