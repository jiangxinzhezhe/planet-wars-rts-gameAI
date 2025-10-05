package games.planetwars.agents

import games.planetwars.core.GameState
import util.Vec2d

/**
 * A simple greedy agent:
 * - choose the owned planet with the most ships
 * - choose the weakest non-owned planet as target (lowest nShips, ties: lower growthRate then
 * distance)
 * - send half of the ships from source to target
 */
class MyGreedyAgent : PlanetWarsPlayer() {

    override fun getAction(gameState: GameState): Action {
        // find strongest owned planet
        val myPlanets = gameState.planets.filter { it.owner == player }
        if (myPlanets.isEmpty()) return Action.doNothing()

        val source = myPlanets.maxByOrNull { it.nShips } ?: return Action.doNothing()

        // find weakest non-owned planet (neutral or enemy)
        val targets = gameState.planets.filter { it.owner != player }
        if (targets.isEmpty()) return Action.doNothing()

        val target =
                targets.minWithOrNull(
                        compareBy(
                                { it.nShips },
                                { -it.growthRate },
                                { distance(source.position, it.position) }
                        )
                )
                        ?: return Action.doNothing()

        val numShips = (source.nShips / 2.0).coerceAtLeast(1.0)

        return Action(
                playerId = player,
                sourcePlanetId = source.id,
                destinationPlanetId = target.id,
                numShips = numShips
        )
    }

    override fun getAgentType(): String = "MyGreedyAgent"

    private fun distance(a: Vec2d, b: Vec2d): Double {
        val dx = a.x - b.x
        val dy = a.y - b.y
        return kotlin.math.hypot(dx, dy)
    }
}
