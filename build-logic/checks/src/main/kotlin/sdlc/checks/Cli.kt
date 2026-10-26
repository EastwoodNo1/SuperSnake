package sdlc.checks

import java.io.File

object Cli {
    const val USAGE = "usage: checks no-comments <path...>"

    fun run(
        args: List<String>,
        readFile: (String) -> String,
        out: (String) -> Unit,
    ): Int {
        val paths = args.drop(1)
        if (args.firstOrNull() != "no-comments" || paths.isEmpty()) {
            out(USAGE)
            return 2
        }
        val findings = paths.flatMap { NoComments.scan(it, readFile(it)) }
        findings.forEach { out(it.toString()) }
        return if (findings.isEmpty()) 0 else 1
    }
}

internal var terminate: (Int) -> Unit = System::exit

fun main(args: Array<String>) {
    terminate(Cli.run(args.toList(), { File(it).readText() }, ::println))
}
