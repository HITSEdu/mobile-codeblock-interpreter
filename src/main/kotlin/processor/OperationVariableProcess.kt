package processor

import models.operation.OperationVariable

fun OperationVariable.process(variables: List<OperationVariable>): OperationVariable {
    return this.copy(value = variables.find { it.name == value.value }?.value ?: value)
}