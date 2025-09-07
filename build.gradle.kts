plugins {
    `java-library`
    `maven-publish`
    id("com.adarshr.test-logger") version "3.2.0"
}

group = "io.github.dualuse"
version = "1.4.0"

description = "Graphics 3D"

java {
    // Match Maven's source/target 1.6
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
}

tasks.test {
    useJUnitPlatform()

    testlogger {
        setTheme("standard")
    }
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = group.toString()
            artifactId = "Graphics3D"
            version = version.toString()
            pom {
                name.set("Graphics 3D")
                description.set("A lightweight extension to java.awt.Graphics2D featuring OpenGL-style 3D transformations and primitives")

                licenses {
                    license {
                        name = "GNU Lesser General Public License v3.0"
                        url = "https://www.gnu.org/licenses/lgpl-3.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "holzschneider"
                        name = "Philipp Holzschneider"
                        email = "philipp@covicon.de"
                    }
                }
                scm {
                    connection = "scm:git:git://example.com/my-library.git"
                    developerConnection = "scm:git:ssh://example.com/my-library.git"
                    url = "http://example.com/my-library/"
                }
            }
            }
        }
    }
}

// Configure the Gradle Wrapper generation without committing wrapper files
// Usage: run with a system Gradle installation:
//   gradle createWrapper
// This will generate gradlew/gradlew.bat and gradle/wrapper files for the specified version.

tasks.wrapper {
    gradleVersion = "8.14.2"
    distributionType = Wrapper.DistributionType.ALL
}

// Friendly alias so users don't have to remember the built-in task name
tasks.register("createWrapper") {
    group = "build setup"
    description = "Generates the Gradle Wrapper scripts for the configured version"
    dependsOn(tasks.named("wrapper"))
}
