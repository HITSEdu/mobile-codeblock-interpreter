package processor

import models.ConsoleOutput
import models.Value
import models.E
import models.operation.OperationArray
import models.operation.OperationArrayIndex
import models.operation.OperationVariable
import utils.Parser

fun OperationArrayIndex.process(
    variables: MutableList<OperationVariable>,
    arrays: MutableList<OperationArray>
): ConsoleOutput? {
    val idx = index.value.toIntOrNull() ?:
    Parser.parseMathExpression(index.value) { name ->
        Value(name).process(
            variables = variables,
            arrays = arrays
        )
    }

    val calculatedValue = Parser.parseMathExpression(value.value) { name ->
        Value(name).process(variables, arrays)
    }

    val array = arrays.find { it.name == name } ?: return ConsoleOutput("" ,E("Array $name not found", id))
    if (idx !in array.values.indices) return ConsoleOutput("", E("Index $idx out of bounds", id))

    val newValues = array.values.toMutableList().apply {
        this[idx] = Value(calculatedValue.toString())
    }
    arrays[arrays.indexOf(array)] = array.copy(values = newValues)
    return null
}