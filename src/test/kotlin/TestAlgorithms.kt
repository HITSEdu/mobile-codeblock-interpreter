import hitsedu.interpreter.InterpreterImpl
import hitsedu.interpreter.models.Scope
import hitsedu.interpreter.models.Value
import hitsedu.interpreter.models.operation.*
import kotlin.test.Test
import kotlin.test.assertEquals

class TestAlgorithms {
    @Test
    fun testGrades() {
        val interpreter = InterpreterImpl()

        val scope = Scope(
            operations = listOf(
                OperationArray(
                    id = 20,
                    name = "grades",
                    values = listOf(
                        Value("85"), Value("92"), Value("78")
                    ),
                ),
                OperationVariable(
                    id = 503,
                    name = "index",
                    value = Value("1")
                ),
                OperationIf(
                    id = 913845,
                    value = Value("grades[index] >= 90"),
                    scope = Scope(
                        id = 2903875,
                        operations = listOf(
                            OperationOutput(
                                id = 9486754,
                                value = Value(
                                    "\"Great\""
                                )
                            )
                        )
                    ),
                ),
                OperationElse(
                    id = 39,
                    scope = Scope(
                        id = 45,
                        operations = listOf(
                            OperationIf(
                                value = Value("grades[index] >= 80"),
                                scope = Scope(
                                    id = 3756,
                                    operations = listOf(
                                        OperationOutput(
                                            id = 94867579,
                                            value = Value("\"Good\"")
                                        )
                                    )
                                ),
                            ),
                            OperationElse(
                                id = 12947,
                                scope = Scope(
                                    id = 90746,
                                    operations = listOf(
                                        OperationOutput(
                                            id = 28967984,
                                            value = Value("\"You can improve\"")
                                        )
                                    )
                                )
                            )
                        )
                    ),
                ),
                OperationOutput(
                    id = 1385738956,
                    value = Value("\"Complete\"")
                )
            )
        )
        interpreter.process(scope)
        val console = interpreter.getConsole()
        assertEquals(
            "Great",
            console[0].output
        )
        assertEquals(
            "Complete",
            console[1].output
        )
    }

    @Test
    fun testTemperatureAnalysis() {
        val interpreter = InterpreterImpl()

        val scope = Scope(
            id = 918274,
            operations = listOf(
                OperationArray(
                    id = 9182,
                    name = "cities",
                    values = listOf(Value("\"Москва\""), Value("\"Санкт-Петербург\""))
                ),
                OperationArray(
                    id = 918274153247,
                    name = "temps",
                    values = listOf(Value("22"), Value("18"))
                ),
                OperationVariable(
                    id = 918274817352467,
                    name = "cityIndex", value = Value("0")
                ),
                OperationIf(
                    value = Value("temps[cityIndex] > 20"),
                    scope = Scope(
                        id = 91827455555,
                        operations = listOf(
                            OperationOutput(
                                id = 9182741873264,
                                value = Value("cities[cityIndex]")
                            ),
                            OperationIf(
                                id = 91827417236,
                                value = Value("temps[cityIndex] > 25"),
                                scope = Scope(
                                    operations = listOf(
                                        OperationOutput(
                                            id = 9182741123,
                                            value = Value("\"Очень жарко!\"")
                                        )
                                    )
                                )
                            )
                        ),
                    )
                ),
                OperationArrayIndex(
                    id = 918274012947,
                    name = "temps",
                    index = Value("1"),
                    value = Value("19")
                ),
                OperationOutput(
                    id = 91827400987273,
                    value = Value("\"Данные обновлены\"")
                )
            )
        )
        interpreter.process(scope)
        val console = interpreter.getConsole()
        assertEquals(
            "\"Москва\"",
            console[0].output
        )
        assertEquals(
            "Данные обновлены",
            console[1].output
        )
    }

    @Test
    fun testAccessControl() {
        val interpreter = InterpreterImpl()

        val scope = Scope(
            operations = listOf(
                OperationArray(
                    id = 918274289476,
                    name = "permissions",
                    values = listOf(Value("true"), Value("false"))
                ),
                OperationVariable(
                    id = 9182748834,
                    name = "userRole", value = Value("1")
                ),
                OperationIf(
                    id = 9182741284,
                    value = Value("userRole == 0"),
                    scope = Scope(
                        id = 918274263,
                        operations = listOf(
                            OperationOutput(
                                id = 91827400123,
                                value = Value("\"Администратор\"")
                            ),
                            OperationIf(
                                id = 918274112227,
                                value = Value("permissions[0]"),
                                scope = Scope(
                                    id = 9182740,
                                    operations = listOf(
                                        OperationOutput(
                                            id = 918274,
                                            value = Value("\"Полный доступ\"")
                                        )
                                    )
                                )
                            )
                        )
                    )
                ),
                OperationIf(
                    id = 12418274,
                    value = Value("userRole == 1"),
                    scope = Scope(
                        id = 3754918274,
                        operations = listOf(
                            OperationOutput(
                                id = 56566918274,
                                value = Value("\"Пользователь\"")
                            ),
                            OperationIf(
                                id = 11283918274,
                                value = Value("permissions[1]"),
                                scope = Scope(
                                    id = 9111243,
                                    operations = listOf(
                                        OperationOutput(
                                            id = 91832735413,
                                            value = Value(
                                                "\"Доступ разрешен\""
                                            )
                                        )
                                    )
                                )
                            ),
                            OperationElse(
                                id = 1239111243,
                                scope = Scope(
                                    id = 9115551243,
                                    operations = listOf(
                                        OperationOutput(value = Value("\"Доступ запрещен\""))
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
        interpreter.process(scope)
        val console = interpreter.getConsole()
        assertEquals("Пользователь", console[0].output)
        assertEquals("Доступ запрещен", console[1].output)
    }
}