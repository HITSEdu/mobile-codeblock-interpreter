package hitsedu.interpreter.processor

import hitsedu.interpreter.models.operation.OperationArray
import hitsedu.interpreter.models.operation.OperationIf
import hitsedu.interpreter.models.operation.OperationVariable
import hitsedu.interpreter.syntax.ParserLogic
import hitsedu.interpreter.syntax.ParserMath
import hitsedu.interpreter.utils.Type

fun OperationIf.process(
    variables: MutableList<OperationVariable>,
    arrays: MutableList<OperationArray>,
): Boolean? {
    fun resolve(v: String): Any {
        when {
            v == "true" -> return true
            v == "false" -> return false
            v.toIntOrNull() != null -> return v.toInt()
            v.toDoubleOrNull() != null -> return v.toDouble()
        }

        variables.find { it.name == v }?.value?.let { value ->
            return when (value.type) {
                Type.BOOLEAN -> value.value.toBoolean()
                Type.INT -> value.value.toInt()
                Type.DOUBLE -> value.value.toDouble()
                Type.LOGIC -> ParserLogic.parseLogicExpression(value.value, ::resolve)
                Type.MATH -> ParserMath.parseMathExpression(value.value, ::resolve)
                else -> value.value
            }
        }

        if (v.contains("[") && v.endsWith("]")) {
            val arrayName = v.substringBefore("[")
            val indexExpr = v.substringAfter("[").substringBefore("]")

            val index = when {
                indexExpr.toIntOrNull() != null -> indexExpr.toInt()
                else -> ParserMath.parseMathExpression(indexExpr, ::resolve).toInt()
            }

            return arrays.find { it.name == arrayName }?.values?.getOrNull(index)?.let { value ->
                when (value.type) {
                    Type.BOOLEAN -> value.value.toBoolean()
                    Type.INT -> value.value.toInt()
                    Type.DOUBLE -> value.value.toDouble()
                    Type.LOGIC -> ParserLogic.parseLogicExpression(value.value, ::resolve)
                    Type.MATH -> ParserMath.parseMathExpression(value.value, ::resolve)
                    else -> value.value
                }
            } ?: error("Array element not found: $v")
        }

        error("Cannot resolve value: $v")
    }

    return try {
        ParserLogic.parseLogicExpression(this.value.value, ::resolve)
    } catch (e: Exception) {
        null
    }
}