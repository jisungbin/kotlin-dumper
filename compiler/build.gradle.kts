plugins {
  kotlin("jvm") version "2.2.10"
  id("com.vanniktech.maven.publish") version "0.34.0"
}

kotlin {
  jvmToolchain(21)
  compilerOptions {
    freeCompilerArgs.add("-Xcontext-parameters")
  }
}

mavenPublishing {
  coordinates(groupId = "land.sungbin", artifactId = "kotlin-dumper", version = "0.1.2")
}

dependencies {
  compileOnly(kotlin("stdlib", version = "2.2.10"))
  compileOnly(kotlin("compiler-embeddable", version = "2.2.10"))
  compileOnly(kotlin("compose-compiler-plugin-embeddable", version = "2.2.10")) // because("IrElement.dumpSrc()")
}
