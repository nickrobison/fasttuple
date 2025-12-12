plugins {
    signing
    `maven-publish`
    id("info.solidsoft.pitest")
    id("org.jreleaser")
}

val junitVersion = "5.12.1"

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

tasks.jar {
    manifest {
        attributes("Automatic-Module-Name" to "com.nickrobison.fasttuple")
    }
}

val isRelease = !version.toString().endsWith("SNAPSHOT")

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])

            pom {
                name.set(project.name)
                description.set(project.description)
                url.set("https://github.com/nickrobison/fasttuple")
                inceptionYear.set("2014")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
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

                scm {
                    connection.set("scm:git:git@github.com:nickrobison/fasttuple.git")
                    developerConnection.set("scm:git:git@github.com:nickrobison/fasttuple.git")
                    url.set("https://github.com/nickrobison/fasttuple")
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/nickrobison/fasttuple/issues")
                }
            }
        }
    }

    repositories {
        maven {
            url =
                layout.buildDirectory
                    .dir("staging-deploy")
                    .get()
                    .asFile
                    .toURI()
        }
    }
}

signing {
    isRequired = isRelease
    useGpgCmd()
    if (isRequired) {
        sign(publishing.publications["maven"])
    }
}

// Credentials for JReleaser
val mavenCentralUsername = findProperty("sonatypeUsername") as String? ?: System.getenv("MAVEN_USER") ?: ""
val mavenCentralPassword = findProperty("sonatypePassword") as String? ?: System.getenv("MAVEN_PASSWORD") ?: ""

jreleaser {
    project {
        name.set("fasttuple")
        description.set(
            "FastTuple is a library for generating heterogeneous tuples of primitive types from a runtime defined schema without boxing.",
        )
        authors.set(listOf("Nick Robison", "Cliff Moon", "Philip Warren"))
        license.set("Apache-2.0")
        inceptionYear.set("2014")
        links {
            homepage.set("https://github.com/nickrobison/fasttuple")
        }
    }

    gitRootSearch.set(true)

    signing {
        active.set(org.jreleaser.model.Active.ALWAYS)
        armored.set(true)
        mode.set(org.jreleaser.model.Signing.Mode.COMMAND)
    }

    deploy {
        maven {
            mavenCentral {
                create("sonatype") {
                    active.set(org.jreleaser.model.Active.ALWAYS)
                    url.set("https://central.sonatype.com/api/v1/publisher")
                    stagingRepository(
                        layout.buildDirectory
                            .dir("staging-deploy")
                            .get()
                            .asFile.path,
                    )
                    username.set(mavenCentralUsername)
                    password.set(mavenCentralPassword)
                }
            }
        }
    }
}
