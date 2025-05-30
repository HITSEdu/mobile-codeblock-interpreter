package hitsedu.interpreter.processor

import hitsedu.interpreter.models.E
import hitsedu.interpreter.models.Value
import hitsedu.interpreter.models.operation.OperationArray
import hitsedu.interpreter.models.operation.OperationArrayIndex
import hitsedu.interpreter.models.operation.OperationVariable
import hitsedu.interpreter.syntax.ParserMath

fun OperationArrayIndex.process(
    variables: MutableList<OperationVariable>,
    arrays: MutableList<OperationArray>,
): E? {
    val indexResult = runCatching {
        index.value.toIntOrNull() ?: ParserMath.parseMathExpression(index.value) { name ->
            Value(name).process(variables, arrays)?.value?.toIntOrNull() ?: error("Invalid index expression: '$name'")
        }
    }

    val index = indexResult.getOrElse { e ->
        return E(e.message ?: "Failed to parse index", id)
    }

    val valueResult = runCatching {
        ParserMath.parseMathExpression(value.value) { name ->
            Value(name).process(variables, arrays)?.value?.toIntOrNull()
                ?: throw IllegalArgumentException("Cannot resolve variable: '$name'")
        }
    }

    val calculatedValue = valueResult.getOrElse { e ->
        return E(e.message ?: "Failed to parse value", id)
    }

    val array = arrays.find { it.name == name } ?: return E("Array '$name' not found", id)

    if (index !in array.values.indices) {
        return E("Index $index out of bounds for array '$name'", id)
    }
    arrays.replaceAll {
        if (it.name == name) it.copy(values = it.values.toMutableList().apply {
            this[index.toInt()] = Value(calculatedValue.toString())
        }) else it
    }

    return null
}