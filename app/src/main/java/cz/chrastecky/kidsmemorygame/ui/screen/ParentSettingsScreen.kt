package cz.chrastecky.kidsmemorygame.ui.screen

import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import cz.chrastecky.kidsmemorygame.R
import cz.chrastecky.kidsmemorygame.enums.SharedPreferenceName
import cz.chrastecky.kidsmemorygame.ui.component.IconCircleButton
import cz.chrastecky.kidsmemorygame.ui.theme.BackgroundColor
import cz.chrastecky.kidsmemorygame.ui.theme.ButtonBackground
import cz.chrastecky.kidsmemorygame.ui.theme.CardBackground
import cz.chrastecky.kidsmemorygame.ui.theme.TextOnBackgroundColor
import kotlin.random.Random

@Composable
fun ParentSettingsScreen(
    sharedPreferences: SharedPreferences,
    onBack: () -> Unit,
) {
    val pinDigits = remember { List(4) { Random.nextInt(0, 10) } }
    val pinValue = remember(pinDigits) { pinDigits.joinToString(separator = "") }
    val digitWords = stringArrayResource(id = R.array.digit_words)
    val pinWords = remember(pinDigits, digitWords) {
        pinDigits.joinToString(", ") { digitWords[it] }
    }

    var isUnlocked by remember { mutableStateOf(false) }
    var pinInput by remember { mutableStateOf("") }
    var showPinError by remember { mutableStateOf(false) }

    val currentValue = remember {
        mutableIntStateOf(
            sharedPreferences.getInt(SharedPreferenceName.MinimumDistinctImages.name, 0)
        )
    }

    fun updateValue(newValue: Int) {
        val safeValue = if (newValue < 0) 0 else newValue
        currentValue.intValue = safeValue
        sharedPreferences.edit {
            putInt(SharedPreferenceName.MinimumDistinctImages.name, safeValue)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Image(
            painter = painterResource(id = R.drawable.theme_picker_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )

        val density = LocalDensity.current
        val layoutDirection = LocalLayoutDirection.current

        if (!isUnlocked) {
            ParentPinGate(
                pinInput = pinInput,
                showError = showPinError,
                pinWords = pinWords,
                onBack = onBack,
                onPinChange = {
                    pinInput = it
                    showPinError = false
                },
                onSubmit = {
                    if (pinInput == pinValue) {
                        isUnlocked = true
                        showPinError = false
                    } else {
                        showPinError = true
                    }
                },
                density = density,
                layoutDirection = layoutDirection,
            )
            return@Box
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = maxOf(
                        with(density) {
                            WindowInsets.safeDrawing.getLeft(density, layoutDirection).toDp()
                        },
                        16.dp,
                    ),
                    end = maxOf(
                        with(density) {
                            WindowInsets.safeDrawing.getRight(density, layoutDirection).toDp()
                        },
                        16.dp,
                    ),
                    top = maxOf(
                        with(density) {
                            WindowInsets.safeDrawing.getTop(density).toDp()
                        },
                        16.dp,
                    ),
                    bottom = maxOf(
                        with(density) {
                            WindowInsets.safeDrawing.getBottom(density).toDp()
                        },
                        16.dp,
                    )
                ),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                IconCircleButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                    size = 32.dp,
                    backgroundColor = ButtonBackground.copy(alpha = 0.8f),
                    borderColor = Color.Transparent,
                    onClick = onBack,
                )

                Card(
                    shape = RoundedCornerShape(999.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground.copy(alpha = 0.9f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(999.dp),
                            ambientColor = BackgroundColor.copy(alpha = 0.6f),
                            spotColor = BackgroundColor.copy(alpha = 0.6f),
                        )
                        .border(
                            width = 4.dp,
                            color = BackgroundColor,
                            shape = RoundedCornerShape(999.dp),
                        )
                ) {
                    Text(
                        text = stringResource(R.string.parent_settings_title),
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextOnBackgroundColor,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .border(
                        width = 6.dp,
                        color = BackgroundColor,
                        shape = RoundedCornerShape(32.dp),
                    )
                    .clip(RoundedCornerShape(32.dp))
                    .widthIn(max = 480.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = stringResource(R.string.minimum_distinct_images_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = TextOnBackgroundColor,
                    )
                    Text(
                        text = stringResource(R.string.minimum_distinct_images_description),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextOnBackgroundColor,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        IconCircleButton(
                            icon = Icons.Default.Remove,
                            contentDescription = stringResource(R.string.decrease_button),
                            size = 36.dp,
                            backgroundColor = ButtonBackground.copy(alpha = 0.9f),
                            borderColor = Color.Transparent,
                            onClick = { updateValue(currentValue.intValue - 1) },
                        )

                        Text(
                            text = currentValue.intValue.toString(),
                            style = MaterialTheme.typography.headlineLarge,
                            color = TextOnBackgroundColor,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f),
                        )

                        IconCircleButton(
                            icon = Icons.Default.Add,
                            contentDescription = stringResource(R.string.increase_button),
                            size = 36.dp,
                            backgroundColor = ButtonBackground.copy(alpha = 0.9f),
                            borderColor = Color.Transparent,
                            onClick = { updateValue(currentValue.intValue + 1) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ParentPinGate(
    pinInput: String,
    showError: Boolean,
    pinWords: String,
    onBack: () -> Unit,
    onPinChange: (String) -> Unit,
    onSubmit: () -> Unit,
    density: androidx.compose.ui.unit.Density,
    layoutDirection: androidx.compose.ui.unit.LayoutDirection,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = maxOf(
                    with(density) {
                        WindowInsets.safeDrawing.getLeft(density, layoutDirection).toDp()
                    },
                    16.dp,
                ),
                end = maxOf(
                    with(density) {
                        WindowInsets.safeDrawing.getRight(density, layoutDirection).toDp()
                    },
                    16.dp,
                ),
                top = maxOf(
                    with(density) {
                        WindowInsets.safeDrawing.getTop(density).toDp()
                    },
                    16.dp,
                ),
                bottom = maxOf(
                    with(density) {
                        WindowInsets.safeDrawing.getBottom(density).toDp()
                    },
                    16.dp,
                )
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopStart,
        ) {
            IconCircleButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back_button),
                size = 32.dp,
                backgroundColor = ButtonBackground.copy(alpha = 0.8f),
                borderColor = Color.Transparent,
                onClick = onBack,
            )
        }

        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            modifier = Modifier
                .border(
                    width = 6.dp,
                    color = BackgroundColor,
                    shape = RoundedCornerShape(32.dp),
                )
                .clip(RoundedCornerShape(32.dp))
                .widthIn(max = 420.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.parent_pin_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextOnBackgroundColor,
                    textAlign = TextAlign.Center,
                )
                val description = stringResource(R.string.parent_pin_description, pinWords)
                val startIndex = description.indexOf(pinWords)
                val endIndex = if (startIndex >= 0) startIndex + pinWords.length else -1

                Text(
                    text = buildAnnotatedString {
                        append(description)
                        if (startIndex >= 0 && endIndex >= 0) {
                            addStyle(
                                style = SpanStyle(fontWeight = FontWeight.Bold),
                                start = startIndex,
                                end = endIndex,
                            )
                        }
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextOnBackgroundColor,
                    textAlign = TextAlign.Center,
                )

                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { value ->
                        onPinChange(value.filter { it.isDigit() }.take(4))
                    },
                    label = { Text(text = stringResource(R.string.parent_pin_label)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    textStyle = MaterialTheme.typography.headlineLarge.copy(
                        letterSpacing = 12.sp,
                        textAlign = TextAlign.Center,
                    ),
                    modifier = Modifier.width(220.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = TextOnBackgroundColor,
                        unfocusedTextColor = TextOnBackgroundColor,
                        cursorColor = TextOnBackgroundColor,
                        focusedIndicatorColor = BackgroundColor,
                        unfocusedIndicatorColor = BackgroundColor.copy(alpha = 0.7f),
                        focusedLabelColor = TextOnBackgroundColor,
                        unfocusedLabelColor = TextOnBackgroundColor.copy(alpha = 0.8f),
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                    ),
                )

                if (showError) {
                    Text(
                        text = stringResource(R.string.parent_pin_error),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFB00020),
                        textAlign = TextAlign.Center,
                    )
                }

                Button(
                    onClick = onSubmit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButtonBackground,
                        contentColor = TextOnBackgroundColor,
                    ),
                ) {
                    Text(text = stringResource(R.string.parent_pin_confirm))
                }
            }
        }
    }
}
