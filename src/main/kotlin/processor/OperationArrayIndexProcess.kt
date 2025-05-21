package processor

import hitsedu.interpreter.models.operation.OperationArray
import hitsedu.interpreter.models.operation.OperationArrayIndex

fun OperationArrayIndex.process(arrays: List<OperationArray>) {
    arrays.forEach { a ->
        if (a.name == name) {
            a.values.map {  }
        }
    }
}