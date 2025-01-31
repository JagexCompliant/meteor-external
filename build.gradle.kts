import proguard.gradle.ProGuardTask

plugins {
    java
    kotlin("jvm") version "1.7.20"
}

group = "org.example"
version = "1.0-SNAPSHOT"

val apiRelease by rootProject.extra { "9.9.9" }
val pluginClass by rootProject.extra { "meteor.plugins.external.ExternalPlugin" }

val outputJar by rootProject.extra { "./build/libs/${rootProject.name}-${version}.jar" }
val exportDir by rootProject.extra { "${System.getProperty("user.home")}/.meteor/externalplugins/" }

buildscript {
    dependencies {
        classpath("com.guardsquare:proguard-gradle:7.2.1")
    }
}

repositories {
    mavenLocal()
    maven { url = uri("https://raw.githubusercontent.com/MeteorLite/hosting/main/repo/") }
    mavenCentral()
}

dependencies {
    //Required libraries
    //meteor-client-1.7-r2.jar
    compileOnly(group = "meteor", name = "client", version = apiRelease)
    compileOnly(group = "meteor", name = "api", version = apiRelease)
    implementation(group ="org.rationalityfrontline" ,name = "kevent", version ="2.1.4")
}

tasks {
    compileJava {
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
    }

    compileKotlin {
        kotlinOptions {
            jvmTarget = "17"
            apiVersion = "1.7"
            languageVersion = "1.7"
            freeCompilerArgs = listOf("-Xjvm-default=all")
        }
    }
    jar {
        finalizedBy(":export")
        manifest {
            attributes["Main-Class"] = pluginClass
        }
    }
}

tasks.register<Copy>("export") {
    from(outputJar)
    into(exportDir)
}