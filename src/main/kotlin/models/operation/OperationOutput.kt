package models.operation

import models.Value

data class OperationOutput(
    val value: Value,
    override val id: Long = 0,
) : Operation(id)