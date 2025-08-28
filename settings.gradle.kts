@file:Suppress("UnstableApiUsage")

rootProject.name = "kotlin-dumper"

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

pluginManagement {
  repositories {
    gradlePluginPortal()
    mavenCentral {
      mavenContent {
        releasesOnly()
      }
    }
    mavenLocal()
  }
}

dependencyResolutionManagement {
  repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
  repositories {
    mavenCentral {
      mavenContent {
        releasesOnly()
      }
    }
    mavenLocal()
  }
}

include(":compiler")
