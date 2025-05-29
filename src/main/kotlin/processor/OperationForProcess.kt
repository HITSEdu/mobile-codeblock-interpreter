package hitsedu.interpreter.processor

import hitsedu.interpreter.models.ConsoleOutput
import hitsedu.interpreter.models.E
import hitsedu.interpreter.models.Value
import hitsedu.interpreter.models.operation.*
import hitsedu.interpreter.syntax.Parser
import hitsedu.interpreter.syntax.ParserLogic

fun OperationFor.process(
    variables: MutableList<OperationVariable>,
    arrays: MutableList<OperationArray>
): E? {
    // Логирование начала обработки цикла
    println("[FOR LOOP] Start processing for loop at line $id")
    println("[FOR LOOP] Initialization: ${variable.value}")
    println("[FOR LOOP] Condition: ${condition.value}")
    println("[FOR LOOP] Step: ${value.value}")
    println("[VARS BEFORE INIT] ${variables.joinToString { "${it.name}=${it.value.value}" }}")
    println("[ARRAYS BEFORE INIT] ${arrays.joinToString { "${it.name}=[${it.values.joinToString()}]" }}")

    // Парсим инициализацию переменной цикла
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
        val errorMsg = "Error in for loop initialization: ${e.message}"
        println("[ERROR] $errorMsg")
        return E(errorMsg, id)
    }

    println("[VARS AFTER INIT] ${variables.joinToString { "${it.name}=${it.value.value}" }}")
    println("[ARRAYS AFTER INIT] ${arrays.joinToString { "${it.name}=[${it.values.joinToString()}]" }}")

    var iterationCount = 0
    // Основной цикл
    while (true) {
        iterationCount++
        println("\n[ITERATION $iterationCount] Starting iteration")
        println("[CURRENT VARS] ${variables.joinToString { "${it.name}=${it.value.value}" }}")

        // Проверяем условие продолжения цикла
        val conditionResult = try {
            println("[CONDITION CHECK] Evaluating: ${condition.value}")
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
            println("[ERROR] $errorMsg")
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
                    println("[OUTPUT] Processing output operation")
                    op.process(variables, arrays)
                }

                is OperationIf -> {
                    println("[IF PROCESSING] Processing if condition at line ${op.id}")
                    val ifResult = try {
                        op.process(variables, arrays)
                    } catch (e: Exception) {
                        val errorMsg = "Error in if condition: ${e.message}"
                        println("[ERROR] $errorMsg")
                        return E(errorMsg, op.id)
                    }

                    when (ifResult) {
                        true -> {
                            println("[IF TRUE] Executing if body with ${op.scope.operations.size} operations")
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
                                    is OperationFor -> ifOp.process(variables.toMutableList(), arrays.toMutableList())
                                    else -> {
                                        val errorMsg = "Unsupported operation in if body: ${ifOp::class.simpleName}"
                                        println("[ERROR] $errorMsg")
                                        return E(errorMsg, ifOp.id)
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
                            val errorMsg = "Invalid if condition result"
                            println("[ERROR] $errorMsg")
                            return E(errorMsg, op.id)
                        }
                    }
                }
                is OperationFor -> {
                    println("[NESTED FOR] Starting nested for loop")
                    op.process(variables.toMutableList(), arrays.toMutableList())?.let {
                        println("[ERROR] Nested for loop failed: ${it.message}")
                        return it
                    }
                }
                else -> {
                    val errorMsg = "Unsupported operation in for loop body"
                    println("[ERROR] $errorMsg")
                    return E(errorMsg, op.id)
                }
            }
        }

        // Выполняем шаг цикла
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
            println("[VARS AFTER STEP] ${variables.joinToString { "${it.name}=${it.value.value}" }}")
        } catch (e: Exception) {
            val errorMsg = "Error in for loop step: ${e.message}"
            println("[ERROR] $errorMsg")
            return E(errorMsg, id)
        }
    }

    println("[FOR LOOP] Finished processing for loop at line $id")
    return null
}