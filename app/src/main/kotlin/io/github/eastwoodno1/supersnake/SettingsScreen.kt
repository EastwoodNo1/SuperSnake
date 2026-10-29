package io.github.eastwoodno1.supersnake

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.eastwoodno1.supersnake.domain.ControlScheme
import io.github.eastwoodno1.supersnake.domain.Difficulty
import io.github.eastwoodno1.supersnake.domain.NeonPalette

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    modifier: Modifier = Modifier,
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(stringResource(R.string.settings), Modifier.semantics { heading() }, color = Color(NeonPalette.SNAKE))
        Text(stringResource(R.string.difficulty), color = Color(NeonPalette.TEXT))
        FlowRow(
            Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Difficulty.entries.forEach { level ->
                val description = stringResource(R.string.difficulty_option, level.label)
                Choice(
                    level.label,
                    settings.difficulty == level,
                    { viewModel.setDifficulty(level) },
                    Modifier.semantics {
                        contentDescription =
                            description
                    },
                )
            }
        }
        SwitchRow(stringResource(R.string.sound), settings.soundOn, viewModel::setSound)
        SwitchRow(stringResource(R.string.haptics), settings.hapticsOn, viewModel::setHaptics)
        Text(stringResource(R.string.control_scheme), color = Color(NeonPalette.TEXT))
        Row(Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Choice(
                stringResource(R.string.control_swipe),
                settings.controlScheme == ControlScheme.SWIPE,
                { viewModel.setControlScheme(ControlScheme.SWIPE) },
            )
            Choice(
                stringResource(R.string.control_dpad),
                settings.controlScheme == ControlScheme.DPAD,
                { viewModel.setControlScheme(ControlScheme.DPAD) },
            )
        }
    }
}

@Composable
private fun Choice(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val orb = Color(NeonPalette.ORB)
    Surface(
        modifier
            .heightIn(min = 48.dp)
            .widthIn(min = 48.dp)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = if (selected) orb else Color.Transparent,
        contentColor = if (selected) Color(NeonPalette.ON_PRIMARY) else Color(NeonPalette.TEXT),
        border = BorderStroke(2.dp, orb),
    ) {
        Row(Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(label)
        }
    }
}

@Composable
private fun SwitchRow(
    label: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .toggleable(value = checked, role = Role.Switch, onValueChange = onChange),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = Color(NeonPalette.TEXT))
        Switch(checked = checked, onCheckedChange = null)
    }
}
