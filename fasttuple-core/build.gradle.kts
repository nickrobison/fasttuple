plugins {
    id("info.solidsoft.pitest")
    id("com.gradleup.shadow")
}

val junitVersion = "5.12.0"

dependencies {
    api("org.codehaus.janino:janino:${rootProject.ext.get("janinoVersion")}")
    implementation("org.codehaus.janino:commons-compiler:${rootProject.ext.get("janinoVersion")}")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

configure<info.solidsoft.gradle.pitest.PitestPluginExtension> {
    junit5PluginVersion.set("0.12")
    targetClasses.add("com.nickrobison.tuple.*")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    relocate("org.codehaus.janino", "shadow.janino")
}