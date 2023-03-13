package com.rainy.mastodroid.features.login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.rainy.mastodroid.R
import com.rainy.mastodroid.features.login.model.LoginUiState
import com.rainy.mastodroid.ui.theme.MastodroidTheme
import com.rainy.mastodroid.util.ColorSchemePreviews
import com.rainy.mastodroid.util.ErrorModel

@Composable
fun LoginScreen(
    loginState: LoginUiState,
    onContinueClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var instanceDomainText by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue())
    }
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        InstanceHostTextField(
            instanceHostText = instanceDomainText,
            instanceLoginError = loginState.instanceLoginError,
            onTextChange = { instanceDomainText = it },
            onImeDone = {
                focusManager.clearFocus()
                onContinueClicked(instanceDomainText.text)
            }
        )
        Button(
            onClick = {
                focusManager.clearFocus()
                onContinueClicked(instanceDomainText.text)
            },
            modifier = Modifier.align(Alignment.End), enabled = !loginState.isLoading
        ) {
            Text(text = stringResource(R.string.log_in_button))
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstanceHostTextField(
    instanceHostText: TextFieldValue,
    instanceLoginError: ErrorModel?,
    onTextChange: (TextFieldValue) -> Unit,
    onImeDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = instanceHostText,
        label = { Text(stringResource(R.string.login_instance_host_hint)) },
        onValueChange = onTextChange,
        singleLine = true,
        keyboardActions = KeyboardActions(onDone = {
            onImeDone()
        }),
        isError = instanceLoginError != null,
        supportingText = {
            val errorText = instanceLoginError?.resolveText() ?: ""
            Text(errorText)
        },
        modifier = modifier.fillMaxWidth()
    )
}

@ColorSchemePreviews
@Composable
private fun LoginFormPreview() {
    MastodroidTheme {
        LoginScreen(LoginUiState(), onContinueClicked = {})
    }
}
