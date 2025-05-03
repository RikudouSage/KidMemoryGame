import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.FileFilter
import java.security.MessageDigest

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "cz.chrastecky.kidsmemorygame"
    compileSdk = 35

    defaultConfig {
        applicationId = "cz.chrastecky.kidsmemorygame"
        minSdk = 24
        targetSdk = 35
        versionCode = 10
        versionName = "1.2.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }

        debug {
            kotlinOptions {
                freeCompilerArgs += listOf("-Xdebug")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    flavorDimensions += "distribution"

    productFlavors {
        create("playstore") {
            dimension = "distribution"
        }
        create("lite") {
            dimension = "distribution"
        }
        create("full") {
            dimension = "distribution"
        }
    }

    if (project.findProperty("includeAssetPacks") == "true") {
        val themesDir = File(rootDir, "themes")
        val themePacks = themesDir.listFiles(FileFilter { it.isDirectory })?.map { ":${it.name}" } ?: emptyList()
        assetPacks += themePacks
        assetPacks += ":theme_icons"
    }
}

android.applicationVariants.all {
    val variant = this
    val flavor = variant.flavorName

    if (flavor == "full") {
        variant.mergeAssetsProvider.configure {
            doLast {
                val themesSource = File(rootDir, "themes")
                val themesDestination = File(variant.mergeAssetsProvider.get().outputDir.asFile.get(), "themes")

                project.copy {
                    from(themesSource)
                    into(themesDestination)
                }

                val musicSource = File(rootDir, "music")
                val musicDestination = File(variant.mergeAssetsProvider.get().outputDir.asFile.get(), "music")

                project.copy {
                    from(musicSource)
                    into(musicDestination)
                }
            }
        }
    } else if (flavor == "playstore") {
        variant.mergeAssetsProvider.configure {
            doLast {
                val mapper = jacksonObjectMapper()

                val themesSource = File(rootDir, "themes")
                val templatesDir = File(rootDir, "templates")
                val themeDirs = themesSource.listFiles { file -> file.isDirectory}?.toList() ?: emptyList()

                val themeIconsDir = File(rootDir, "theme_icons")
                themeIconsDir.mkdirs()
                val gitignoreIconsDir = File(themeIconsDir, ".gitignore")
                gitignoreIconsDir.writeText("/*")
                val targetGradleFile = themeIconsDir.resolve("build.gradle.kts")
                if (targetGradleFile.exists()) {
                    targetGradleFile.delete()
                }
                templatesDir.resolve("theme-icons-gradle.template").copyTo(targetGradleFile)

                themeDirs.forEach { themeDir ->
                    val themeName = themeDir.name
                    val targetDir = File(rootDir, themeName)
                    val assetsDir = File(targetDir, "src/main/assets/$themeName")
                    assetsDir.mkdirs()

                    val gitignore = File(targetDir, ".gitignore")
                    gitignore.writeText("/*")

                    val template = File(templatesDir, "asset-pack-build-gradle.template").readText()
                    File(targetDir, "build.gradle.kts").writeText(template.replace("{{name}}", themeName))

                    project.copy {
                        from(themeDir)
                        into(assetsDir)
                    }

                    val themeJson = mapper.readValue<Map<String, Any>>(themeDir.resolve("theme.json"))
                    val iconFilePath = themeJson["icon"] as String
                    val iconFile = themeDir.resolve(iconFilePath)
                    if (!iconFile.exists()) {
                        throw Exception("Failed resolving the icon file")
                    }
                    val targetIconFile = themeIconsDir.resolve("src/main/assets/$themeName.${iconFile.extension}")
                    targetIconFile.mkdirs()
                    if (targetIconFile.exists()) {
                        targetIconFile.delete()
                    }
                    iconFile.copyTo(targetIconFile)
                }

                val destination = File(variant.mergeAssetsProvider.get().outputDir.asFile.get(), "themes")
                destination.parentFile.mkdirs()
                val themesJson = File(rootDir, "themes/themes.json")

                project.copy {
                    from(themesJson)
                    into(destination)
                }
            }
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.asset.delivery)
    implementation(libs.asset.delivery.ktx)
//    add("liteImplementation", libs.ktor.client.core)
//    add("liteImplementation", libs.ktor.client.cio)
//    add("liteImplementation", libs.ktor.client.content.negotiation)
//    add("liteImplementation", libs.ktor.serialization.kotlinx.json)
//    add("playstoreImplementation", libs.asset.delivery)
//    add("playstoreImplementation", libs.asset.delivery.ktx)
}

tasks.register("generateThemes") {
    group = "assets"
    description = "Generates theme.json files for all themes."

    doLast {
        val prettyPrinter = DefaultPrettyPrinter().apply {
            indentArraysWith(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
        }
        val mapper = jacksonObjectMapper().writer(prettyPrinter)

        val themesRoot = File(rootDir, "themes")
        val themeDirs = themesRoot.listFiles { it -> it.isDirectory } ?: return@doLast

        val globalIndex = mutableListOf<Map<String, Any>>()

        themeDirs.forEach { themeDir ->
            val digest = MessageDigest.getInstance("SHA-256")

            val themeId = themeDir.name
            val cardsDir = File(themeDir, "cards")
            val cardFiles = cardsDir.listFiles { file ->
                file.extension in listOf("webp", "png", "jpg")
            }?.map { "cards/${it.name}" }?.sorted() ?: listOf()

            val background = themeDir.listFiles()?.find { it.name.startsWith("background") }?.name ?: ""

            val nameFile = File(themeDir, "name.txt")
            val iconFile = File(themeDir, "icon.txt")
            val name = if (nameFile.exists()) nameFile.readText().trim() else themeId.replaceFirstChar { it.uppercase() }
            val icon = if (iconFile.exists()) iconFile.readText().trim() else cardFiles[0]

            val mascots = mutableListOf<Map<String, Any>>()
            cardFiles.forEach { card ->
                val fileName = card.substringAfter("cards/").substringBeforeLast('.')
                val mascotFile = File(cardsDir, "$fileName.mascot.json")
                if (mascotFile.exists()) {
                    val mascotData: Map<String, Any> = jacksonObjectMapper().readValue(mascotFile)
                    mascots.add(mapOf("image" to card) + mascotData)
                }
            }

            val hashableFiles = mutableListOf<File>()
            hashableFiles += cardsDir.listFiles()?.toList() ?: emptyList()
            hashableFiles += themeDir.listFiles { file ->
                !file.isDirectory && !file.name.equals("theme.json")
            }?.toList() ?: emptyList()

            hashableFiles.forEach { file ->
                file.inputStream().use { input ->
                    val buffer = ByteArray(8192)
                    while (true) {
                        val read = input.read(buffer)
                        if (read <= 0) {
                            break
                        }
                        digest.update(buffer, 0, read)
                    }
                }
            }

            val themeJson = mapOf(
                "id" to themeId,
                "name" to name,
                "background" to background,
                "cards" to cardFiles,
                "icon" to icon,
                "mascots" to mascots,
                "hash" to digest.digest().joinToString("") { "%02x".format(it) }
            )

            File(themeDir, "theme.json").writeText(
                mapper.writeValueAsString(themeJson)
            )

            globalIndex.add(
                mapOf(
                    "id" to themeId,
                    "name" to name,
                    "configPath" to "$themeId/theme.json",
                    "icon" to icon,
                    "hash" to themeJson["hash"]!!,
                )
            )

            println("✓ Wrote theme.json for '$themeId'")
        }

        val globalFile = File(themesRoot, "themes.json")
        globalFile.writeText(
            mapper.writeValueAsString(globalIndex)
        )

        println("✓ Wrote top-level themes.json with ${globalIndex.size} entries.")

        val musicDir = File(rootDir, "music")
        val musicFiles = musicDir.listFiles { it -> it.isFile && it.extension == "ogg" }?.toList() ?: return@doLast
        val indexFile = musicDir.resolve("music.json")

        val result: MutableList<String> = mutableListOf()
        musicFiles.forEach {
            result += it.name
        }

        indexFile.writeText(mapper.writeValueAsString(result))
    }
}