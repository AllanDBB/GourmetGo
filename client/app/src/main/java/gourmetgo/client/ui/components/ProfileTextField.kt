package gourmetgo.client.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    focusManager: androidx.compose.ui.focus.FocusManager,
    onFocusLost: () -> Unit = {}
) {
    Text(
        text = label,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start
    )

    Spacer(modifier = Modifier.height(8.dp))

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, fontSize = 14.sp) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (!focusState.isFocused && value.isNotEmpty()) {
                    onFocusLost()
                }
            },
        singleLine = true,
        isError = isError,
        supportingText = if (errorMessage.isNotEmpty()) {
            { Text(errorMessage, color = MaterialTheme.colorScheme.error) }
        } else null,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
    )

    Spacer(modifier = Modifier.height(16.dp))
}