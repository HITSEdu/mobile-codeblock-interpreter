package processor

import models.operation.OperationArray
import models.operation.OperationVariable

fun OperationArray.process(variables: List<OperationVariable>): OperationArray {
    val values = values.map { value ->
        val variable = variables.find { it.name == value.value }
        value.copy(value = variable?.value?.value ?: value.value)
    }
    return this.copy(values = values)
}