pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url =uri("https://www.jitpack.io")}
        maven { url =uri("https://storage.zego.im/maven")}
    }
}

rootProject.name = "Health_Connect"
include(":app")
 