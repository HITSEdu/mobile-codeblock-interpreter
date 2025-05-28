package models.operation

import models.Value

data class OperationArray(
    val name: String,
    val size: Int = 0,
    val values: List<Value>,
    override val id: Long = 0,
) : Operation(id)