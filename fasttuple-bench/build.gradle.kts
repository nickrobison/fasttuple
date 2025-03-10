plugins {
    application
    id("com.gradleup.shadow")
}

val jmhVersion = "1.37"
val objectPoolVersion = "1.1.2"

dependencies {
    implementation(project(":fasttuple-core"))
    implementation("org.openjdk.jmh:jmh-core:$jmhVersion")
    implementation("nf.fr.eraasoft:objectpool:$objectPoolVersion")
    annotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:$jmhVersion")
}

application {
    mainClass.set("org.openjdk.jmh.Main")
}

tasks.shadowJar {
    archiveFileName.set("microbenchmarks.jar")
}