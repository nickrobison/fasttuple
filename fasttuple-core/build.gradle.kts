import net.researchgate.release.ReleaseExtension

plugins {
    signing
    `maven-publish`
    id("info.solidsoft.pitest")
    id("com.gradleup.shadow")
    id("net.researchgate.release")
}

val junitVersion = "6.0.0"

dependencies {
    api("org.codehaus.janino:janino:${rootProject.ext.get("janinoVersion")}")
    implementation("org.codehaus.janino:commons-compiler:${rootProject.ext.get("janinoVersion")}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

configure<info.solidsoft.gradle.pitest.PitestPluginExtension> {
    pitestVersion = "1.19.0"
    junit5PluginVersion = "1.2.2"
    threads = 4
    targetClasses.add("com.nickrobison.tuple.*")
    excludedClasses.add("com.nickrobison.tuple.*DirectTuple*")
    outputFormats.add("HTML")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    archiveClassifier = ""
    relocate("org.codehaus.janino", "shadow.janino")
}



tasks.jar {
    manifest {
        attributes("Automatic-Module-Name" to "com.nickrobison.fasttuple")
    }
}

val isRelease = !version.toString().endsWith("SNAPSHOT")
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
            from(components["shadow"])
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

configure<ReleaseExtension> {
    with(git) {
        requireBranch = "master"
        signTag = true
    }
}