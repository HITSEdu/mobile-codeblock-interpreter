import hitsedu.interpreter.models.Scope
import models.operation.OperationArray
import models.operation.OperationArrayIndex
import models.operation.OperationElse
import models.operation.OperationFor
import models.operation.OperationIf
import models.operation.OperationOutput
import models.operation.OperationVariable
import processor.process
import utils.MockData

class Interpreter {
    val program = MockData.arrayTest

    val variables = mutableListOf<OperationVariable>()
    val arrays = mutableListOf<OperationArray>()
    val console = mutableListOf<ConsoleOutput>()

    val visited = mutableSetOf<Long>() // Множество id скопов

    fun process(scope: Scope = program) {
        if (!visited.add(scope.id)) return
        if (!visited.contains(scope.id)) visited.add(scope.id)
        for (operation in scope.operations) {
            when (operation) {
                is OperationVariable -> variables.add(operation.process(variables))
                is OperationArray -> arrays.add(operation.process(variables))
                is OperationArrayIndex -> operation.process(arrays)
                is OperationOutput -> console.add(operation.process())
                is OperationIf -> {
                    operation.process()
                    process(operation.scope)
                }
                is OperationElse -> {
                    operation.process()
                    process(operation.scope)
                }
                is OperationFor -> {
                    operation.process()
                    process(operation.scope)
                }
            }
        }
    }
}