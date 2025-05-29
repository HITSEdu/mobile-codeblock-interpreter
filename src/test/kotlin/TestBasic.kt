import hitsedu.interpreter.InterpreterImpl
import hitsedu.interpreter.models.Scope
import hitsedu.interpreter.models.Value
import hitsedu.interpreter.models.operation.Operation
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
}