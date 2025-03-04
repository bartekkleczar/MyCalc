package com.example.mycalc.calculatorcore
import Bracket
import Divide
import Factorial
import Minus
import Modulator
import Operator
import Percent
import Plus
import Power
import Root
import Times
import format
import kotlin.Exception

class Calculator {
    fun calculate(input: List<Any>): String {
        return try{
            val formattedInput = formatBracketMultiplying(input)
            val negativesHandled = handleNegatives(formattedInput)
            val factorialResult = calculateFactorials(negativesHandled).toMutableList()
            val bracketsChecked = checkBrackets(factorialResult)
            val powersRootsCalculated = calculatePowersRoots(bracketsChecked)
            val multiplyingCalculated = calculateMultiplying(powersRootsCalculated)
            val result = calculateAddition(multiplyingCalculated)
            format(result[0].toString().toFloat())
        } catch (e: Exception) {
            println(e)
            "Input error"
        }
    }

    fun parseExpression(expression: String): List<Any> {
        val (tokens, _) = parseHelper(expression, startIndex = 0, depth = 0)
        return tokens
    }

    private fun parseHelper(
        expression: String,
        startIndex: Int,
        depth: Int
    ): Pair<MutableList<Any>, Int> {
        val tokens = mutableListOf<Any>()
        var i = startIndex

        while (i < expression.length) {
            when (val c = expression[i]) {
                in '0'..'9', '.' -> {
                    val number = buildString {
                        var decimalCount = 0
                        while (i < expression.length && (expression[i].isDigit() || expression[i] == '.')) {
                            if (expression[i] == '.') {
                                if (decimalCount++ > 0) throw IllegalArgumentException("Invalid number format")
                            }
                            append(expression[i++])
                        }
                    }
                    tokens.add(number)
                }
                '+' -> { tokens.add(Plus()); i++ }
                '-' -> { tokens.add(Minus()); i++ }
                '*' -> { tokens.add(Times()); i++ }
                '/' -> { tokens.add(Divide()); i++ }
                ':' -> { tokens.add(Divide()); i++ }
                '^' -> { tokens.add(Power()); i++ }
                '√' -> { tokens.add(Root()); i++ }
                '!' -> { tokens.add(Factorial()); i++ }
                '(' -> {
                    i++
                    val (innerTokens, newIndex) = parseHelper(expression, i, depth + 1)

                    if (newIndex > expression.length || expression[newIndex - 1] != ')') {
                        throw IllegalArgumentException("Missing closing bracket")
                    }
                    tokens.add(Bracket(innerTokens))
                    i = newIndex
                }
                ')' -> {
                    if (depth == 0) throw IllegalArgumentException("Unexpected closing bracket")
                    i++
                    return Pair(tokens, i)
                }
                else -> i++
            }
            val maxIndex = tokens.size-1
            if(maxIndex >= 1 && (tokens[maxIndex-1] is Operator && tokens[maxIndex] is Operator)){
                throw IllegalArgumentException("Two operators in a row")
            }
            if(maxIndex >= 1 && (tokens[maxIndex-1] is Modulator && tokens[maxIndex] is Modulator)){
                throw IllegalArgumentException("Two modulators in a row")
            }
        }
        if (depth > 0) throw IllegalArgumentException("Unclosed bracket")
        return Pair(tokens, i)
    }

    private fun isContainingBrackets(input: List<Any>): Boolean {
        for (i in input) {
            if (i is Bracket) return true
        }
        return false
    }
    
    private fun handleNegatives(input: List<Any>): List<Any>{
        val handled = input.toMutableList()
        var previous: Any = 0
        var previous2: Any = 0
        var index = 0

        for(i in input){
            try {
                val previous2f = previous2.toString().toFloat() //whether converts to float or throws exception, meaning previous2 isn't number
                if(previous is Minus && index != 0 && previous2f == 0f){
                    handled[index] = -1 * i.toString().toFloat()
                    handled.removeAt(index-1)
                }
            } catch (_: Exception){}
            previous2 = previous
            previous = i
            index += 1
        }
        println("---handleNegatives---")
        println(handled)
        return handled
    }

    private fun checkBrackets(input: List<Any>): List<Any> {
        if (!isContainingBrackets(input)) return input
        val inputReturn = input.toMutableList()

        var index = 0
        for(i in input){
            if(i is Bracket){
                val bracketContent = Calculator().calculate(i.content)
                inputReturn[index] = bracketContent
            }
            index += 1
        }
        println("---checkBrackets---")
        println(inputReturn)
        return inputReturn
    }

    private fun formatBracketMultiplying(input: List<Any>): MutableList<Any> {
        val formatted = input.toMutableList()
        var previous: Any = 0
        var index = 0

        for(i in input){
            try {
                if(((previous is Int || previous is Float || previous is Bracket) && i is Bracket) && index != 0){
                    formatted.add(index, Times())
                }
            } catch (_: Exception){}
            previous = i
            index += 1
        }
        println("---formatBracketMultiplying---")
        println(formatted)
        return formatted
    }

    private fun calculateFactorials(input: List<Any>): MutableList<Any> {
        val calculated = input.toMutableList()
        for (i in input) {
            val location = calculated.indexOf(i)
            try {
                val next = calculated[location + 1]
                if (i.toString().toInt() is Int && next is Factorial) {
                    calculated[location] = next.calculate(i.toString().toInt())
                    calculated.removeAt(location + 1)
                }
            } catch (e: Exception) {
                break
            }
        }
        println("---calculateFactorials---")
        println(calculated)
        return calculated
    }

    private fun calculatePowersRoots(input: List<Any>): MutableList<Any> {
        val calculated = input.toMutableList()
        for (i in input) {
            if (i is Operator && (i is Power || i is Root)) {
                val location = calculated.indexOf(i)
                calculated[location] =
                    i.calculate(calculated[location - 1].toString().toFloat(), calculated[location + 1].toString().toFloat())
                calculated.removeAt(location + 1)
                calculated.removeAt(location - 1)
            }
        }
        println("---calculatePowersRoots---")
        println(calculated)
        return calculated
    }

    private fun calculateMultiplying(input: List<Any>): MutableList<Any> {
        val calculated = input.toMutableList()
        for (i in input) {
            if (i is Operator && (i is Times || i is Divide || i is Percent)) {
                val location = calculated.indexOf(i)
                calculated[location] =
                    i.calculate(calculated[location - 1].toString().toFloat(), calculated[location + 1].toString().toFloat())
                calculated.removeAt(location + 1)
                calculated.removeAt(location - 1)
            }
        }
        println("---calculateMultiplying---")
        println(calculated)
        return calculated
    }

    private fun calculateAddition(input: List<Any>): MutableList<Any> {
        val calculated = input.toMutableList()
        for (i in input) {
            if (i is Operator && (i is Plus || i is Minus)) {
                val location = calculated.indexOf(i)
                calculated[location] =
                    i.calculate(calculated[location - 1].toString().toFloat(), calculated[location + 1].toString().toFloat())
                calculated.removeAt(location + 1)
                calculated.removeAt(location - 1)
            }
        }
        println("---calculateAddition---")
        println(calculated)
        return calculated
    }
}