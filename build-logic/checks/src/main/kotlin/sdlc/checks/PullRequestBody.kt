package sdlc.checks

object PullRequestBody {
    const val EVIDENCE_PLACEHOLDER = "Paste the exact command and its real output in a fenced block."
    private const val EVIDENCE_HEADING = "## Evidence"
    private const val EXEMPT_LABEL = "dependencies"
    private val issueLink = Regex("(?i)\\b(closes|fixes|resolves) #\\d+")
    private val filledFence = Regex("(?s)```[^\\n]*\\n\\s*\\S.*?```")

    fun validate(
        body: String,
        labels: Set<String>,
    ): List<String> =
        buildList {
            if (EXEMPT_LABEL !in labels && !issueLink.containsMatchIn(body)) {
                add("link an issue with Closes #<n> (or Fixes/Resolves)")
            }
            val lines = body.lines()
            val start = lines.indexOfFirst { it.trim() == EVIDENCE_HEADING }
            if (start < 0) {
                add("add an $EVIDENCE_HEADING section with the exact command and its real output")
            } else {
                val section =
                    lines
                        .drop(start + 1)
                        .takeWhile { !it.startsWith("## ") }
                        .joinToString("\n")
                        .replace(EVIDENCE_PLACEHOLDER, "")
                if (!filledFence.containsMatchIn(section)) {
                    add("fill in the Evidence section with a fenced block holding the command and its real output")
                }
            }
        }
}
