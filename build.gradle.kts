plugins {
    `java-library`
    signing
    `maven-publish`
    jacoco
    id("org.sonarqube") version "4.4.1.3373"
    id("info.solidsoft.pitest") version "1.15.0" apply (false)
    id("net.researchgate.release") version "3.0.2"
}

val janinoVersion by extra("3.1.0")

allprojects {
    description = "FastTuple is a library for generating heterogeneous tuples of primitive types from a runtime defined schema without boxing."
    val isRelease = !version.toString().endsWith("SNAPSHOT")

    apply(plugin = "signing")
    apply(plugin = "org.sonarqube")
    apply(plugin = "java-library")
    apply(plugin = "jacoco")
    apply(plugin = "maven-publish")

    repositories {
        mavenCentral()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        withJavadocJar()
        withSourcesJar()
    }

    dependencies {
        val implementation by configurations
        val api by configurations
        api("org.codehaus.janino:janino:$janinoVersion")
        implementation("com.google.guava:guava:33.4.0-jre")
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

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                pom {
                    name.set(project.name)
                    description.set(project.description)
                    url.set("https://github.com/nickrobison/fasttuple")

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }

                    scm {
                        scm {
                            connection.set("git@github.com:nickrobison/fasttuple.git")
                            developerConnection.set("git@github.com:nickrobison/fasttuple.git")
                            url.set("https://github.com:nickrobison/fasttuple")
                        }
                    }

                    developers {
                        developer {
                            id.set("nick")
                            name.set("Nick Robison")
                            email.set("nick@nickrobison.com")
                        }
                        developer {
                            id.set("cliff")
                            name.set("Cliff Moon")
                            email.set("cliff@boundary.com")
                        }
                        developer {
                            id.set("philip")
                            name.set("Philip Warren")
                            email.set("philip@boundary.com")
                        }
                    }
                }
                from(components["java"])
            }
        }

        repositories {
            maven {
                credentials {
                    val sonatypeUsername: String? by project
                    val sonatypePassword: String? by project
                    username = sonatypeUsername ?: System.getenv("MAVEN_USER")
                    password = sonatypePassword ?: System.getenv("MAVEN_PASSWORD")
                }
                val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2"
                val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots"
                url = uri(if (isRelease) releasesRepoUrl else snapshotsRepoUrl)
                name = "maven-central"
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
