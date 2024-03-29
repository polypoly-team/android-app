package com.github.polypoly.app.viewmodels.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.github.polypoly.app.base.game.Game
import com.github.polypoly.app.base.game.Player
import com.github.polypoly.app.base.game.PlayerState
import com.github.polypoly.app.base.game.TradeRequest
import com.github.polypoly.app.base.game.location.InGameLocation
import com.github.polypoly.app.base.game.location.LocationBid
import com.github.polypoly.app.base.game.location.LocationProperty
import com.github.polypoly.app.base.game.transactions.TaxTransaction
import com.github.polypoly.app.base.user.Stats
import com.github.polypoly.app.base.user.User
import com.github.polypoly.app.data.GameRepository
import com.github.polypoly.app.database.getAllValues
import com.github.polypoly.app.database.getValue
import com.github.polypoly.app.database.removeValue
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.currentUser
import com.github.polypoly.app.utils.global.GlobalInstances.Companion.remoteDB
import com.github.polypoly.app.utils.global.Settings.Companion.NUMBER_OF_LOCATIONS_ROLLED
import com.github.polypoly.app.viewmodels.commons.LoadingModel
import kotlinx.coroutines.*
import org.osmdroid.util.GeoPoint
import java.util.concurrent.CompletableFuture
import kotlin.random.Random
import kotlin.random.nextInt

class GameViewModel(
    game: Game,
    player: Player,
    private val coroutineScope: CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
): LoadingModel() {

    private val gameData: MutableLiveData<Game> = MutableLiveData(game)

    private val playerData: MutableLiveData<Player> = MutableLiveData(player)

    private val roundTurnData: MutableLiveData<Int> = MutableLiveData(game.currentRound)

    private val gameEndedData: MutableLiveData<Boolean> = MutableLiveData(false)

    private val playerStateData: MutableLiveData<PlayerState> = MutableLiveData(PlayerState.INIT)

    private val _successfulBidData: MutableLiveData<LocationBid> = MutableLiveData(null)
    val successfulBidData: LiveData<LocationBid> get() = _successfulBidData

    private val _locationsOwnedData: MutableLiveData<List<InGameLocation>> = MutableLiveData(game.getOwnedLocations(player))
    val locationsOwnedData: LiveData<List<InGameLocation>> get() = _locationsOwnedData

    private var currentTurnBid: LocationBid? = null

    private val tradeRequestData: MutableLiveData<TradeRequest> = MutableLiveData()

    private val _taxToPayData: MutableLiveData<InGameLocation> = MutableLiveData(null)
    val taxToPay: LiveData<InGameLocation> = _taxToPayData

    //used to determine if the player is close enough to a location to interact with it
    private val MAX_INTERACT_DISTANCE = 10.0 // meters

    init {
        setLoading(true)
        coroutineScope.launch {
            refreshInGameLocationsOwned()
            gameLoop()
            listenToTradeRequest()
        }
    }

    fun getTradeRequestData(): LiveData<TradeRequest> {
        return tradeRequestData
    }

    /**
     * Close a trade request
     */
    fun closeTradeRequest() {
        remoteDB.removeValue<TradeRequest>(tradeRequestData.value?.code ?: return).thenAccept {
            tradeRequestData.value = null
        }
    }

    /**
     * Update a trade request
     * @param trade The trade request to update
     */
    private fun updateTradeRequest(trade: TradeRequest) {
        remoteDB.updateValue(trade).thenAccept {
            tradeRequestData.value = null
            tradeRequestData.value = trade
        }
    }

    /**
     * Trade a location with another player
     * @param player the player to trade with
     * @param locationGiven the location the player wants to give
     * @param locationReceived the location the player wants to receive
     * @throws IllegalArgumentException if the player receiver does not own the location he/she wants to give
     * @throws IllegalArgumentException if the player does not own the location he/she wants to give
     */
    fun tradeWith(player: Player, locationGiven: InGameLocation, locationReceived: InGameLocation) {
        val currentPlayer = playerData.value ?: return
        if (locationGiven.owner != currentPlayer)
            throw IllegalArgumentException("The player does not own the location he/she wants to give")
        if (locationReceived.owner != player)
            throw IllegalArgumentException("The player receiver does not own the location he/she wants to give")
        locationGiven.owner = player
        locationReceived.owner = currentPlayer

        // TODO: push to DB here

        refreshInGameLocationsOwned()
    }

    /**
     * Accept or decline the trade request
     * @param accept True if the player accept the trade request, false otherwise
     */
    fun acceptOrDeclineTradeRequest(accept: Boolean) {
        val tradeRequest = tradeRequestData.value ?: return
        val currentPlayer = playerData.value ?: return
        if(tradeRequest.isReceiver(currentPlayer) ) {
            tradeRequest.currentPlayerReceiverAcceptance = accept
        }
        if(tradeRequest.isApplicant(currentPlayer)) {
            tradeRequest.currentPlayerApplicantAcceptance = accept
        }
        updateTradeRequest(tradeRequest)
    }

    /**
     * Update the location received in the trade request
     */
    fun updateReceiverLocationTradeRequest(location: InGameLocation) {
        val tradeRequest = tradeRequestData.value ?: return
        tradeRequest.locationReceived = location
        updateTradeRequest(tradeRequest)
    }

    /**
     * Listen to the trade request that are sent to the current player
     */
    private suspend fun listenToTradeRequest() {
        while (gameData.value?.isGameFinished() == false) {
            remoteDB.getAllValues<TradeRequest>().thenAccept { tradeRequests ->
                tradeRequests.forEach { tradeRequest ->
                    if (tradeRequest.playerReceiver.user.name == playerData.value?.user?.name
                        && tradeRequestData.value == null) {
                        tradeRequestData.value = tradeRequest
                    }
                }
            }
            delay(2500)
        }
    }

    fun createATradeRequest(playerReceiver: Player, locationGiven: InGameLocation) {
        val playerDataValue = playerData.value ?: return
        val tradeRequest = TradeRequest(
            playerApplicant = playerDataValue,
            playerReceiver = playerReceiver,
            locationGiven = locationGiven,
            locationReceived = null,
            currentPlayerApplicantAcceptance = null,
            currentPlayerReceiverAcceptance = null,
            code = "${playerReceiver.user.name}${playerDataValue.user.name}",
        )
        remoteDB.setValue(tradeRequest).thenAccept {
            tradeRequestData.value = tradeRequest
        }
    }

    fun getGameData(): LiveData<Game> {
        return gameData
    }

    fun getPlayerData(): LiveData<Player> {
        return playerData
    }

    fun getRoundTurnData(): LiveData<Int> {
        return roundTurnData
    }

    fun getGameFinishedData(): LiveData<Boolean> {
        return gameEndedData
    }

    fun getPlayerStateData(): LiveData<PlayerState> {
        return playerStateData
    }

    private suspend fun gameLoop() {
        var currentGame = gameData.value

        while (currentGame != null && !currentGame.isGameFinished()) {
            playerStateData.postValue(PlayerState.ROLLING_DICE)
            setLoading(false)

            delay(currentGame.rules.roundDuration.toLong() * 1000 * 60)

            nextTurn()

            currentGame = gameData.value
        }
    }

    /**
     * Computes the next turn state and synchronizes with the other players
     * @return a future that completes when the turn is synced with the other players
     */
    fun nextTurn(): CompletableFuture<Boolean> {
        setLoading(true)
        val completionFuture = CompletableFuture<Boolean>()

        coroutineScope.launch {
            gameData.value?.nextTurn()

            synchronizeGame().thenApply { syncSucceeded ->
                if (syncSucceeded) {
                    roundTurnData.value = gameData.value?.currentRound ?: -1
                    gameEndedData.value = gameData.value?.isGameFinished() ?: false
                }

                onNextTurnEnd()

                playerData.value = gameData.value?.getPlayer(playerData.value?.user?.id ?: "")

                setLoading(false)
                completionFuture.complete(syncSucceeded)
            }
        }

        return completionFuture
    }

    private fun onNextTurnEnd() {
        refreshInGameLocationsOwned()

        val game = gameData.value ?: return

        val previousBid = currentTurnBid
        if (previousBid != null &&
            game.getInGameLocation(previousBid.location)?.isTheOwner(playerData.value) == true) {
            _successfulBidData.value = previousBid
        }
        currentTurnBid = null
    }

    private fun synchronizeGame(): CompletableFuture<Boolean> {
        val gameUpdated = gameData.value ?: return CompletableFuture.completedFuture(false)
        return remoteDB.getValue<Game>(gameUpdated.key).thenCompose { gameFound ->
            if (gameFound.currentRound < gameUpdated.currentRound) {
                remoteDB.setValue(gameUpdated).thenCompose{
                    gameData.value = gameUpdated
                    refreshPlayer()
                    CompletableFuture.completedFuture(true)
                }
            } else { // up to date version already on live db
                gameData.value = gameFound
                CompletableFuture.completedFuture(true)
            }
        }
    }

    private fun playerStateFSMTransition(expectedFrom: PlayerState, to: PlayerState) {
        if (playerStateData.value != expectedFrom) {
            throw IllegalStateException("Illegal state transition from ${playerStateData.value} instead of $expectedFrom to $to")
        }
        playerStateData.value = to
    }

    /**
     * Ends ROLLING_DICE state and moves to MOVING
     */
    fun diceRolled() {
        playerStateFSMTransition(PlayerState.ROLLING_DICE, PlayerState.MOVING)
    }

    /**
     * Ends MOVING state and moves to INTERACTING
     */
    fun locationReached() {
        playerStateFSMTransition(PlayerState.MOVING, PlayerState.INTERACTING)
    }

    /**
     * Ends INTERACTING state and moves to BIDDING
     */
    fun startBidding() {
        playerStateFSMTransition(PlayerState.INTERACTING, PlayerState.BIDDING)
    }

    /**
     * Ends BIDDING state and moves back to INTERACTING
     */
    fun cancelBidding() {
        playerStateFSMTransition(PlayerState.BIDDING, PlayerState.INTERACTING)
    }

    /**
     * Ends BIDDING state and moves to TURN_FINISHED
     */
    fun endBidding() {
        playerStateFSMTransition(PlayerState.BIDDING, PlayerState.TURN_FINISHED)
    }

    /**
     * Resets player state back to the beginning of a turn (ie ROLLING_DICE)
     */
    fun resetTurnState() {
        playerStateData.value = PlayerState.ROLLING_DICE
    }

    fun endInteraction() {
        _taxToPayData.postValue(null)
        playerStateFSMTransition(PlayerState.INTERACTING, PlayerState.TURN_FINISHED)
    }

    /**
     * Computes the closest location to the given position among the existing locations in the game.
     * @param position Reference position
     * @return A future holding the closest location found or null if the closest location is farther than MAX_INTERACT_DISTANCE
     */
    fun computeClosestLocation(position: GeoPoint): CompletableFuture<LocationProperty?> {
        val result = CompletableFuture<LocationProperty?>()

        coroutineScope.launch {
            var closestLocation: LocationProperty? = null
            var closestDistance = Double.MAX_VALUE

            val allLocations = gameData.value?.getLocations() ?: listOf()
            for (location in allLocations) {
                val distance = position.distanceToAsDouble(location.position())
                if (distance < closestDistance) {
                    closestLocation = location
                    closestDistance = distance
                }
            }

            if (closestDistance > MAX_INTERACT_DISTANCE) {
                closestLocation = null
            }

            result.complete(closestLocation)
        }

        return result
    }

    /**
     * Rolls the dice and returns the location that corresponds to the sum of 2 dice rolls, 3 times
     * ensuring that the player does not visit the same location twice.
     * @param currentLocation current location that the player can interact with. May be null if no such location exist
     */
    fun rollDiceLocations(currentLocation: LocationProperty?): CompletableFuture<List<LocationProperty>> {
        val result = CompletableFuture<List<LocationProperty>>()

        coroutineScope.launch {
            val locationsNotToVisitName = mutableListOf<String>()
            if (currentLocation != null)
                locationsNotToVisitName.add(currentLocation.name)

            val allLocations = gameData.value?.getLocations() ?: listOf()

            val locationsToVisit = mutableListOf<LocationProperty>()
            for (i in 1..NUMBER_OF_LOCATIONS_ROLLED) {
                val closestLocations = allLocations
                    .filter { !locationsNotToVisitName.contains(it.name) }
                    .sortedBy { it.position().distanceToAsDouble(currentLocation?.position() ?: it.position()) }
                val diceRoll = Random.Default.nextInt(allLocations.indices)

                locationsToVisit.add(closestLocations[diceRoll])
                locationsNotToVisitName.add(closestLocations[diceRoll].name)
            }

            result.complete(locationsToVisit)
        }

        return result
    }

    /**
     * Resets all game related code
     * @return a future that contains true if the user was successfully updated
     */
    fun finishGame(): CompletableFuture<Boolean> {
        val isWinner = gameData.value?.ranking()?.get(playerData.value?.user?.key) == 1
        val user = currentUser!!
        val updatedStats = Stats(
            accountCreation = user.stats.accountCreation,
            lastConnection = user.stats.lastConnection,
            numberOfGames = user.stats.numberOfGames + 1,
            numberOfWins = user.stats.numberOfWins + if(isWinner) 1 else 0
        )
        val updatedUser = User(
            id = user.id,
            name = user.id,
            bio = user.bio,
            skin = user.skin,
            stats = updatedStats,
            trophiesWon = user.trophiesWon,
            trophiesDisplay = user.trophiesDisplay,
            currentUser = user.currentUser
        )
        val future = remoteDB.updateValue(updatedUser)
        GameRepository.game = null
        GameRepository.player = null
        GameRepository.gameCode = null
        return future
    }

    /*
     * Registers a bid on the location and amount provided
     * @param location location to bid on
     * @param bidAmount amount for the bid
     * @return a future that completes once the registering ended, holding true iff it succeeded
     */
    fun bidForLocation(location: LocationProperty, bidAmount: Int): CompletableFuture<Boolean> {
        val gameUpdated = gameData.value ?: return CompletableFuture.completedFuture(false)
        val player = playerData.value ?: return CompletableFuture.completedFuture(false)

        if (currentTurnBid != null || !player.canBuy(location, bidAmount))
            return CompletableFuture.completedFuture(false)

        val future = CompletableFuture<Boolean>()

        coroutineScope.launch {
            if (gameUpdated.getInGameLocation(location)?.owner != null || bidAmount > player.getBalance()) {
                future.complete(false)
            } else {
                val bid = LocationBid(location, player, bidAmount)
                gameUpdated.registerBid(bid)

                remoteDB.setValue(gameUpdated).thenApply {
                    gameData.value = gameUpdated
                    refreshPlayer()
                    currentTurnBid = bid
                    endBidding()
                    future.complete(true)
                }
            }
        }

        return future
    }

    fun refreshInGameLocationsOwned() {
        val player = playerData.value ?: return
        _locationsOwnedData.postValue(gameData.value?.getOwnedLocations(player))
    }

    fun bidOnLocationSelected(location: LocationProperty) {
        val game = gameData.value ?: return
        val inGameLocation = game.getInGameLocation(location) ?: return

        if (inGameLocation.owner == null) {
            startBidding()
        } else { // location already owned so we pay the tax instead
            _taxToPayData.value = inGameLocation
            game.transactions.add(TaxTransaction("tax", refreshPlayer(), false,
                inGameLocation.currentTax(), inGameLocation.owner!!, inGameLocation.locationProperty))
        }
    }

    private fun refreshPlayer(): Player {
        val player = gameData.value?.getPlayer(playerData.value?.user?.id ?: "") ?: Player()
        playerData.value = player
        return player
    }

    companion object {
        /**
         * Factory object for the GameLobbyWaitingViewModel
         * @see https://developer.android.com/topic/libraries/architecture/viewmodel?hl=fr#viewmodel-with-dependencies for pattern explanation
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                requireNotNull(GameRepository.game)
                requireNotNull(GameRepository.player)

                remoteDB.setValue(GameRepository.game!!)

                GameViewModel(
                    GameRepository.game!!,
                    GameRepository.player!!
                )
            }
        }
    }

}