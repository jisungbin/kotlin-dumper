package land.sungbin.kotlindumper

import androidx.compose.compiler.plugins.kotlin.lower.dumpSrc
import androidx.compose.compiler.plugins.kotlin.lower.fastForEachIndexed
import java.io.File
import land.sungbin.kotlindumper.KotlinDumperPluginRegistrar.Companion.PATH
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.DumpIrTreeOptions
import org.jetbrains.kotlin.ir.util.dump

class IrDumpExtension(private val coordinator: WorkStealingDumpCoordinator) : IrGenerationExtension {
  override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
    coordinator.awaitCompletion()

    moduleFragment.files
      .asSequence()
      .chunked(30)
      .forEachIndexed { chunkedIndex, files ->
        files.fastForEachIndexed { index, file ->
          coordinator.submitIrOnly(
            dstPath = {
              val srcPath = file.fileEntry.name
              val pkgPath = file.packageFqName.asString().replace('.', '/')
              val baseName =
                srcPath.substringAfterLast('/')
                  .substringBeforeLast('.', missingDelimiterValue = "file_${chunkedIndex * 30 + index}")
                  .ifEmpty { "file_${chunkedIndex * 30 + index}" }

              File(PATH, "$pkgPath/$baseName")
            },
            irString = { file.dump(IR_DUMP_OPTION) },
            irKtString = { file.dumpSrc(useFir = true) },
          )
        }

        coordinator.awaitCompletion()
      }
  }

  private companion object {
    val IR_DUMP_OPTION = DumpIrTreeOptions()
  }
}