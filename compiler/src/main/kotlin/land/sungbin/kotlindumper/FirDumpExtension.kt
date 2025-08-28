package land.sungbin.kotlindumper

import java.io.File
import land.sungbin.kotlindumper.KotlinDumperPluginRegistrar.Companion.PATH
import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFileChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.declarations.FirFile
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.packageFqName
import org.jetbrains.kotlin.fir.render
import org.jetbrains.kotlin.fir.renderWithType
import org.jetbrains.kotlin.psi

class FirDumpExtension(private val coordinator: WorkStealingDumpCoordinator) : FirExtensionRegistrar() {
  override fun ExtensionRegistrarContext.configurePlugin() {
    +FirFileDumpCheckers.Factory(coordinator)
  }

  private class FirFileDumpCheckers(
    session: FirSession,
    private val coordinator: WorkStealingDumpCoordinator,
  ) : FirAdditionalCheckersExtension(session) {
    class Factory(private val coordinator: WorkStealingDumpCoordinator) : FirAdditionalCheckersExtension.Factory {
      override fun create(session: FirSession): FirAdditionalCheckersExtension = FirFileDumpCheckers(session, coordinator)
    }

    override val declarationCheckers: DeclarationCheckers =
      object : DeclarationCheckers() {
        override val fileCheckers: Set<FirFileChecker> = setOf(FileDumper())
      }

    inner class FileDumper : FirFileChecker(MppCheckerKind.Common) {
      context(context: CheckerContext, reporter: DiagnosticReporter)
      override fun check(declaration: FirFile) {
        coordinator.submitFirOnly(
          dstPath = {
            val pkgPath = declaration.packageFqName.asString().replace('.', '/')
            File(PATH, "$pkgPath/${deriveBaseName(declaration)}")
          },
          firString = { declaration.render() },
        )
      }
    }

    private fun deriveBaseName(firFile: FirFile): String {
      val srcPath: String? =
        (firFile.source?.psi?.containingFile?.virtualFile?.path)
          ?: (firFile.sourceFile?.path)
          ?: (firFile.sourceFile?.name)

      return srcPath?.substringAfterLast('/')
        ?.substringBeforeLast('.', missingDelimiterValue = "file_${firFile.hashCode()}")
        ?: "file_${firFile.hashCode()}"
    }
  }
}