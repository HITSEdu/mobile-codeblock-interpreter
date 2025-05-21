package processor

import hitsedu.interpreter.models.operation.OperationVariable

fun OperationVariable.process(variables: List<OperationVariable>): OperationVariable {

    return this.copy(value = variables.find { it.name == value.value }?.value ?: value)
}