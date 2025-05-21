package processor

import ConsoleOutput
import hitsedu.interpreter.models.operation.OperationIf
import hitsedu.interpreter.models.operation.OperationOutput

fun OperationOutput.process(): ConsoleOutput = ConsoleOutput(
    output = value.value
)