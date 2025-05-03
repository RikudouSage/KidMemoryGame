package cz.chrastecky.kidsmemorygame.ui.nav

import android.content.SharedPreferences
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.chrastecky.kidsmemorygame.dto.ThemeInfo
import cz.chrastecky.kidsmemorygame.enums.SharedPreferenceName
import cz.chrastecky.kidsmemorygame.provider.ThemeProvider
import cz.chrastecky.kidsmemorygame.ui.screen.DownloadScreen
import cz.chrastecky.kidsmemorygame.ui.screen.ErrorScreen
import cz.chrastecky.kidsmemorygame.ui.screen.GameScreen
import cz.chrastecky.kidsmemorygame.ui.screen.SplashScreen
import cz.chrastecky.kidsmemorygame.ui.screen.ThemePickerScreen

@Composable
fun AppNavigation(
    themeProvider: ThemeProvider,
    sharedPreferences: SharedPreferences,
) {
    val navController = rememberNavController()

    var themes by rememberSaveable { mutableStateOf<List<ThemeInfo>?>(null) }
    var error by remember { mutableStateOf<Throwable?>(null) }
    var reloadGameKey by remember { mutableIntStateOf(0) }

    NavHost(
        navController = navController,
        startDestination = "splash",
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        composable("splash") {
            SplashScreen(
                loadThemes = { themeProvider.listAvailableThemes() },
                onLoaded = {
                    themes = it
                    navController.navigate("picker") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                onError = {
                    error = it
                    navController.navigate("error")
                },
            )
        }

        composable("error") {
            ErrorScreen(error!!) {
                navController.navigate("splash")
            }
        }

        composable("picker") {
            var screenState by remember { mutableStateOf<PickerScreenState>(PickerScreenState.Loading) }
            val lastThemeId = sharedPreferences.getString(SharedPreferenceName.LastUsedTheme.name, null)

            LaunchedEffect(lastThemeId) {
                if (lastThemeId != null) {
                    val isDownloaded = themeProvider.isThemeDownloaded(lastThemeId)
                    screenState = if (isDownloaded) {
                        PickerScreenState.NavigateToGame(lastThemeId)
                    } else {
                        PickerScreenState.NavigateToDownload(lastThemeId)
                    }
                } else {
                    screenState = PickerScreenState.ShowPicker
                }
            }

            when (val state = screenState) {
                is PickerScreenState.NavigateToGame -> {
                    LaunchedEffect(state.id) {
                        navController.navigate("game/${state.id}") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                is PickerScreenState.NavigateToDownload -> {
                    LaunchedEffect(state.id) {
                        navController.navigate("download/${state.id}") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                PickerScreenState.ShowPicker -> {
                    ThemePickerScreen(
                        themes = themes ?: emptyList(),
                        themeProvider = themeProvider,
                    ) { theme, isDownloaded ->
                        val route = if (isDownloaded) "game/${theme.id}" else "download/${theme.id}"
                        navController.navigate(route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }

                PickerScreenState.Loading -> {
                }
            }
        }

        composable(
            route = "download/{themeId}",
            enterTransition = { scaleIn() }
        ) { backStackEntry ->
            val themeId = backStackEntry.arguments?.getString("themeId") ?: return@composable

            DownloadScreen(
                themeId = themeId,
                themeProvider = themeProvider,
            ) {
                navController.navigate("game/${themeId}") {
                    popUpTo(0) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }

        composable(
            route = "game/{themeId}",
            enterTransition = { scaleIn() }
        ) { backStackEntry ->
            val themeId = backStackEntry.arguments?.getString("themeId")

            if (themeId != null) {
                GameScreen(
                    themeId = themeId,
                    sharedPreferences = sharedPreferences,
                    themeProvider = themeProvider,
                    reloadGameKey = reloadGameKey,
                    onRequestReset = { reloadGameKey++ },
                    onThemeChangeRequested = {
                        sharedPreferences.edit(true) {
                            remove(SharedPreferenceName.LastUsedTheme.name)
                        }

                        navController.navigate("picker") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}

private sealed class PickerScreenState {
    data object Loading : PickerScreenState()
    data object ShowPicker : PickerScreenState()
    data class NavigateToGame(val id: String) : PickerScreenState()
    data class NavigateToDownload(val id: String) : PickerScreenState()
}