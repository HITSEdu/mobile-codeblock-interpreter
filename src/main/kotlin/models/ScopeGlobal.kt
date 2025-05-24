package models

import models.operation.Operation
import models.operation.OperationArray
import models.operation.OperationVariable

data class ScopeGlobal(
    override val operations: List<Operation>,
    override val id: Long,
    val variableUIOS: List<OperationVariable>,
    val arrayUIOS: List<OperationArray>,
) : Scope(operations, id)