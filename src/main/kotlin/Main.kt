fun main() {
    println("%1$-3s| %2$-10s| %3$-10s".format("n", "Random", "Smart"))
    for (i in AmongUsGame.MIN_PLAYERS .. AmongUsGame.MAX_PLAYERS) {
        val game = AmongUsGame(i)
        val pRandom = game.pImposterWinsByRandomVoting()
        val pSmart = game.pImposterWinsByBeingSmart()
        println("%1$-3d| %2$-10.5f| %3$-10.5f".format(i, pRandom, pSmart))
    }
}