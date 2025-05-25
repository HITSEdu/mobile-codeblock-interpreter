package models.operation

import models.Scope
import models.Value

data class OperationIf(
    val scope: Scope,
    val value: Value,
    override val id: Long = 0,
) : Operation(id)