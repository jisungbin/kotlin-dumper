@file:Suppress("DEPRECATION")

package land.sungbin.kotlindumper

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.com.intellij.mock.MockProject
import org.jetbrains.kotlin.com.intellij.openapi.extensions.LoadingOrder
import org.jetbrains.kotlin.compiler.plugin.ComponentRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.messageCollector
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter

@OptIn(ExperimentalCompilerApi::class)
class KotlinDumperPluginRegistrar : ComponentRegistrar {
  override val supportsK2: Boolean get() = true

  override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
    println("Hi. This is println.")
    configuration.messageCollector.report(CompilerMessageSeverity.LOGGING, "Hi. This is messageCollector logging.")
    configuration.messageCollector.report(CompilerMessageSeverity.INFO, "Hi. This is messageCollector info.")
    configuration.messageCollector.report(CompilerMessageSeverity.WARNING, "Hi. This is messageCollector warn.")

    val coordinator = WorkStealingDumpCoordinator(configuration.messageCollector)

//    project.extensionArea
//      .getExtensionPoint(FirExtensionRegistrarAdapter.extensionPointName)
//      .registerExtension(FirDumpExtension(coordinator), LoadingOrder.LAST, project)

    project.extensionArea
      .getExtensionPoint(IrGenerationExtension.extensionPointName)
      .registerExtension(IrDumpExtension(coordinator), LoadingOrder.LAST, project)
  }

  companion object {
    const val PATH = "/Users/jisungbin/AndroidStudioProjects/ComposeMagic/app/kotlin-dump"
  }
}