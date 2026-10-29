package io.github.eastwoodno1.supersnake

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import io.github.eastwoodno1.supersnake.domain.Phrase
import io.github.eastwoodno1.supersnake.domain.Spoken

val PHRASES: Map<Phrase, Int> =
    mapOf(
        Phrase.COUNTDOWN to R.string.phrase_countdown,
        Phrase.GO to R.string.phrase_go,
        Phrase.PAUSED to R.string.phrase_paused,
        Phrase.ORB_MILESTONE to R.string.phrase_orbs,
        Phrase.GAME_OVER to R.string.phrase_game_over,
        Phrase.WON to R.string.phrase_won,
        Phrase.NEW_HIGH_SCORE to R.string.phrase_new_high_score,
    )

@Composable
fun Announcer(
    spoken: Spoken?,
    modifier: Modifier = Modifier,
) {
    val lines = spoken?.lines.orEmpty()
    val said = lines.map { stringResource(PHRASES.getValue(it.phrase), *listOfNotNull(it.value).toTypedArray()) }.joinToString(". ")
    key(spoken?.sequence) {
        Box(
            modifier
                .size(1.dp)
                .testTag("announcer")
                .semantics {
                    liveRegion = LiveRegionMode.Polite
                    text = AnnotatedString(said)
                },
        )
    }
}
