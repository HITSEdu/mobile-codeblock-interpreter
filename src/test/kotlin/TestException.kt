import hitsedu.interpreter.InterpreterImpl
import hitsedu.interpreter.models.Scope
import hitsedu.interpreter.models.Value
import hitsedu.interpreter.models.operation.OperationArray
import hitsedu.interpreter.models.operation.OperationOutput
import kotlin.test.Test
import kotlin.test.assertEquals

class TestException {
    @Test
    fun testOutOfBounds() {
        val interpreter = InterpreterImpl()
        val scope = Scope(
            id = 0,
            operations = listOf(
                OperationArray(
                    id = 8,
                    name = "arr",
                    values = listOf(
                        Value("1"),
                        Value("2"),
                        Value("3"),
                        Value("4"),
                        Value("5"),
                    ),
                ),
                OperationOutput(
                    id = 1,
                    value = Value("arr[8]"),
                ),
            )
        )
        interpreter.process(scope)
        val output = interpreter.getConsole()
        assertEquals(
            expected = "Array index out of bounds",
            actual = output[0].exception?.message,
            message = "[OUT]: Math with array",
        )
    }

    @Test
    fun testElseWithoutIf() {

    }
}