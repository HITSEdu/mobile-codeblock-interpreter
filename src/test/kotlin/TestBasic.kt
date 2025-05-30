import hitsedu.interpreter.InterpreterImpl
import hitsedu.interpreter.models.Scope
import hitsedu.interpreter.models.Value
import hitsedu.interpreter.models.operation.OperationArray
import hitsedu.interpreter.models.operation.OperationArrayIndex
import hitsedu.interpreter.models.operation.OperationOutput
import hitsedu.interpreter.models.operation.OperationVariable
import org.junit.jupiter.api.Test
import kotlin.test.junit5.JUnit5Asserter.assertEquals

class TestBasic {
    @Test
    fun testSimpleMath() {
        val interpreter = InterpreterImpl()
        val scope = Scope(
            id = 0,
            operations = listOf(
                OperationOutput(
                    id = 1,
                    value = Value("(8 + (3 + (45 / 5) * 3 - 30/2) * 2)/4"),
                )
            )
        )
        interpreter.process(scope)
        val output = interpreter.getConsole()[0]
        assertEquals(
            expected = "9.5",
            actual = output.output,
            message = "[OUT]: Simple math",
        )
    }

    @Test
    fun testMathWithVariables() {
        val interpreter = InterpreterImpl()
        val scope = Scope(
            id = 0,
            operations = listOf(
                OperationVariable(
                    id = 2,
                    name = "a",
                    value = Value("((36 * 19) - 150) / 2 + 40"), // 307
                ),
                OperationVariable(
                    id = 3,
                    name = "b",
                    value = Value("3 + (((94 / 2) - 1) / 2) /2"), // 14.5
                ),
                OperationVariable(
                    id = 4,
                    name = "c",
                    value = Value("1/4"), // 0.25
                ),
                OperationOutput(
                    id = 1,
                    value = Value("(a - b) * c"),
                )
            )
        )
        interpreter.process(scope)
        val output = interpreter.getConsole()[0]
        assertEquals(
            expected = "73.125",
            actual = output.output,
            message = "[OUT]: Math with variables",
        )
    }

    @Test
    fun testMathWithArray() {
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
                    value = Value("arr"),
                ),
                OperationArrayIndex(
                    id = 3,
                    name = "arr",
                    index = Value("0"),
                    value = Value("1111"),
                ),
                OperationOutput(
                    id = 2,
                    value = Value("arr"),
                ),
                OperationOutput(
                    id = 5,
                    value = Value("arr[1 + (2 + 1)/3]"),
                ),
            )
        )
        interpreter.process(scope)
        val output = interpreter.getConsole()
        assertEquals(
            expected = "[1, 2, 3, 4, 5]",
            actual = output[0].output,
            message = "[OUT]: Math with array",
        )
        assertEquals(
            expected = "[1111.0, 2, 3, 4, 5]",
            actual = output[1].output,
            message = "[OUT]: Math with array",
        )
        assertEquals(
            expected = "3",
            actual = output[2].output,
            message = "[OUT]: Math with array",
        )
    }

    @Test
    fun testSimpleLogic() {
        val interpreter = InterpreterImpl()
        val scope = Scope(
            id = 0,
            operations = listOf(
                OperationVariable(
                    id = 3,
                    name = "a",
                    value = Value("true"),
                ),
                OperationOutput(
                    id = 1,
                    value = Value("(5 > 3 && 7 != 5) || (4 == 3) && a"),
                )
            )
        )
        interpreter.process(scope)
        val output = interpreter.getConsole()[0]
        assertEquals(
            expected = "true",
            actual = output.output,
            message = "[OUT]: Simple logic",
        )
    }

    @Test
    fun testArray () {
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
                    value = Value("arr"),
                ),
                OperationArrayIndex(
                    id = 3,
                    name = "arr",
                    index = Value("0"),
                    value = Value("12"),
                ),
                OperationVariable(
                    id = 16,
                    name = "a",
                    value = Value("5 > 3 && (4 >= 1 || 3 == 7) || 5 <= 1")
                ),
                OperationOutput(
                    id = 2,
                    value = Value("arr[0] < arr[4] && a"),
                ),
            )
        )
        interpreter.process(scope)
        val output = interpreter.getConsole()
        assertEquals(
            expected = "[1, 2, 3, 4, 5]",
            actual = output[0].output,
            message = "[OUT]: Math with array",
        )
        assertEquals(
            expected = "false",
            actual = output[1].output,
            message = "[OUT]: Math with array",
        )
    }
}