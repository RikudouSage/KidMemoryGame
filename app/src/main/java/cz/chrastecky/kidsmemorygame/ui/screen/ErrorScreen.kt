package cz.chrastecky.kidsmemorygame.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cz.chrastecky.kidsmemorygame.R

@Composable
fun ErrorScreen(error: Throwable, onRetry: () -> Unit) {
    LaunchedEffect(error) {
        Log.e("ErrorScreen", "Unhandled error", error)
    }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.error),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(R.string.error_prefix, error.message ?: "Unknown error"),
                color = MaterialTheme.colorScheme.onError,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
            Button(onClick = onRetry) {
                Text(stringResource(R.string.button_retry))
            }
        }
    }
}
