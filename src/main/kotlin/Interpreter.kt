import hitsedu.interpreter.models.Project
import models.ConsoleOutput
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

class Interpreter() {

    private val variables = mutableListOf<OperationVariable>()
    private val arrays = mutableListOf<OperationArray>()
    private val console = mutableListOf<ConsoleOutput>()

    val visited = mutableSetOf<Long>() // Множество id скопов

    fun process(scope: Scope) {
        if (!visited.add(scope.id)) return
        for (operation in scope.operations) {
            when (operation) {
                is OperationVariable -> variables.add(operation.process(variables, arrays))
                is OperationArray -> arrays.add(operation.process(variables))
                is OperationArrayIndex -> {
                    val exec = operation.process(variables, arrays)
                    if (exec != null)
                        console.add(exec)
                }
                is OperationOutput -> console.add(operation.process(variables, arrays))
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

    fun getConsole() = console
    fun getArrays() = arrays
    fun getVariables() = variables
}