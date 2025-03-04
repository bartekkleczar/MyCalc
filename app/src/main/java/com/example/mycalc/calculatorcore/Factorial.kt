class Factorial: Modulator {
    override fun calculate(previous: Int): Int {
        var factorial = 1
        for (i in 1..previous) {
            factorial *= i
        }
        return factorial
    }
}