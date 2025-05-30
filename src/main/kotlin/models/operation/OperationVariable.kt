package hitsedu.interpreter.models.operation

import hitsedu.interpreter.models.Value

data class OperationVariable(
    val name: String,
    var value: Value,
    override val id: Long = 0,
) : Operation(id)