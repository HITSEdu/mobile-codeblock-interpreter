package hitsedu.interpreter.models.operation

import hitsedu.interpreter.models.Scope
import hitsedu.interpreter.models.Value
import models.operation.Operation

data class OperationFor(
    val scope: Scope,
    val variable: OperationVariable,
    val condition: Value,
    val value: Value,
    override val id: Long = 0,
) : Operation(id)