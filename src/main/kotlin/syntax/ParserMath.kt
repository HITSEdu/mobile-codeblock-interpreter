package hitsedu.interpreter.syntax

import hitsedu.interpreter.syntax.Tokenizer.tokenizeMath
import hitsedu.interpreter.utils.Operators.MATH
import java.util.*
import kotlin.math.pow

object ParserMath {
    fun parseMathExpression(exp: String, resolve: (String) -> Any): Double {
        val output = mutableListOf<String>()
        val stack = Stack<String>()
        val tokens = tokenizeMath(exp)

        for (token in tokens) {
            when {
                token.isNumber() || token.isVariable() -> output.add(token)
                token in MATH -> Handler.handleOperator(token, stack, output, MATH)
                token == "(" -> stack.push(token)
                token == ")" -> Handler.handleClosingParenthesis(stack, output)
            }
        }

        while (stack.isNotEmpty()) {
            val op = stack.pop()
            if (op == "(" || op == ")") error("Mismatched parentheses")
            output.add(op)
        }

        return evaluateMathRPN(output, resolve)
    }

    private fun evaluateMathRPN(rpn: List<String>, resolve: (String) -> Any): Double {
        val stack = Stack<Double>()

        for (token in rpn) {
            when {
                token.isNumber() -> stack.push(token.toDouble())
                token.isVariable() -> {
                    val value = resolve(token)
                    stack.push(
                        when (value) {
                            is Number -> value.toDouble()
                            is Boolean -> if (value) 1.0 else 0.0
                            else -> error("Cannot convert ${value::class.simpleName} to number")
                        }
                    )
                }

                token in MATH -> {
                    if (stack.size < 2) error("Not enough operands for operator $token")
                    val b = stack.pop()
                    val a = stack.pop()
                    stack.push(applyMathOp(a, b, token))
                }

                else -> error("Unknown token $token in math expression")
            }
        }

        if (stack.size != 1) error("Invalid expression - stack has ${stack.size} elements")
        return stack.pop()
    }

    private fun applyMathOp(a: Double, b: Double, op: String): Double = when (op) {
        "+" -> a + b
        "-" -> a - b
        "*" -> a * b
        "/" -> {
            if (b == 0.0) error("Division by zero")
            a / b
        }

        "%" -> a % b
        "^" -> a.pow(b)
        else -> error("Unknown math operator $op")
    }

    private fun String.isNumber(): Boolean {
        if (this == ".") return false
        return try {
            this.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun String.isVariable(): Boolean = matches(Regex("[a-zA-Z_][a-zA-Z0-9_]*"))
}