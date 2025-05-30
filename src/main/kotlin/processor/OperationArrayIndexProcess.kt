package hitsedu.interpreter.processor

import hitsedu.interpreter.models.E
import hitsedu.interpreter.models.Value
import hitsedu.interpreter.models.operation.OperationArray
import hitsedu.interpreter.models.operation.OperationArrayIndex
import hitsedu.interpreter.models.operation.OperationVariable
import hitsedu.interpreter.syntax.ParserMath
import hitsedu.interpreter.utils.Type
import kotlin.math.roundToInt

fun OperationArrayIndex.process(
    variables: MutableList<OperationVariable>,
    arrays: MutableList<OperationArray>,
): E? {
    println("[ARRAY INDEX DEBUG] Processing array index operation: ${name}[${index.value}] = ${value.value}")

    val indexInt = try {
        ParserMath.parseMathExpression(index.value) { name ->
            variables.find { it.name == name }?.value?.value?.toDoubleOrNull()
                ?: error("Cannot resolve variable: $name")
        }.toInt().also {
            if (it < 0) error("Array index cannot be negative")
        }
    } catch (e: Exception) {
        return E("Error processing index: ${e.message}", id)
    }

    println("[ARRAY INDEX DEBUG] Processed index: $indexInt")

    val processedValue = try {
        Value(value.value).process(variables, arrays) ?:
        return E("Failed to process value expression: '${value.value}'", id)
    } catch (e: Exception) {
        return E("Error processing value: ${e.message}", id)
    }

    println("[ARRAY INDEX DEBUG] Processed value: ${processedValue.value} (${processedValue.type})")

    val array = arrays.find { it.name == name } ?:
    return E("Array '$name' not found", id)

    if (indexInt !in array.values.indices) {
        return E("Index $indexInt out of bounds for array '$name' (size: ${array.values.size})", id)
    }

    arrays.replaceAll {
        if (it.name == name) {
            val newValues = it.values.toMutableList().apply {
                this[indexInt] = processedValue
            }
            println("[ARRAY INDEX DEBUG] Updating array $name at index $indexInt from ${it.values[indexInt].value} to ${processedValue.value}")
            it.copy(values = newValues)
        } else {
            it
        }
    }

    return null
}