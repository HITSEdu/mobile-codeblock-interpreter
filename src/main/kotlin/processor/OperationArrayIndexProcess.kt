package processor

import models.operation.OperationArray
import models.operation.OperationArrayIndex

fun OperationArrayIndex.process(arrays: MutableList<OperationArray>){
    val idx = index.value.toIntOrNull() ?: return // TODO("throw IllegalArgument exception")

    val i = arrays.indexOfFirst { it.name == name }
    if (i == -1) return //TODO("throw index out of bound")

    val array = arrays[i]
    if (idx !in array.values.indices) return // TODO("throw index of bound")

    val values = array.values.mapIndexed { j, v -> if (j == idx) value else v }
    arrays[i] = array.copy(values = values)
}