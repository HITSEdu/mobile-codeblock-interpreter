package processor

import ConsoleOutput
import models.operation.OperationOutput

fun OperationOutput.process(): ConsoleOutput = ConsoleOutput(
    output = value.value
)