import java.io.FileFilter

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Kids Memory Game"
include(":app")

val themesDir = File(rootDir, "themes")
val themeDirs = themesDir.listFiles(FileFilter { it.isDirectory }) ?: arrayOf()
val themeIconsDir = File(rootDir, "theme_icons")

themeDirs.forEach { themeDir ->
    val name = themeDir.name
    if (themeDir.exists()) {
        include(":$name")
    }
}
if (themeIconsDir.exists()) {
    include(":theme_icons")
}
