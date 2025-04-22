package cz.chrastecky.kidsmemorygame.ui.nav

import android.content.SharedPreferences
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeInfo
import cz.chrastecky.kidsmemorygame.theme_provider.ThemeProvider
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
    var themes by remember { mutableStateOf<List<ThemeInfo>?>(null) }
    var error by remember { mutableStateOf<Throwable?>(null) }

    NavHost(
        navController = navController,
        startDestination = "splash",
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        composable("splash") {
            SplashScreen(
                sharedPreferences = sharedPreferences,
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
                onThemeSelected = {
                    navController.navigate("game/$it") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("error") {
            ErrorScreen(error!!) {
                navController.navigate("splash")
            }
        }

        composable("picker") {
            ThemePickerScreen (
                themes = themes ?: emptyList(), // shouldn't happen, but safe
                onThemeSelected = { theme ->
                    navController.navigate("game/${theme.id}")
                }
            )
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
                )
            }
        }
    }
}