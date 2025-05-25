package processor

import models.Value
import models.operation.OperationArray
import models.operation.OperationVariable
import utils.Parser

fun Value.process(variables: List<OperationVariable>, arrays: List<OperationArray>): Int {
    return Parser.parseMathExpression(this.value) { name ->

        variables.find { it.name == name }?.value?.value?.toIntOrNull()

            ?: arrays.firstOrNull { name.startsWith(it.name + "[") }
                ?.let { array ->
                    val regex = Regex("""\[(\d+)]""")
                    val index = regex.find(name)?.groupValues?.get(1)?.toIntOrNull() ?: return@let null
                    array.values.getOrNull(index)?.value?.toIntOrNull()
                }
            ?: error("Cannot resolve value: $name")
    }
}