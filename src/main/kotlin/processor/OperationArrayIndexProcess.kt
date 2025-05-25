package processor

import models.Value
import models.operation.OperationArray
import models.operation.OperationArrayIndex
import models.operation.OperationVariable
import utils.Parser

fun OperationArrayIndex.process(
    variables: MutableList<OperationVariable>,
    arrays: MutableList<OperationArray>
) {
    val idx = index.value.toIntOrNull() ?:
    Parser.parseMathExpression(index.value) { name ->
        Value(name).process(variables, arrays)
    }

    val calculatedValue = Parser.parseMathExpression(value.value) { name ->
        Value(name).process(variables, arrays)
    }

    val array = arrays.find { it.name == name } ?: error("Array $name not found")
    if (idx !in array.values.indices) error("Index $idx out of bounds")

    val newValues = array.values.toMutableList().apply {
        this[idx] = Value(calculatedValue.toString())
    }
    arrays[arrays.indexOf(array)] = array.copy(values = newValues)
}