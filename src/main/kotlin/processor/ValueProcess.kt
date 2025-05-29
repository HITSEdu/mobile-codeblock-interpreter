package hitsedu.interpreter.processor

import hitsedu.interpreter.models.Value
import hitsedu.interpreter.models.operation.OperationArray
import hitsedu.interpreter.models.operation.OperationVariable
import hitsedu.interpreter.syntax.Parser
import hitsedu.interpreter.syntax.ParserLogic
import hitsedu.interpreter.syntax.Validator
import hitsedu.interpreter.utils.Type

fun Value.process(
    variables: MutableList<OperationVariable>,
    arrays: MutableList<OperationArray>,
): Value? {
    val type = try {
        Validator.validate(value)
    } catch (e: Exception) {
        return null
    }

    fun resolveInt(name: String): Int {
        return variables.find { it.name == name }?.value?.value?.toIntOrNull()
            ?: arrays.firstOrNull { array ->
                name.startsWith(array.name + "[") && name.endsWith("]")
            }?.let { array ->
                val indexStr = name.substring(array.name.length + 1, name.length - 1)
                val index = indexStr.toIntOrNull() ?: return@let null
                array.values.getOrNull(index)?.value?.toIntOrNull()
            } ?: error("Cannot resolve value: $name")
    }

    fun resolveBoolean(name: String): Any {
        when {
            name == "true" -> return true
            name == "false" -> return false
            name.toIntOrNull() != null -> return name.toInt()
            name.toDoubleOrNull() != null -> return name.toDouble()
        }

        variables.find { it.name == name }?.value?.value?.let {
            return when {
                it.equals("true", ignoreCase = true) -> true
                it.equals("false", ignoreCase = true) -> false
                it.toIntOrNull() != null -> it.toInt()
                it.toDoubleOrNull() != null -> it.toDouble()
                else -> it
            }
        }
        if (name.contains("[") && name.endsWith("]")) {
            val arrayName = name.substringBefore("[")
            val indexStr = name.substringAfter("[").substringBefore("]")
            val index = indexStr.toIntOrNull() ?: error("Invalid array index: $indexStr")

            return arrays.find { it.name == arrayName }?.values?.getOrNull(index)?.value?.let { value ->
                when {
                    value.equals("true", ignoreCase = true) -> true
                    value.equals("false", ignoreCase = true) -> false
                    value.toIntOrNull() != null -> value.toInt()
                    value.toDoubleOrNull() != null -> value.toDouble()
                    else -> value
                }
            } ?: error("Array element not found: $name")
        }

        error("Cannot resolve value: $name")
    }

    fun assignVar(name: String, value: Any) {
        val index = variables.indexOfFirst { it.name == name }
        if (index != -1) {
            variables[index] = variables[index].copy(value = Value(value.toString()))
        } else {
            variables.add(OperationVariable(name, Value(value.toString())))
        }
    }

    fun assignArray(name: String, index: Int, value: Any) {
        val array = arrays.find { it.name == name } ?: error("Array $name not found")
        if (index !in array.values.indices) error("Index $index out of bounds for array $name")
        arrays[arrays.indexOf(array)] = array.copy(
            values = array.values.toMutableList().apply { this[index] = Value(value.toString()) }
        )
    }

    return when (type) {
        Type.MATH -> this.copy(
            value = Parser.parseAssignment(
                exp = value,
                resolve = ::resolveInt,
                assignVar = ::assignVar,
                assignArray = ::assignArray
            ).toString(),
            type = Type.INT,
        )

        Type.LOGIC -> this.copy(
            value = ParserLogic.parseLogicExpression(
                exp = value,
                resolve = ::resolveBoolean
            ).toString(),
            type = Type.BOOLEAN,
        )

        Type.VARIABLE -> {
            val resolvedValue = resolveBoolean(value)
            this.copy(
                value = resolvedValue.toString(),
                type = when (resolvedValue) {
                    is Boolean -> Type.BOOLEAN
                    is Int -> Type.INT
                    is Double -> Type.DOUBLE
                    else -> Type.STRING
                }
            )
        }

        Type.ARRAY_ACCESS -> {
            val resolvedValue = resolveBoolean(value)
            this.copy(
                value = resolvedValue.toString(),
                type = when (resolvedValue) {
                    is Boolean -> Type.BOOLEAN
                    is Int -> Type.INT
                    is Double -> Type.DOUBLE
                    else -> Type.STRING
                }
            )
        }

        Type.BOOLEAN, Type.STRING, Type.INT, Type.DOUBLE -> this.copy(type = type)
        Type.UNKNOWN -> null
    }
}