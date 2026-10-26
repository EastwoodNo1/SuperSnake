package sdlc.checks

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.writeText
import kotlin.test.assertEquals

class CliTest {
    private val files =
        mapOf(
            "clean.kt" to "val a = 1",
            "dirty.kt" to "val a = 1\n// x",
            "dirty.yml" to "# y",
        )

    private fun run(vararg args: String): Pair<Int, List<String>> {
        val printed = mutableListOf<String>()
        val code = Cli.run(args.toList(), { files.getValue(it) }, { printed += it })
        return code to printed
    }

    @Test
    fun noFindingsExitsZeroAndPrintsNothing() {
        val (code, printed) = run("no-comments", "clean.kt")
        assertEquals(0, code)
        assertEquals(emptyList<String>(), printed)
    }

    @Test
    fun findingsExitOneAndArePrintedAsPathLineMessage() {
        val (code, printed) = run("no-comments", "clean.kt", "dirty.kt", "dirty.yml")
        assertEquals(1, code)
        assertEquals(
            listOf(
                "dirty.kt:2: comment not allowed (why/what belong in the commit message)",
                "dirty.yml:1: comment not allowed (why/what belong in the commit message)",
            ),
            printed,
        )
    }

    @Test
    fun noArgumentsPrintsUsageAndExitsTwo() {
        val (code, printed) = run()
        assertEquals(2, code)
        assertEquals(listOf("usage: checks no-comments <path...>"), printed)
    }

    @Test
    fun unknownSubcommandPrintsUsageAndExitsTwo() {
        val (code, printed) = run("nope", "clean.kt")
        assertEquals(2, code)
        assertEquals(listOf("usage: checks no-comments <path...>"), printed)
    }

    @Test
    fun noCommentsWithoutPathsPrintsUsageAndExitsTwo() {
        val (code, printed) = run("no-comments")
        assertEquals(2, code)
        assertEquals(listOf("usage: checks no-comments <path...>"), printed)
    }

    @Test
    fun mainReadsRealFilesAndTerminatesWithTheExitCode(
        @TempDir dir: Path,
    ) {
        val clean = dir.resolve("a.kt").also { it.writeText("val a = 1") }
        val dirty = dir.resolve("b.kt").also { it.writeText("val b = 1 // x") }
        val exits = mutableListOf<Int>()
        terminate = { exits += it }
        try {
            main(arrayOf("no-comments", clean.toString()))
            main(arrayOf("no-comments", dirty.toString()))
            main(arrayOf())
        } finally {
            terminate = System::exit
        }
        assertEquals(listOf(0, 1, 2), exits)
    }
}
