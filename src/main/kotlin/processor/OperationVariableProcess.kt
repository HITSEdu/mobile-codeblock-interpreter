package hitsedu.interpreter.processor

import hitsedu.interpreter.models.E
import hitsedu.interpreter.models.Value
import hitsedu.interpreter.models.operation.OperationArray
import hitsedu.interpreter.models.operation.OperationVariable
import hitsedu.interpreter.syntax.Parser
import hitsedu.interpreter.syntax.ParserLogic
import hitsedu.interpreter.utils.Type

fun OperationVariable.process(
    variables: MutableList<OperationVariable>,
    arrays: MutableList<OperationArray>
): E? {
    val processedValue = this.copy(value = value.process(variables, arrays) ?: value)
    variables.add(processedValue)
    println(processedValue)

    fun resolve(name: String): Any {
        when (name) {
            "true" -> return true
            "false" -> return false
        }

        name.toIntOrNull()?.let { return it }
        name.toDoubleOrNull()?.let { return it }

        variables.find { it.name == name }?.value?.let { value ->
            return when (value.type) {
                Type.BOOLEAN -> value.value.toBoolean()
                Type.INT -> value.value.toInt()
                Type.DOUBLE -> value.value.toDouble()
                Type.LOGIC -> ParserLogic.parseLogicExpression(value.value, ::resolve)
                else -> value.value
            }
        }

        if (name.contains("[") && name.endsWith("]")) {
            val arrayName = name.substringBefore("[")
            val indexStr = name.substringAfter("[").substringBefore("]")
            val index = indexStr.toIntOrNull() ?: error("Invalid array index: $indexStr")

            return arrays.find { it.name == arrayName }?.values?.getOrNull(index)?.let { value ->
                when (value.type) {
                    Type.BOOLEAN -> value.value.toBoolean()
                    Type.INT -> value.value.toInt()
                    Type.DOUBLE -> value.value.toDouble()
                    Type.LOGIC -> ParserLogic.parseLogicExpression(value.value, ::resolve)
                    else -> value.value
                }
            } ?: error("Array element not found: $name")
        }

        error("Cannot resolve value: $name")
    }

    fun assignVar(name: String, value: Any) {
        val type = when (value) {
            is Boolean -> Type.BOOLEAN
            is Int -> Type.INT
            is Double -> Type.DOUBLE
            else -> Type.STRING
        }

        val newValue = Value(value.toString(), type = type)

        val index = variables.indexOfFirst { it.name == name }
        if (index != -1) {
            variables[index] = variables[index].copy(value = newValue)
        } else {
            variables.add(OperationVariable(name, newValue))
        }
    }

    fun assignArray(name: String, index: Int, value: Any) {
        val array = arrays.find { it.name == name } ?: error("Array $name not found")
        if (index !in array.values.indices) error("Index $index out of bounds for array $name")

        val type = when (value) {
            is Boolean -> Type.BOOLEAN
            is Int -> Type.INT
            is Double -> Type.DOUBLE
            else -> Type.STRING
        }

        val newValue = Value(value.toString(), type = type)

        arrays[arrays.indexOf(array)] = array.copy(
            values = array.values.toMutableList().apply { this[index] = newValue }
        )
    }

    return try {
        val result = Parser.parseAssignment(
            exp = value.value,
            resolve = ::resolve,
            assignVar = ::assignVar,
            assignArray = ::assignArray
        )
        variables.add(copy(value = Value(result.toString(), type = fromValue(result))))
        null
    } catch (e: Exception) {
        E(message = e.message ?: "Unknown error", blockId = id)
    }
}

fun fromValue(value: Any): Type {
    return when (value) {
        is Boolean -> Type.BOOLEAN
        is Int -> Type.INT
        is Double -> Type.DOUBLE
        else -> Type.STRING
    }
}