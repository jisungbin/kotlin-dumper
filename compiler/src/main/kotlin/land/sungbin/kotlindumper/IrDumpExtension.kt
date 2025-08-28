package land.sungbin.kotlindumper

import androidx.compose.compiler.plugins.kotlin.lower.dumpSrc
import java.io.File
import land.sungbin.kotlindumper.KotlinDumperPluginRegistrar.Companion.PATH
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.DumpIrTreeOptions
import org.jetbrains.kotlin.ir.util.dump

class IrDumpExtension(private val coordinator: WorkStealingDumpCoordinator) : IrGenerationExtension {
  override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
    moduleFragment.files.forEachIndexed { idx, irFile ->
      coordinator.submitIrOnly(
        dstPath = {
          val srcPath = irFile.fileEntry.name
          val pkgPath = irFile.packageFqName.asString().replace('.', '/')
          val baseName =
            srcPath.substringAfterLast('/')
              .substringBeforeLast('.', missingDelimiterValue = "file_$idx")
              .ifEmpty { "file_$idx" }

          File(PATH, "$pkgPath/$baseName")
        },
        irString = { irFile.dump(IR_DUMP_OPTION) },
        irKtString = { irFile.dumpSrc(useFir = true) },
      )
    }
  }

  private companion object {
    val IR_DUMP_OPTION = DumpIrTreeOptions()
  }
}