package models.operation

import models.Value

data class OperationArray(
    val name: String,
    val size: Int,
    val values: List<Value>,
    override val id: Long = 0,
) : Operation(id)