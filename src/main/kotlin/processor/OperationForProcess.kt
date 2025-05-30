package hitsedu.interpreter.processor

import hitsedu.interpreter.models.ConsoleOutput
import hitsedu.interpreter.models.E
import hitsedu.interpreter.models.Value
import hitsedu.interpreter.models.operation.OperationArray
import hitsedu.interpreter.models.operation.OperationArrayIndex
import hitsedu.interpreter.models.operation.OperationFor
import hitsedu.interpreter.models.operation.OperationIf
import hitsedu.interpreter.models.operation.OperationOutput
import hitsedu.interpreter.models.operation.OperationVariable
import hitsedu.interpreter.syntax.Parser
import hitsedu.interpreter.syntax.ParserLogic

fun OperationFor.process(
    variables: MutableList<OperationVariable>,
    arrays: MutableList<OperationArray>,
    console: MutableList<ConsoleOutput>,
): E? {
    val initParts = variable.value.split("=").map { it.trim() }
    if (initParts.size != 2) {
        val errorMsg = "Invalid for loop initialization: ${variable.value}"
        println("[ERROR] $errorMsg")
        return E(errorMsg, id)
    }
    val varName = initParts[0]
    val initValue = initParts[1]

    // Инициализируем переменную цикла
    val initResult = try {
        println("[FOR INIT] Initializing $varName = $initValue")
        Parser.parseAssignment(
            exp = "${varName}=${initValue}",
            resolve = { name ->
                variables.find { it.name == name }?.value?.value?.toIntOrNull()
                    ?: arrays.firstOrNull { array ->
                        name.startsWith(array.name + "[") && name.endsWith("]")
                    }?.let { array ->
                        val indexStr = name.substring(array.name.length + 1, name.length - 1)
                        val index = indexStr.toIntOrNull() ?: return@let null
                        array.values.getOrNull(index)?.value?.toIntOrNull()
                    } ?: error("Cannot resolve value: $name")
            },
            assignVar = { name, value ->
                val index = variables.indexOfFirst { it.name == name }
                if (index != -1) {
                    println("[VAR UPDATE] Updating existing variable $name = $value")
                    variables[index] = variables[index].copy(value = Value(value.toString()))
                } else {
                    println("[VAR CREATE] Creating new variable $name = $value")
                    variables.add(OperationVariable(name, Value(value.toString())))
                }
            },
            assignArray = { name, index, value ->
                val array = arrays.find { it.name == name } ?: error("Array $name not found")
                if (index !in array.values.indices) error("Index $index out of bounds for array $name")
                println("[ARRAY UPDATE] Updating ${array.name}[$index] = $value")
                val newValues = array.values.toMutableList().apply {
                    this[index] = Value(value.toString())
                }
                arrays[arrays.indexOf(array)] = array.copy(values = newValues)
            }
        )
    } catch (e: Exception) {
        return E("Error in for loop initialization: ${e.message}", id)
    }

    var iterationCount = 0
    while (true) {
        iterationCount++
        val conditionResult = try {
            ParserLogic.parseLogicExpression(
                condition.value,
                resolve = { name ->
                    variables.find { it.name == name }?.value?.value?.toIntOrNull()
                        ?: arrays.firstOrNull { array ->
                            name.startsWith(array.name + "[") && name.endsWith("]")
                        }?.let { array ->
                            val indexStr = name.substring(array.name.length + 1, name.length - 1)
                            val index = indexStr.toIntOrNull() ?: return@let null
                            array.values.getOrNull(index)?.value?.toIntOrNull()
                        } ?: error("Cannot resolve value: $name")
                }
            ).also { result ->
                println("[CONDITION RESULT] $result")
            }
        } catch (e: Exception) {
            val errorMsg = "Error in for loop condition: ${e.message}"
            return E(errorMsg, id)
        }

        if (!conditionResult) {
            println("[LOOP EXIT] Condition false, exiting loop after ${iterationCount-1} iterations")
            break
        }

        // Выполняем тело цикла
        println("[BODY EXECUTION] Starting body execution with ${scope.operations.size} operations")
        for (op in scope.operations) {
            println("[OP PROCESSING] Processing operation: ${op::class.simpleName} at line ${op.id}")
            when (op) {
                is OperationVariable -> op.process(variables, arrays)?.let {
                    println("[ERROR] Variable operation failed: ${it.message}")
                    return it
                }
                is OperationArray -> op.process(variables, arrays)?.let {
                    println("[ERROR] Array operation failed: ${it.message}")
                    return it
                }
                is OperationArrayIndex -> op.process(variables, arrays)?.let {
                    println("[ERROR] Array index operation failed: ${it.message}")
                    return it
                }
                is OperationOutput -> {
                    console.add(op.process(variables, arrays))
                }

                is OperationIf -> {
                    println("[IF PROCESSING] Processing if condition at line ${op.id}")
                    val ifResult = try {
                        op.process(variables, arrays)
                    } catch (e: Exception) {
                        return E("Error in if condition: ${e.message}", op.id)
                    }

                    when (ifResult) {
                        true -> {
                            for (ifOp in op.scope.operations) {
                                println("[IF OP PROCESSING] Processing if operation: ${ifOp::class.simpleName} at line ${ifOp.id}")
                                val result = when (ifOp) {
                                    is OperationVariable -> ifOp.process(variables, arrays)
                                    is OperationArray -> ifOp.process(variables, arrays)
                                    is OperationArrayIndex -> ifOp.process(variables, arrays)
                                    is OperationOutput -> {
                                        ifOp.process(variables, arrays)
                                        null
                                    }
                                    is OperationFor -> ifOp.process(variables, arrays, console)
                                    else -> {
                                        return E("Unsupported operation in if body: ${ifOp::class.simpleName}", ifOp.id)
                                    }
                                }
                                if (result != null) {
                                    println("[ERROR] Operation failed: ${result.message}")
                                    return result
                                }
                            }
                        }
                        false -> println("[IF FALSE] Skipping if body")
                        else -> {
                            return E("Invalid if condition result", op.id)
                        }
                    }
                }
                is OperationFor -> {
                    println("[NESTED FOR] Starting nested for loop")
                    op.process(variables, arrays, console)?.let {
                        println("[ERROR] Nested for loop failed: ${it.message}")
                        return it
                    }
                }
                else -> {
                    return E("Unsupported operation in for loop body", op.id)
                }
            }
        }

        try {
            println("[STEP EXECUTION] Executing step: ${varName}=${value.value}")
            Parser.parseAssignment(
                exp = "${varName}=${value.value}",
                resolve = { name ->
                    variables.find { it.name == name }?.value?.value?.toIntOrNull()
                        ?: arrays.firstOrNull { array ->
                            name.startsWith(array.name + "[") && name.endsWith("]")
                        }?.let { array ->
                            val indexStr = name.substring(array.name.length + 1, name.length - 1)
                            val index = indexStr.toIntOrNull() ?: return@let null
                            array.values.getOrNull(index)?.value?.toIntOrNull()
                        } ?: error("Cannot resolve value: $name")
                },
                assignVar = { name, value ->
                    val index = variables.indexOfFirst { it.name == name }
                    if (index != -1) {
                        println("[VAR UPDATE] Updating $name = $value")
                        variables[index] = variables[index].copy(value = Value(value.toString()))
                    } else {
                        println("[VAR CREATE] Creating $name = $value")
                        variables.add(OperationVariable(name, Value(value.toString())))
                    }
                },
                assignArray = { name, index, value ->
                    val array = arrays.find { it.name == name } ?: error("Array $name not found")
                    if (index !in array.values.indices) error("Index $index out of bounds for array $name")
                    println("[ARRAY UPDATE] Updating ${array.name}[$index] = $value")
                    val newValues = array.values.toMutableList().apply {
                        this[index] = Value(value.toString())
                    }
                    arrays[arrays.indexOf(array)] = array.copy(values = newValues)
                }
            )
        } catch (e: Exception) {
            return E("Error in for loop step: ${e.message}", id)
        }
    }

    println("[FOR LOOP] Finished processing for loop at line $id")
    return null
}