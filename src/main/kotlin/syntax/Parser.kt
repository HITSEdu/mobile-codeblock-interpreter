package hitsedu.interpreter.syntax

import hitsedu.interpreter.syntax.ParserLogic.parseLogicExpression
import hitsedu.interpreter.syntax.ParserMath.parseMathExpression
import hitsedu.interpreter.syntax.Tokenizer.tokenizeMath
import hitsedu.interpreter.utils.Type

object Parser {
    fun parseAssignment(
        exp: String,
        resolve: (String) -> Any,
        assignVar: (String, Any) -> Unit,
        assignArray: (String, Int, Any) -> Unit
    ): Any {
        val tokens = tokenizeMath(exp)
        val assignIndex = tokens.indexOf("=")

        if (assignIndex > 0) {
            val leftPart = tokens.subList(0, assignIndex).joinToString("")
            val rightPart = tokens.subList(assignIndex + 1, tokens.size).joinToString("")

            when {
                leftPart.isVariable() -> {
                    val value = parseMathExpression(rightPart, resolve)
                    assignVar(leftPart, value)
                    return value
                }

                leftPart.isArrayAccess() -> {
                    val (arrayName, index) = parseArrayAccess(leftPart)
                    val value = parseMathExpression(rightPart, resolve)
                    assignArray(arrayName, index, value)
                    return value
                }
            }
        }

        return parseExpression(exp, resolve)
    }

    fun parseExpression(exp: String, resolve: (String) -> Any): Any {
        return when (Validator.validate(exp)) {
            Type.BOOLEAN -> parseLogicExpression(exp, resolve)
            Type.INT, Type.DOUBLE -> parseMathExpression(exp, resolve)
            Type.STRING -> parseStringExpression(exp, resolve)
            Type.LOGIC -> parseLogicExpression(exp, resolve)
            Type.MATH -> parseMathExpression(exp, resolve)
            Type.VARIABLE -> resolve(exp)
            Type.ARRAY_ACCESS -> resolve(exp)
            else -> error("Unsupported expression type")
        }
    }

    private fun parseArrayAccess(access: String): Pair<String, Int> {
        val regex = Regex("""([a-zA-Z_][a-zA-Z0-9_]*)\[(\d+)]""")
        val match = regex.matchEntire(access) ?: error("Invalid array access syntax")
        return match.groupValues[1] to match.groupValues[2].toInt()
    }

    private fun parseStringExpression(exp: String, resolve: (String) -> Any): String {
        // Удаляем кавычки и разрешаем возможные переменные внутри строки
        val content = exp.removeSurrounding("\"")
        return resolveStringVariables(content, resolve)
    }

    private fun resolveStringVariables(content: String, resolve: (String) -> Any): String {
        val regex = Regex("""\$\{([a-zA-Z_][a-zA-Z0-9_]*)}""")
        return regex.replace(content) { match ->
            val varName = match.groupValues[1]
            resolve(varName).toString()
        }
    }


    private fun String.isVariable() = matches(Regex("[a-zA-Z_][a-zA-Z0-9_]*"))
    private fun String.isArrayAccess() = matches(Regex("""[a-zA-Z_][a-zA-Z0-9_]*\[\d+]"""))
}