import kotlin.math.pow

data class AmongUsGame(
    val numPlayers: Int
) {
    companion object {
        const val MIN_PLAYERS = 2
        const val MAX_PLAYERS = 11
    }

    init {
        require(numPlayers in MIN_PLAYERS..MAX_PLAYERS) {
            "Number of players must be between $MIN_PLAYERS and $MAX_PLAYERS!"
        }
    }

    // if the voting is random, everyone including the imposter has equal chance to survive.
    // this means for n players at any particular point of the game, the imposter has
    // (1 - 1/n) = (n-1)/n probability to survive.
    fun pImposterWinsByRandomVoting(): Double {
        return MIN_PLAYERS.toDouble() / numPlayers
    }

    // the probability of the imposter surviving when there are n players remaining is
    // P(crew kicked) / (1 - P(Tie))
    // Multiply the probabilities of the imposter surviving for any number of
    // players remaining in the game (until the imposter can take over)
    // to find the probability of the imposter eventually winning.
    fun pImposterWinsByBeingSmart(): Double {
        var prob = 1.0
        for (i in (MIN_PLAYERS + 1)..numPlayers) {
            prob *= pSurvivingSmart(playersLeft = i)
        }
        return prob
    }

    // Calculates the probability of the imposter surviving with any number
    // of players remaining.
    // Since the voting is random (excluding the imposter's vote), each voting pattern
    // has equal probability and the imposter can decide to:
    // 1. Kick a crew mate
    // 2. Tie to save himself/herself
    // 3. Get kicked (can't tie to save himself/herself)
    // This algorithm counts all possible scenarios
    private fun pSurvivingSmart(playersLeft: Int): Double {
        val numCrew = playersLeft - 1 // players excluding imposter
        val crewPatterns = numCrew.toDouble().pow(numCrew).toLong() // # of ways crew can vote

        var tieCount = 0.0
        var crewCount = 0.0
        for (i in 0 until crewPatterns) { // iterate through all voting patterns to count ties and kicks
            val votes = indexToVotes(i, playersLeft, numCrew) // index -> vote distribution in a list
            val imposterVotes = votes.last() // assume the last element to be votes for the imposter
            val max = votes.take(numCrew).maxOrNull()!! // find max votes excluding imposter
            if (max >= imposterVotes) { // crew mate gets kicked
                crewCount++
            } else if (max + 1 == imposterVotes) { // imposter ties to save himself/herself
                tieCount++
            }
        }

        val pCrew = crewCount / crewPatterns
        val pTie = tieCount / crewPatterns
        return pCrew / (1 - pTie)
    }

    // Converts index of a voting pattern to a list that contains
    // number of votes for each player
    // Example: 4 players (3 crew, 1 imposter)
    // Consider the votes from the crew as a 3-digit base 3 number.
    // Position i with value j means player i voted for player j
    // this function adds the vote counts for each player.
    private fun indexToVotes(index: Long, players: Int, numVotes: Int): IntArray {
        val voteMax = players - 1
        val result = IntArray(players) { 0 } // everyone starts with 0 votes
        var copy = index
        for (i in 0 until numVotes) {
            val vote = (copy % voteMax).toInt() // give a vote to player at index "vote"
            if (vote >= i) {
                result[vote + 1]++
            } else {
                result[vote]++
            }
            copy /= voteMax
        }
        return result
    }
}