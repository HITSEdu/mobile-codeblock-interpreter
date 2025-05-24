package processor

import models.Value
import models.operation.OperationArray
import models.operation.OperationIf
import models.operation.OperationVariable
import utils.Parser

fun OperationIf.process(
    variables: List<OperationVariable>,
    arrays: List<OperationArray>
): Boolean {
    return Parser.parseLogicExpression(this.value.value) { name ->
        val tempVal = Value(name)
        tempVal.process(variables, arrays)
    }
}