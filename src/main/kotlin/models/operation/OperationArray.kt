package hitsedu.interpreter.models.operation

import hitsedu.interpreter.models.Value
import models.operation.Operation

data class OperationArray(
    val name: String,
    val size: Int,
    val values: List<Value>,
    override val id: Long = 0,
) : Operation(id)