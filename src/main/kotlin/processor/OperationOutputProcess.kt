package hitsedu.interpreter.processor

import hitsedu.interpreter.models.ConsoleOutput
import hitsedu.interpreter.models.E
import hitsedu.interpreter.models.Value
import hitsedu.interpreter.models.operation.OperationArray
import hitsedu.interpreter.models.operation.OperationOutput
import hitsedu.interpreter.models.operation.OperationVariable
import hitsedu.interpreter.syntax.ParserLogic
import hitsedu.interpreter.syntax.ParserMath
import hitsedu.interpreter.utils.Operators

fun OperationOutput.process(
    variables: MutableList<OperationVariable>,
    arrays: MutableList<OperationArray>
): ConsoleOutput {
    return try {
        ConsoleOutput(value.process(variables, arrays)?.value.toString())
    } catch (e: Exception) {
        ConsoleOutput("", E("${e.message}", id))
    }
}
