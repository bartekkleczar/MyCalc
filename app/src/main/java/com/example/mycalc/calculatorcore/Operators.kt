import kotlin.math.pow

class Plus : Operator {
    override fun calculate(previous: Float, actual: Float): Float {
        return previous + actual
    }
}

class Minus : Operator {
    override fun calculate(previous: Float, actual: Float): Float {
        return previous - actual
    }
}

class Times : Operator {
    override fun calculate(previous: Float, actual: Float): Float {
        return previous * actual
    }
}

class Divide : Operator {
    override fun calculate(previous: Float, actual: Float): Float {
        return previous / actual
    }
}

class Percent : Operator {
    override fun calculate(previous: Float, actual: Float): Float {
        return (previous*actual) / 100
    }
}

class Power : Operator {
    override fun calculate(previous: Float, actual: Float): Float {
        return previous.pow(actual)
    }
}

class Root : Operator {
    override fun calculate(previous: Float, actual: Float): Float {
        return actual.pow(1/previous)
    }
}