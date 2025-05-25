package processor

import Interpreter
import models.Scope
import models.Value
import models.operation.OperationArray
import models.operation.OperationFor
import models.operation.OperationVariable
import utils.Parser

fun OperationFor.process(
    variables: MutableList<OperationVariable>,
    arrays: List<OperationArray>,
    interpreter: Interpreter
) {
    var current = variable.process(variables)
    variables.removeIf { it.name == current.name }
    variables.add(current)

    while (Parser.parseLogicExpression(condition.value) { name ->
            Value(name).process(variables, arrays)
        }) {
        interpreter.process(scope)

        val updatedValue = Parser.parseMathExpression(value.value) { name ->
            Value(name).process(variables, arrays)
        }
        variables.replaceAll {
            if (it.name == variable.name) it.copy(value = Value(updatedValue.toString()))
            else it
        }
    }
}