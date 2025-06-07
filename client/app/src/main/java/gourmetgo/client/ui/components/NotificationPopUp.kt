package gourmetgo.client.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun NotificationPopUp(
    showDialog: MutableState<Boolean>,
    title: String = "Notificación",
    body: String,
    option1: String,
    onOption1: () -> Unit,
    option2: String? = null,
    onOption2: (() -> Unit)? = null
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            },
            text = {
                Text(text = body)
            },
            confirmButton = {
                Button(onClick = {
                    showDialog.value = false
                    onOption1()
                }) {
                    Text(option1)
                }
            },
            dismissButton = option2?.let {
                {
                    Button(onClick = {
                        showDialog.value = false
                        onOption2?.invoke()
                    }) {
                        Text(option2)
                    }
                }
            }
        )
    }
}

