package models.operation

import models.Scope

data class OperationElse(
    val scope: Scope,
    override val id: Long = 0,
) : Operation(id)