import net.ltgt.gradle.errorprone.errorprone

plugins {
    java
    jacoco
    id("org.sonarqube") version "7.2.3.7755"
    id("info.solidsoft.pitest") version "1.19.0-rc.3" apply(false)
    id("org.jreleaser") version "1.23.0" apply(false)
    id("net.ltgt.errorprone") version "5.1.0"
    id("com.gradleup.shadow") version "9.4.0" apply(false)
}

val janinoVersion by extra("3.1.12")
val errorProneVersion by extra("2.31.0")

allprojects {
    description = "FastTuple is a library for generating heterogeneous tuples of primitive types from a runtime defined schema without boxing."

    apply(plugin = "org.sonarqube")
    apply(plugin = "java-library")
    apply(plugin = "jacoco")
    apply(plugin = "net.ltgt.errorprone")

    repositories {
        mavenCentral()
    }

    dependencies {
        errorprone("com.google.errorprone:error_prone_core:${errorProneVersion}")

    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(11))
        }
        withJavadocJar()
        withSourcesJar()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.errorprone.disableWarningsInGeneratedCode = true
    }

    val testJavaVersion = providers.gradleProperty("testJavaVersion").map { v ->
        v.toIntOrNull() ?: throw GradleException("Property 'testJavaVersion' must be a valid integer, got: '$v'")
    }.orElse(11)
    tasks.withType<Test>().configureEach {
        javaLauncher.set(javaToolchains.launcherFor {
            languageVersion.set(testJavaVersion.map { JavaLanguageVersion.of(it) })
        })
    }

    tasks.jacocoTestReport {
        reports {
            xml.required.set(true)
        }
    }

    sonarqube {
        properties {
            property("sonar.projectKey", "nickrobison_fasttuple")
            property("sonar.organization", "nickrobison-github")
            property("sonar.host.url", "https://sonarcloud.io")
        }
    }
}
