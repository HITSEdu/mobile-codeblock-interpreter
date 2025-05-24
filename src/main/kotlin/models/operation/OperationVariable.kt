package models.operation

import models.Value

data class OperationVariable(
    val name: String,
    val value: Value,
    override val id: Long = 0,
) : Operation(id)