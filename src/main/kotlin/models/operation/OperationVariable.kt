package hitsedu.interpreter.models.operation

import hitsedu.interpreter.models.Value
import models.operation.Operation

data class OperationVariable(
    val name: String,
    val value: Value,
    override val id: Long = 0,
) : Operation(id)