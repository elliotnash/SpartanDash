import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "org.chsrobotics.dash"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://jitpack.io")
}

kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("com.mayakapps.compose:window-styler:0.3.3-SNAPSHOT")
                implementation("com.github.elliotnash.compose-fluent-ui:fluent:master-SNAPSHOT")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "org.chsrobotics.dash.AppKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "SpartanDash"
            packageVersion = "1.0.0"
        }
    }
}
