package hitsedu.interpreter.models.operation

import hitsedu.interpreter.models.Scope
import models.operation.Operation

data class OperationElse(
    val scope: Scope,
    override val id: Long = 0,
) : Operation(id)