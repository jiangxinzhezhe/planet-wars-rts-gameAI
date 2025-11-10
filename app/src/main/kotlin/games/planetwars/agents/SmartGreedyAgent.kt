package games.planetwars.agents

import games.planetwars.core.GameState
import games.planetwars.core.Player
import kotlin.math.hypot
import util.Vec2d

/**
 * SmartGreedyAgent — Improved version of MyGreedyAgent (Level 1)
 *
 * Features:
 * 1. Dynamic fleet size (sends just enough ships to win)
 * 2. Distance-aware cost function
 * 3. Simple defensive behavior when under threat
 */
class SmartGreedyAgent : PlanetWarsPlayer() {

    override fun getAction(gameState: GameState): Action {
        val myPlanets = gameState.planets.filter { it.owner == player }
        val enemyPlanets =
                gameState.planets.filter { it.owner != player && it.owner != Player.Neutral }

        if (myPlanets.isEmpty()) return Action.doNothing()

        // --- Check if any planet is under threat ---
        val threatened =
                myPlanets.firstOrNull { planet ->
                    gameState.planets.any { other ->
                        other.owner == player.opponent() &&
                                distance(other.position, planet.position) < 60 && // enemy nearby
                                other.nShips > planet.nShips * 0.8
                    }
                }

        if (threatened != null) {
            val reinforcements =
                    myPlanets.filter { it != threatened && it.nShips > 15 }.minByOrNull {
                        distance(it.position, threatened.position)
                    }

            if (reinforcements != null) {
                val shipsToSend = (reinforcements.nShips * 0.4).coerceAtLeast(1.0)
                return Action(
                        playerId = player,
                        sourcePlanetId = reinforcements.id,
                        destinationPlanetId = threatened.id,
                        numShips = shipsToSend
                )
            }
        }

        // --- Offensive phase ---
        val source = myPlanets.maxByOrNull { it.nShips } ?: return Action.doNothing()
        val targets = gameState.planets.filter { it.owner != player }
        if (targets.isEmpty()) return Action.doNothing()

        // Cost function: enemy strength + distance penalty - growth incentive
        val target =
                targets.minByOrNull { t ->
                    val dist = distance(source.position, t.position)
                    val strength = t.nShips
                    val growthPenalty = -t.growthRate * 10
                    strength + dist * 0.25 + growthPenalty
                }
                        ?: return Action.doNothing()

        // --- Compute ships to send ---
        val requiredShips = target.nShips + 5.0 // add buffer for safety
        val numShips = minOf(source.nShips - 1.0, requiredShips)
        if (numShips <= 0) return Action.doNothing()

        return Action(
                playerId = player,
                sourcePlanetId = source.id,
                destinationPlanetId = target.id,
                numShips = numShips
        )
    }

    override fun getAgentType(): String = "SmartGreedyAgent"

    private fun distance(a: Vec2d, b: Vec2d): Double {
        return hypot(a.x - b.x, a.y - b.y)
    }
}
