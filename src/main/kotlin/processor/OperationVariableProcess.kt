package hitsedu.interpreter.processor

import hitsedu.interpreter.models.E
import hitsedu.interpreter.models.Value
import hitsedu.interpreter.models.operation.OperationArray
import hitsedu.interpreter.models.operation.OperationVariable
import hitsedu.interpreter.syntax.ParserLogic
import hitsedu.interpreter.syntax.ParserMath
import hitsedu.interpreter.utils.Type

fun OperationVariable.process(
    variables: MutableList<OperationVariable>,
    arrays: MutableList<OperationArray>
): E? {
    return try {
        val processed = value.process(variables, arrays) ?: value
        variables.find { it.name == name }?.let {
            variables[variables.indexOf(it)] = copy(value = processed)
        } ?: variables.add(copy(value = processed))
        null
    } catch (e: Exception) {
        E(message = e.message ?: "Unknown error", blockId = id)
    }
}