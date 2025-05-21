package hitsedu.interpreter.models.operation

import hitsedu.interpreter.models.Value
import models.operation.Operation

data class OperationArrayIndex(
    val name: String,
    val index: Value,
    val value: Value,
    override val id: Long = 0,
) : Operation(id)