package com.github.polypoly.app.base.game

import com.github.polypoly.app.data.GameRepository

class GameMilestoneRewardTransaction(private val milestoneNumber : Int) :
    GameTransaction(
        "Milestone Reward",
        if (GameRepository.player != null) GameRepository.player!! else Player(),
        false
    ){

    init{
        if(milestoneNumber < 0){
            throw IllegalArgumentException("Milestone number must be positive")
        }
    }

    companion object{
        const val milestoneRewardValue : Int = 1000
        const val milestoneRewardDistance : Int = 1000
    }


    override fun executeTransaction() {
        getInvolvedPlayer().earnMoney(milestoneRewardValue)
    }

    override fun getTransactionDescription(): String {
        return "You have reached the ${milestoneNumber * milestoneRewardDistance} meters milestone " +
                "and will earn $milestoneRewardValue by the end of the round!"
    }
}