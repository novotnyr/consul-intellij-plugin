plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.6.0"
}

group = "com.github.novotnyr"
version = "7-SNAPSHOT"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    implementation("com.ecwid.consul:consul-api:1.4.5")
    implementation("com.google.code.gson:gson:2.8.9")
    implementation("commons-codec:commons-codec:1.13")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")

    testImplementation("junit:junit:4.13.2")

    intellijPlatform {
        intellijIdeaCommunity("2022.3.3")
        pluginVerifier()
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "223"
            untilBuild = "252.*"
        }
        changeNotes = """
            <ul>
            <li>Require at least Platform 2022.3</li>
            <li>Migrate to newer platform API</li>
            </ul>
        """.trimIndent()
    }
    publishing {
        val intellijPublishToken: String by project
        token = intellijPublishToken
    }
    pluginVerification {
        ides {
            recommended()
        }
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}