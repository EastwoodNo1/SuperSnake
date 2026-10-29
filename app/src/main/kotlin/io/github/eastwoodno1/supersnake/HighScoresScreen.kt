package io.github.eastwoodno1.supersnake

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.eastwoodno1.supersnake.domain.NeonPalette
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun HighScoresScreen(
    viewModel: HighScoresViewModel,
    modifier: Modifier = Modifier,
) {
    val scores by viewModel.scores.collectAsStateWithLifecycle()
    val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
    val text = Color(NeonPalette.TEXT)
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(stringResource(R.string.high_scores), Modifier.semantics { heading() }, color = Color(NeonPalette.SNAKE))
        if (scores.isEmpty()) {
            Text(stringResource(R.string.no_scores), color = text)
        }
        scores.forEachIndexed { index, entry ->
            val level = stringResource(R.string.level_description, entry.difficulty.label)
            Row(
                Modifier.fillMaxWidth().semantics(mergeDescendants = true) {},
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(stringResource(R.string.rank, index + 1), color = text)
                Text(entry.score.toString(), color = text)
                Text(entry.difficulty.label, Modifier.semantics { contentDescription = level }, color = text)
                Text(entry.date.format(formatter), color = text)
            }
        }
    }
}
