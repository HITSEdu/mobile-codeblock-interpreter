import hitsedu.interpreter.models.Scope
import hitsedu.interpreter.models.operation.OperationArray
import hitsedu.interpreter.models.operation.OperationArrayIndex
import hitsedu.interpreter.models.operation.OperationElse
import hitsedu.interpreter.models.operation.OperationFor
import hitsedu.interpreter.models.operation.OperationIf
import hitsedu.interpreter.models.operation.OperationOutput
import hitsedu.interpreter.models.operation.OperationVariable
import processor.process
import utils.MockData

class Interpreter {
    val program = MockData.nestedProgram

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