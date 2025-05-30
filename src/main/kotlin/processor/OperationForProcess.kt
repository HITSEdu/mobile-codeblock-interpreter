package hitsedu.interpreter.processor

import hitsedu.interpreter.models.ConsoleOutput
import hitsedu.interpreter.models.E
import hitsedu.interpreter.models.Value
import hitsedu.interpreter.models.operation.*
import hitsedu.interpreter.syntax.ParserLogic
import hitsedu.interpreter.utils.Type


fun OperationFor.process(
    variables: MutableList<OperationVariable>,
    arrays: MutableList<OperationArray>,
    console: MutableList<ConsoleOutput>
): E? {
    val outputs = mutableListOf<ConsoleOutput>()
    println("[FOR DEBUG] Starting for loop processing")
    println("[FOR DEBUG] Initialization: ${variable.value}")
    println("[FOR DEBUG] Condition: ${condition.value}")
    println("[FOR DEBUG] Step: ${value.value}")
    println("[FOR DEBUG] Initial variables: ${variables.map { "${it.name}=${it.value.value}" }}")
    println("[FOR DEBUG] Initial arrays: ${arrays.map { "${it.name}=${it.values.map { it.value }}" }}")

    val initParts = variable.value.split("=").map { it.trim() }
    if (initParts.size != 2) {
        val error = E("Invalid for loop initialization: ${variable.value}", id)
        println("[FOR DEBUG] Initialization error: $error")
        return error
    }
    val varName = initParts[0]
    val initValue = initParts[1]

    val processedInitValue = try {
        println("[FOR DEBUG] Processing init value: $initValue")
        Value(initValue).process(variables, arrays)?.also {
            println("[FOR DEBUG] Processed init value result: ${it.value}")
        } ?: run {
            val error = E("Failed to process initial value: $initValue", id)
            println("[FOR DEBUG] Init value processing failed: $error")
            return error
        }
    } catch (e: Exception) {
        val error = E("Error processing initial value: ${e.message}", id)
        println("[FOR DEBUG] Init value processing exception: $error")
        return error
    }

    variables.find { it.name == varName }?.let {
        println("[FOR DEBUG] Updating existing variable $varName from ${it.value.value} to ${processedInitValue.value}")
        variables[variables.indexOf(it)] = it.copy(value = processedInitValue)
    } ?: run {
        println("[FOR DEBUG] Adding new variable $varName = ${processedInitValue.value}")
        variables.add(OperationVariable(varName, processedInitValue))
    }

    var iteration = 0
    while (true) {
        iteration++
        println("\n[FOR DEBUG] Iteration $iteration")
        println("[FOR DEBUG] Current variables: ${variables.map { "${it.name}=${it.value.value}" }}")

        val conditionResult = try {
            println("[FOR DEBUG] Evaluating condition: ${condition.value}")
            ParserLogic.parseLogicExpression(
                condition.value,
                resolve = { name ->
                    println("[FOR DEBUG] Resolving '$name' for condition")
                    Value(name).process(variables, arrays)?.let { value ->
                        val result = when (value.type) {
                            Type.BOOLEAN -> value.value.toBoolean()
                            Type.INT -> value.value.toInt()
                            Type.DOUBLE -> value.value.toDouble()
                            else -> throw Exception("Condition must evaluate to boolean or number")
                        }
                        println("[FOR DEBUG] Resolved '$name' = $result")
                        result
                    } ?: throw Exception("Cannot resolve value: $name")
                }
            ).also {
                println("[FOR DEBUG] Condition result: $it")
            }
        } catch (e: Exception) {
            val error = E("Error in for loop condition: ${e.message}", id)
            println("[FOR DEBUG] Condition evaluation error: $error")
            return error
        }

        if (!conditionResult) {
            println("[FOR DEBUG] Condition is false, exiting loop")
            break
        }

        println("[FOR DEBUG] Executing ${scope.operations.size} operations in loop body")
        for (op in scope.operations) {
            println("[FOR DEBUG] Processing operation: ${op::class.simpleName}")
            when (val result = when (op) {
                is OperationVariable -> {
                    println("[FOR DEBUG] Processing variable ${op.name} = ${op.value.value}")
                    op.process(variables, arrays)
                }
                is OperationArray -> {
                    println("[FOR DEBUG] Processing array ${op.name}")
                    op.process(variables, arrays)
                }
                is OperationArrayIndex -> {
                    println("[FOR DEBUG] Processing array index ${op.name}[${op.index.value}] = ${op.value.value}")
                    op.process(variables, arrays)
                }
                is OperationOutput -> {
                    println("[FOR DEBUG] Processing output ${op.value.value}")
                    val output = op.process(variables, arrays)
                    outputs.add(output)
                    output.exception?.let { return it }
                    null
                }
                is OperationIf -> {
                    println("[FOR DEBUG] Processing if condition ${op.value.value}")
                    when (val conditionResult = op.process(variables, arrays)) {
                        true -> {
                            println("[FOR DEBUG] If condition is true, executing scope")
                            for (innerOp in op.scope.operations) {
                                when (val innerResult = when (innerOp) {
                                    is OperationVariable -> innerOp.process(variables, arrays)
                                    is OperationArray -> innerOp.process(variables, arrays)
                                    is OperationArrayIndex -> innerOp.process(variables, arrays)
                                    is OperationOutput -> innerOp.process(variables, arrays).exception
                                    is OperationIf -> innerOp.process(variables, arrays)?.let { E("If condition failed", innerOp.id) }
                                    is OperationFor -> innerOp.process(variables, arrays, console)
                                    else -> E("Unsupported operation in if body", innerOp.id)
                                }) {
                                    null -> continue
                                    else -> return innerResult
                                }
                            }
                            null
                        }
                        false -> {
                            println("[FOR DEBUG] If condition is false, skipping scope")
                            null
                        }
                        else -> conditionResult?.let { E("If condition evaluation failed", op.id) } ?: E("Unknown if condition error", op.id)
                    }
                }
                is OperationFor -> {
                    println("[FOR DEBUG] Processing nested for loop")
                    op.process(variables, arrays, console)
                }
                else -> {
                    val error = E("Unsupported operation in for loop body", op.id)
                    println("[FOR DEBUG] Unsupported operation: $error")
                    error
                }
            }) {
                null -> {
                    println("[FOR DEBUG] Operation completed successfully")
                    continue
                }
                else -> {
                    println("[FOR DEBUG] Operation failed: $result")
                    return result
                }
            }
        }

        val stepExpression = value.value
        println("[FOR DEBUG] Processing step: $stepExpression")
        val processedStepValue = try {
            val currentVar = variables.find { it.name == varName }
                ?: return E("Loop variable $varName not found", id).also {
                    println("[FOR DEBUG] Variable $varName not found")
                }

            val currentValue = currentVar.value.value
            println("[FOR DEBUG] Current $varName value: $currentValue")

            val expressionToProcess = stepExpression.replace(varName, currentValue)
            println("[FOR DEBUG] Expression after replacement: $expressionToProcess")

            Value(expressionToProcess).process(variables, arrays)?.also {
                println("[FOR DEBUG] Step result: ${it.value}")
            } ?: return E("Failed to process step value: $stepExpression", id).also {
                println("[FOR DEBUG] Step processing returned null")
            }
        } catch (e: Exception) {
            val error = E("Error processing step value: ${e.message}", id)
            println("[FOR DEBUG] Step processing error: $error")
            return error
        }

        variables.find { it.name == varName }?.let {
            println("[FOR DEBUG] Updating $varName from ${it.value.value} to ${processedStepValue.value}")
            variables[variables.indexOf(it)] = it.copy(value = processedStepValue)
        } ?: return E("Loop variable $varName not found", id).also {
            println("[FOR DEBUG] Variable $varName not found for update")
        }
    }

    println("[FOR DEBUG] Loop completed successfully")
    println("[FOR DEBUG] Final variables: ${variables.map { "${it.name}=${it.value.value}" }}")
    println("[FOR DEBUG] Final arrays: ${arrays.map { "${it.name}=${it.values.map { it.value }}" }}")
    outputs.forEach { console.add(it) }
    return null
}