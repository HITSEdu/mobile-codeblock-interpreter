package utils

import java.util.Stack

object Parser {
    private val operators = mapOf(
        "+" to 1,
        "-" to 1,
        "*" to 2,
        "/" to 2
    )

    fun parseMathExpression(exp: String, resolve: (String) -> Int): Int {
        val output = mutableListOf<String>()
        val stack = Stack<String>()

        val tokens = tokenize(exp)

        for (token in tokens) {
            when {
                token.isNumber() || token.isVariable() -> output.add(token)
                token in operators -> {
                    while (stack.isNotEmpty() &&
                        stack.peek() in operators &&
                        operators[token]!! <= operators[stack.peek()]!!) {
                        output.add(stack.pop())
                    }
                    stack.push(token)
                }
                token == "(" -> stack.push(token)
                token == ")" -> {
                    while (stack.isNotEmpty() && stack.peek() != "(") {
                        output.add(stack.pop())
                    }
                    if (stack.isNotEmpty() && stack.peek() == "(") {
                        stack.pop()
                    } else {
                        error("Mismatched parentheses")
                    }
                }
            }
        }

        while (stack.isNotEmpty()) {
            val op = stack.pop()
            if (op == "(" || op == ")") error("Mismatched parentheses")
            output.add(op)
        }

        return evaluateRPN(output, resolve)
    }

    private fun evaluateRPN(rpn: List<String>, resolve: (String) -> Int): Int {
        val stack = Stack<Int>()
        for (token in rpn) {
            when {
                token.isNumber() -> stack.push(token.toInt())
                token.isVariable() -> stack.push(resolve(token))
                token in operators -> {
                    val b = stack.pop()
                    val a = stack.pop()
                    val result = when (token) {
                        "+" -> a + b
                        "-" -> a - b
                        "*" -> a * b
                        "/" -> a / b
                        else -> error("Unknown operator")
                    }
                    stack.push(result)
                }
            }
        }
        return stack.pop()
    }

    private fun String.isNumber() = this.toIntOrNull() != null
    private fun String.isVariable() = this.matches(Regex("[a-zA-Z_][a-zA-Z0-9_]*"))

    private fun tokenize(exp: String): List<String> {
        val result = mutableListOf<String>()
        val buffer = StringBuilder()
        for (c in exp) {
            when {
                c.isWhitespace() -> continue
                c.isDigit() || c.isLetter() || c == '_' -> buffer.append(c)
                else -> {
                    if (buffer.isNotEmpty()) {
                        result.add(buffer.toString())
                        buffer.clear()
                    }
                    result.add(c.toString())
                }
            }
        }
        if (buffer.isNotEmpty()) result.add(buffer.toString())
        return result
    }


    private val logicPrecedence = mapOf(
        "||" to 1,
        "&&" to 2,
        "==" to 3,
        "!=" to 3,
        "<" to 3,
        ">" to 3,
        "<=" to 3,
        ">=" to 3,
    )

    fun parseLogicExpression(exp: String, resolve: (String) -> Int): Boolean {
        val output = mutableListOf<String>()
        val stack = Stack<String>()
        val tokens = tokenizeLogic(exp)

        for (token in tokens) {
            when {
                token.isOperand() -> output.add(token)
                token in logicPrecedence -> {
                    while (
                        stack.isNotEmpty() &&
                        stack.peek() in logicPrecedence &&
                        logicPrecedence[token]!! <= logicPrecedence[stack.peek()]!!
                    ) {
                        output.add(stack.pop())
                    }
                    stack.push(token)
                }
                token == "(" -> stack.push(token)
                token == ")" -> {
                    while (stack.isNotEmpty() && stack.peek() != "(") {
                        output.add(stack.pop())
                    }
                    if (stack.isNotEmpty() && stack.peek() == "(") {
                        stack.pop()
                    } else {
                        error("Mismatched parentheses in logic expression")
                    }
                }
            }
        }

        while (stack.isNotEmpty()) {
            val op = stack.pop()
            if (op == "(" || op == ")") error("Mismatched parentheses")
            output.add(op)
        }

        return evaluateLogicRPN(output, resolve)
    }

    private fun evaluateLogicRPN(rpn: List<String>, resolve: (String) -> Int): Boolean {
        val stack = Stack<Any>()
        for (token in rpn) {
            when {
                token.isLogicOperator() -> {
                    val b = stack.pop()
                    val a = stack.pop()
                    val result = applyLogicOp(a, b, token, resolve)
                    stack.push(result)
                }
                else -> {
                    stack.push(token)
                }
            }
        }
        return stack.pop() as Boolean
    }

    private fun applyLogicOp(a: Any, b: Any, op: String, resolve: (String) -> Int): Boolean {
        fun evalOperand(x: Any): Int {
            val str = x as String
            return Parser.parseMathExpression(str, resolve)
        }

        return when (op) {
            "==" -> evalOperand(a) == evalOperand(b)
            "!=" -> evalOperand(a) != evalOperand(b)
            "<" -> evalOperand(a) < evalOperand(b)
            ">" -> evalOperand(a) > evalOperand(b)
            "<=" -> evalOperand(a) <= evalOperand(b)
            ">=" -> evalOperand(a) >= evalOperand(b)
            "&&" -> (a as Boolean) && (b as Boolean)
            "||" -> (a as Boolean) || (b as Boolean)
            else -> error("Unsupported logic operator $op")
        }
    }

    private fun tokenizeLogic(exp: String): List<String> {
        val tokens = mutableListOf<String>()
        val buffer = StringBuilder()
        var i = 0
        while (i < exp.length) {
            val c = exp[i]
            if (c.isWhitespace()) {
                i++
                continue
            }

            if (i + 1 < exp.length) {
                val twoChar = exp.substring(i, i + 2)
                if (twoChar in logicPrecedence) {
                    if (buffer.isNotEmpty()) {
                        tokens.add(buffer.toString())
                        buffer.clear()
                    }
                    tokens.add(twoChar)
                    i += 2
                    continue
                }
            }

            if ("()<>!=&|".contains(c)) {
                if (buffer.isNotEmpty()) {
                    tokens.add(buffer.toString())
                    buffer.clear()
                }
                tokens.add(c.toString())
            } else {
                buffer.append(c)
            }
            i++
        }

        if (buffer.isNotEmpty()) tokens.add(buffer.toString())

        return tokens
    }

    private fun String.isOperand() = this.matches(Regex("[a-zA-Z0-9_()+\\-*/]+"))
    private fun String.isLogicOperator() = this in logicPrecedence
}