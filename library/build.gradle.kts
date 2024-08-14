import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)

    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.android.library)

    alias(libs.plugins.vanniktech.maven.publish)
}

kotlin {

    applyDefaultHierarchyTemplate()
    androidTarget {
//        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    jvm("desktop")

    iosX64()
    iosArm64()
    iosSimulatorArm64()

//    listOf(
//        iosX64(),
//        iosArm64(),
//        iosSimulatorArm64(),
//    ).forEach { iosTarget ->
//        iosTarget.binaries.framework {
//            baseName = "library"
//            isStatic = true
//        }
//    }

    //
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        moduleName = "autosizetext"
//        binaries.library()
//        binaries.executable()
//        browser()
//    }
//

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.material)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(compose.uiTooling)
            }
        }
    }

}

android {

    namespace = "com.sillydevices.compose.ui.autosizetext"
    compileSdk = 34

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = 23
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles.add(File("consumer-rules.pro"))
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

val artifactVersion = "0.1.1"

mavenPublishing {
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL, automaticRelease = false)
    signAllPublications()

    coordinates("com.sillydevices", "compose.ui.autosizetext", artifactVersion)

    pom {
        name.set(project.name)
        description.set("Text Composamble wrapper that automatically adjusts its font size to fit the available space.")
        inceptionYear.set("2024")
        url.set("https://github.com/SillyDevices/Compose-AutoSizeText")
        licenses {
            license {
                name.set("MIT License")
                url.set("https://github.com/SillyDevices/Compose-AutoSizeText?tab=MIT-1-ov-file")
                distribution.set("https://github.com/SillyDevices/Compose-AutoSizeText?tab=MIT-1-ov-file")
            }
        }
        developers {
            developer {
                id.set("SillyDevices")
                name.set("Evgenii Petrov")
                url.set("https://github.com/SillyDevices")
            }
        }
        scm {
            url.set("https://github.com/SillyDevices/Compose-AutoSizeText")
            connection.set("scm:git:git://github.com:SillyDevices/Compose-AutoSizeText.git")
            developerConnection.set("scm:git:ssh://git@github.com:SillyDevices/Compose-AutoSizeText.git")
        }
    }

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Empty(),
            sourcesJar = true,
            androidVariantsToPublish = listOf("release")
        )
    )
}

publishing {
    repositories {
        mavenLocal()
    }
}

task("checkArtifactVersion") {
    //./gradlew publishAllToMavenCentralRepository -PgitTag=v0.1.1
    doLast {
        val actualVersion = findProperty("gitTag")
        when(actualVersion) {
            null -> throw GradleException("gitTag property is not set(-PgitTag=vX.X.X)")
            "v${artifactVersion}" -> { }
            else -> throw GradleException("Artifact version is incorrect(actual = $actualVersion, expected = $artifactVersion)\ngitTag must start with 'v'")
        }
    }
}

tasks.withType(PublishToMavenRepository::class) {
    dependsOn("checkArtifactVersion")
}

dependencies {
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)

    androidTestImplementation(libs.compose.ui.test.junit4)
}

