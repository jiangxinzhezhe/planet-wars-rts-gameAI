package games.planetwars.view

import games.planetwars.agents.SmartGreedyAgent
import games.planetwars.agents.random.CarefulRandomAgent
import games.planetwars.core.GameParams
import games.planetwars.core.GameStateFactory
import games.planetwars.runners.GameRunner
import xkg.jvm.AppLauncher

fun main() {
    val gameParams = GameParams(numPlanets = 30, maxTicks = 1000)
    val gameState = GameStateFactory(gameParams).createGame()
    val agent1 = CarefulRandomAgent()
    val agent2 = SmartGreedyAgent()
    // sub in different agents as needed
    //    val agent1 = PureRandomAgent()
    val gameRunner = GameRunner(agent1, agent2, gameParams)

    val title = "${agent1.getAgentType()} : Planet Wars : ${agent2.getAgentType()}"
    AppLauncher(
                    preferredWidth = gameParams.width,
                    preferredHeight = gameParams.height,
                    app =
                            GameView(
                                    params = gameParams,
                                    gameState = gameState,
                                    gameRunner = gameRunner
                            ),
                    title = title,
                    frameRate = 100.0,
            )
            .launch()
}
