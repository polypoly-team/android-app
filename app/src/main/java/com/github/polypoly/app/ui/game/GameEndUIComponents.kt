package com.github.polypoly.app.ui.game

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.ui.theme.Padding
import com.github.polypoly.app.ui.theme.PolypolyTheme
import com.github.polypoly.app.ui.theme.UIElements

/**
 * All the UI displayed at the end of a game is available here
 */

// ============================================================== GLOBAL UI

@Composable
fun GameEndUI(lastGame: Game, quitAction: () -> Unit) {
    Dialog(onDismissRequest = {}) {
        Surface(
            modifier = Modifier.padding(Padding.medium),
            color = Color.Transparent
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.5f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                GameBoard(getPlayersInRankingOrder(lastGame))
                Spacer(modifier = Modifier.height(Padding.large))
                UIElements.BigButton(onClick = quitAction, text = "Return to menu")
            }

        }
    }
}

// ============================================================== PRIVATE COMPONENTS

@Composable
private fun GameBoard(orderedPlayers: List<Player>) {
    Surface(
        shape = RoundedCornerShape(Padding.medium)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.primary)
                .padding(Padding.large),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Padding.large)
        ) {
            Text(
                text = "End of the game!",
                style = MaterialTheme.typography.h5
            )
            LeaderBoard(orderedPlayers)
        }
    }
}

@Composable
private fun LeaderBoard(orderedPlayers: List<Player>) {
    Surface(
        shape = RoundedCornerShape(Padding.medium)
    ) {
        Column {
            for (i in orderedPlayers.indices) {
                LeaderBoardRow(player = orderedPlayers[i], rank = i)
            }
        }
    }
}

@Composable
private fun LeaderBoardRow(player: Player, rank: Int) {
    var textColor = MaterialTheme.colors.onPrimary
    var color = MaterialTheme.colors.secondary

    if(player.user.currentUser) {
        color = MaterialTheme.colors.onSecondary
    } else if(rank % 2 != 0) {
        color = MaterialTheme.colors.secondaryVariant
        textColor = MaterialTheme.colors.onSecondary
    }
    Row(
        modifier = Modifier
            .background(color)
            .fillMaxWidth()
            .padding(Padding.small)
    ) {
        Text(text = "$rank. ${player.user.name}", color = textColor)
        Spacer(modifier = Modifier.weight(1f))
        Text(text = "${player.getBalance()}$", color = textColor)
    }

}

// ============================================================== HELPERS

private fun getPlayersInRankingOrder(game: Game): List<Player> {
    return game.players.sortedBy { game.ranking()[it.user.key] }
}

// ============================================================== PREVIEW

@Preview
@Composable
private fun GameEndUiPreview() {
    PolypolyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.Black
        ) {
            //GameEndUI()
        }
    }
}