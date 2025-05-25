import models.Scope
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
    val program = MockData.forLoopTest1

    val variables = mutableListOf<OperationVariable>()
    val arrays = mutableListOf<OperationArray>()
    val console = mutableListOf<ConsoleOutput>()

    val visited = mutableSetOf<Long>() // Множество id скопов

    fun process(scope: Scope = program) {
        if (!visited.add(scope.id)) return
        for (operation in scope.operations) {
            when (operation) {
                is OperationVariable -> variables.add(operation.process(variables, arrays))
                is OperationArray -> arrays.add(operation.process(variables))
                is OperationArrayIndex -> operation.process(variables, arrays)
                is OperationOutput -> console.add(operation.process())
                is OperationIf -> {
                    if (operation.process(variables, arrays)) {
                        process(operation.scope)
                    }
                }
                is OperationElse -> {
                    process(operation.scope)
                }
                is OperationFor -> {
                    operation.process(variables, arrays, this)
                }
            }
        }
    }

    fun copyForScopeExecution(arrays: MutableList<OperationArray>): Interpreter {
        val copy = Interpreter()
        copy.variables.addAll(this.variables)
        copy.arrays.addAll(arrays)
        return copy
    }
}