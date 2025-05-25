package processor

import Interpreter
import models.Scope
import models.Value
import models.operation.OperationArray
import models.operation.OperationFor
import models.operation.OperationVariable
import utils.Parser

fun OperationFor.process(
    variables: MutableList<OperationVariable>,
    arrays: MutableList<OperationArray>,
    interpreter: Interpreter
) {
    // 1. Инициализация переменной цикла
    initializeLoopVariable(variables, arrays)

    // 2. Выполнение цикла
    while (shouldContinueLoop(variables, arrays)) {
        // 3. Создаем изолированный контекст для тела цикла
        val loopInterpreter = createLoopInterpreter(interpreter, variables, arrays)

        // 4. Выполняем тело цикла
        loopInterpreter.process(scope)

        // 5. Обновляем состояние после итерации
        updateAfterIteration(loopInterpreter, variables, arrays)
    }
}

private fun OperationFor.initializeLoopVariable(
    variables: MutableList<OperationVariable>,
    arrays: MutableList<OperationArray>
) {
    val initialValue = Parser.parseAssignment(
        exp = variable.value.value,
        resolve = { resolveVariable(it, variables, arrays) },
        assignVar = { name, value -> updateVariable(name, value, variables) },
        assignArray = { name, index, value -> updateArrayElement(name, index, value, arrays) }
    )

    updateVariable(variable.name, initialValue, variables)
}

private fun OperationFor.shouldContinueLoop(
    variables: MutableList<OperationVariable>,
    arrays: MutableList<OperationArray>
): Boolean {
    return try {
        Parser.parseLogicExpression(condition.value) {
            resolveVariable(it, variables, arrays)
        }
    } catch (e: Exception) {
        false // В случае ошибки прерываем цикл
    }
}

private fun createLoopInterpreter(
    parent: Interpreter,
    variables: List<OperationVariable>,
    arrays: List<OperationArray>
): Interpreter {
    return Interpreter().apply {
        // Копируем все состояния из родительского интерпретатора
        this.variables.addAll(variables.map { it.copy() })
        this.arrays.addAll(arrays.map { it.copy(values = it.values.toMutableList()) })
        this.console.addAll(parent.console)
    }
}

private fun OperationFor.updateAfterIteration(
    loopInterpreter: Interpreter,
    variables: MutableList<OperationVariable>,
    arrays: MutableList<OperationArray>
) {
    // 1. Обновляем переменные
    variables.clear()
    variables.addAll(loopInterpreter.variables)

    // 2. Обновляем массивы
    arrays.clear()
    arrays.addAll(loopInterpreter.arrays)

    // 3. Обновляем переменную цикла
    val updatedValue = Parser.parseAssignment(
        exp = value.value,
        resolve = { resolveVariable(it, variables, arrays) },
        assignVar = { name, value -> updateVariable(name, value, variables) },
        assignArray = { name, index, value -> updateArrayElement(name, index, value, arrays) }
    )

    updateVariable(variable.name, updatedValue, variables)
}

// region Вспомогательные функции

private fun resolveVariable(
    name: String,
    variables: List<OperationVariable>,
    arrays: List<OperationArray>
): Int {
    // 1. Пробуем найти обычную переменную
    variables.find { it.name == name }?.let {
        return it.value.value.toIntOrNull() ?: error("Invalid variable value: ${it.value.value}")
    }

    // 2. Пробуем найти элемент массива
    val arrayAccess = parseArrayAccess(name) ?: error("Unknown variable: $name")
    val array = arrays.find { it.name == arrayAccess.first } ?: error("Array ${arrayAccess.first} not found")

    if (arrayAccess.second !in array.values.indices) {
        error("Index ${arrayAccess.second} out of bounds for array ${array.name}")
    }

    return array.values[arrayAccess.second].value.toIntOrNull()
        ?: error("Invalid array element value at index ${arrayAccess.second}")
}

private fun parseArrayAccess(access: String): Pair<String, Int>? {
    val regex = Regex("""([a-zA-Z_]\w*)\[(\d+)]""")
    val match = regex.matchEntire(access) ?: return null
    return match.groupValues[1] to match.groupValues[2].toInt()
}

private fun updateVariable(
    name: String,
    value: Int,
    variables: MutableList<OperationVariable>
) {
    variables.removeAll { it.name == name }
    variables.add(OperationVariable(name, Value(value.toString())))
}

private fun updateArrayElement(
    name: String,
    index: Int,
    value: Int,
    arrays: MutableList<OperationArray>
) {
    val array = arrays.find { it.name == name } ?: error("Array $name not found")
    if (index !in array.values.indices) error("Index $index out of bounds")

    arrays.replaceAll {
        if (it.name == name) {
            val newValues = it.values.toMutableList().apply {
                this[index] = Value(value.toString())
            }
            it.copy(values = newValues)
        } else {
            it
        }
    }
}

// endregion