package models.operation

import models.Value

data class OperationArrayIndex(
    val name: String,
    val index: Value,
    val value: Value,
    override val id: Long = 0,
) : Operation(id)