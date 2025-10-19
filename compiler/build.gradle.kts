plugins {
  kotlin("jvm") version "2.3.0-Beta1"
  id("com.vanniktech.maven.publish") version "0.34.0"
}

kotlin {
  jvmToolchain(21)
  compilerOptions {
    freeCompilerArgs.add("-Xcontext-parameters")
  }
}

mavenPublishing {
  coordinates(groupId = "land.sungbin", artifactId = "kotlin-dumper", version = "0.1.8")
}

dependencies {
  compileOnly(kotlin("stdlib", version = "2.3.0-Beta1"))
  compileOnly(kotlin("compiler-embeddable", version = "2.3.0-Beta1"))
  compileOnly(kotlin("compose-compiler-plugin-embeddable", version = "2.3.0-Beta1")) // because("IrElement.dumpSrc()")
}
