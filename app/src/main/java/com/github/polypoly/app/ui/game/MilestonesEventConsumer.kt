package com.github.polypoly.app.ui.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.github.polypoly.app.base.game.GameMilestoneRewardTransaction

/**
 * Dialog to show the locations the user owns.
 * Consumes the [milestonesToDisplay] state and shows a dialog for each milestone.
 * @param milestonesToDisplay The list of milestones to watch.
 */
@Composable
fun MilestoneEventConsumer(
    milestonesToDisplay: State<ArrayList<GameMilestoneRewardTransaction>>
) {
    var showDialog by remember { mutableStateOf(false) }
    var currentMilestone by remember { mutableStateOf<GameMilestoneRewardTransaction?>(null) }

    LaunchedEffect(key1 = milestonesToDisplay.value) {
        if (milestonesToDisplay.value.isNotEmpty()) {
            currentMilestone = milestonesToDisplay.value.first()
            showDialog = true
        }
    }

    if (showDialog && currentMilestone != null) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                milestonesToDisplay.value.remove(currentMilestone)

                if(milestonesToDisplay.value.isNotEmpty()){
                    currentMilestone = milestonesToDisplay.value.first()
                    showDialog = true
                }
            },
            text = {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = currentMilestone!!.getTransactionDescription())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        milestonesToDisplay.value.remove(currentMilestone)

                        if(milestonesToDisplay.value.isNotEmpty()){
                            currentMilestone = milestonesToDisplay.value.first()
                            showDialog = true
                        }
                    }
                ) {
                    Text(text = "COOL!")
                }
            },
        )
    }
}
