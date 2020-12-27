plugins {
    java
    signing
    `maven-publish`
    jacoco
    id("org.sonarqube") version "3.0"
    id("info.solidsoft.pitest") version "1.5.2"
}

val janinoVersion by extra("3.1.0")

allprojects {
    group = "com.nickrobison"
    version = "0.3.2-SNAPSHOT"
    val isRelease = !version.toString().endsWith("SNAPSHOT")

    apply(plugin = "signing")
    apply(plugin = "org.sonarqube")
    apply(plugin = "java")
    apply(plugin = "jacoco")
    apply(plugin = "maven-publish")
    apply(plugin = "info.solidsoft.pitest")

    repositories {
        jcenter()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        withJavadocJar()
        withSourcesJar()
    }

    dependencies {
        val implementation by configurations
        implementation("org.codehaus.janino:janino:$janinoVersion")
        implementation("com.google.guava:guava:30.1-jre")
    }

    tasks.jacocoTestReport {
        reports {
            xml.isEnabled = true
        }
    }

    configure<info.solidsoft.gradle.pitest.PitestPluginExtension> {
        junit5PluginVersion.set("0.12")
        targetClasses.add("com.nickrobison.tuple.*")

        if (project.name in setOf("fasttuple-bench")) {
            failWhenNoMutations.set(false)
        }
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
            }
        }

        repositories {
            maven {
                credentials {
                    val bintrayUsername: String? by project
                    val bintrayPassword: String? by project
                    username = bintrayUsername ?: System.getenv("BINTRAY_USER")
                    password = bintrayPassword ?: System.getenv("BINTRAY_APIKEY")
                }
                val releasesRepoUrl = "https://api.bintray.com/maven/nickrobison/maven/fasttuple/;publish=1"
                val snapshotsRepoUrl = "http://oss.jfrog.org/artifactory/oss-snapshot-local"
                url = uri(if (isRelease) releasesRepoUrl else snapshotsRepoUrl)
                name = "bintray-nickrobison-maven"
            }
        }
    }

    signing {
        isRequired = isRelease
        useGpgCmd()
        if (isRequired) {
            sign(publishing.publications["mavenJava"])
        }
    }
}