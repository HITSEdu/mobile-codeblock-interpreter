package hitsedu.interpreter

import hitsedu.interpreter.models.Project
import hitsedu.interpreter.models.Scope
import hitsedu.interpreter.models.Value
import hitsedu.interpreter.models.operation.*
import hitsedu.interpreter.utils.Type

object MockData {
    val testOutput = Project(
        caption = "test output",
        scale = 1f,
        scopes = emptyList(),
        globalScope = Scope(
            operations = listOf(
                OperationArray(
                    name = "arr",
                    values = listOf(
                        Value("13"),
                        Value("84"),
                    ),
                    id = 8,
                ),
                OperationVariable(
                    name = "a",
                    value = Value("150"),
                    id = 1,
                ),
                OperationVariable(
                    name = "logic",
                    value = Value("8"),
                    id = 5,
                ),
                OperationOutput(
                    value = Value("a"),
                    id = 33,
                ),
                OperationOutput(
                    value = Value("arr"),
                    id = 333,
                ),
                OperationOutput(
                    value = Value("arr[2]"),
                    id = 3333,
                ),
                OperationOutput(
                    value = Value("14 + 1000"),
                    id = 33333,
                ),
                OperationOutput(
                    value = Value("(1 + 2 - 3) * 5 + 100/10"),
                    id = 33333,
                ),
                OperationOutput(
                    value = Value("a < 10"),
                    id = 33333,
                ),
                OperationOutput(
                    value = Value("5 = 5"),
                    id = 33333,
                ),
                OperationOutput(
                    value = Value("logic"),
                    id = 33333,
                ),
            ),
            id = 5,
        ),
        id = 0,
    )

    val arrayTest = Project(
        caption = "array test",
        scale = 1f,
        scopes = emptyList(),
        globalScope = Scope(
            operations = listOf(
                OperationArray(
                    name = "arr",
                    size = 0,
                    values = listOf(
                        Value("1"),
                        Value("2"),
                        Value("3"),
                        Value("4"),
                    ),
                    id = 148,
                ),
                OperationArrayIndex(
                    name = "arr",
                    index = Value("1"),
                    value = Value("800"),
                    id = 996152,
                ),
                OperationOutput(
                    value = Value("arr"),
                    id = 503,
                )
            ),
            id = 1,
        ),
        id = 0
    )

    val nestedProgram = Project(
        caption = "nested",
        scale = 1f,
        scopes = emptyList(),
        globalScope = Scope(
            id = 1,
            operations = listOf(
                OperationVariable(
                    name = "x",
                    value = Value("10"),
                    id = 100
                ),
                OperationIf(
                    value = Value("x > 5"),
                    id = 101,
                    scope = Scope(
                        id = 2,
                        operations = listOf(
                            OperationVariable(
                                name = "y",
                                value = Value("20"),
                                id = 102
                            ),
                            OperationIf(
                                value = Value("y == 20"),
                                id = 103,
                                scope = Scope(
                                    id = 3,
                                    operations = listOf(
                                        OperationArray(
                                            name = "arr",
                                            size = 3,
                                            values = listOf(
                                                Value("1"),
                                                Value("2"),
                                                Value("3")
                                            ),
                                            id = 104
                                        )
                                    )
                                )
                            ),
                            OperationOutput(
                                value = Value("\"Inside outer if\""),
                                id = 105
                            )
                        )
                    )
                ),
                OperationOutput(
                    value = Value("\"Outside if\""),
                    id = 106
                )
            )
        ),
        id = 0,
    )

    val forLoopTest = Project(
        caption = "for loop test",
        scale = 1f,
        scopes = emptyList(),
        globalScope = Scope(
            id = 500,
            operations = listOf(
                OperationFor(
                    id = 1002,
                    variable = Value("i = 0"),
                    condition = Value("i < 5"),
                    value = Value("i + 1"),
                    scope = Scope(
                        id = 2,
                        operations = listOf(
                            OperationArrayIndex(
                                name = "arr",
                                index = Value("i"),
                                value = Value("i * 2"),
                                id = 1003
                            )
                        )
                    )
                )

            )
        ),
        id = 0
    )

    val forLoopTest1 = Project(
        caption = "for loop test 1",
        scale = 1f,
        scopes = emptyList(),
        globalScope = Scope(
            id = 1,
            operations = listOf(
                OperationArray(
                    name = "arr",
                    size = 5,
                    values = List(5) { Value("0") },
                    id = 1001
                ),
                OperationFor(
                    id = 1002,
                    variable = Value("i = 0"),
                    condition = Value("i < 5"),
                    value = Value("i + 1"),
                    scope = Scope(
                        id = 2,
                        operations = listOf(
                            OperationArrayIndex(
                                name = "arr",
                                index = Value("i"),
                                value = Value("i * 2"),
                                id = 1003
                            )
                        )
                    )
                )
            )
        ),
        id = 0
    )

    val printFromForLoop = Project(
        caption = "print from for loop",
        scale = 1f,
        scopes = emptyList(),
        globalScope = Scope(
            id = 999,
            operations = listOf(
                OperationFor(
                    id = 1001,
                    variable = Value("i = 0"),
                    condition = Value("i <= 5"),
                    value = Value("i = i + 1"),
                    scope = Scope(
                        id = 1002,
                        operations = listOf(
                            OperationOutput(
                                value = Value("i"),
                                id = 1003
                            )
                        )
                    )
                )
            )
        ),
        id = 0
    )

    val elseTest = Project(
        caption = "else test",
        scale = 1f,
        scopes = emptyList(),
        globalScope = Scope(
            id = 999,
            operations = listOf(
                OperationIf(
                    scope = Scope(
                        listOf(
                            OperationOutput(
                                value = Value("\"If\""),
                                id = 6,
                            )
                        )
                    ),
                    value = Value("3 > 5"),
                    id = 4
                ),
                OperationElse(
                    scope = Scope(
                        operations = listOf(
                            OperationOutput(
                                value = Value("\"Else\""),
                                id = 6,
                            )
                        ),
                        id = 94
                    ),
                    id = 8
                ),
                OperationElse(
                    scope = Scope(
                        operations = listOf(
                            OperationOutput(
                                value = Value("\"Else with error\""),
                                id = 1725,
                            )
                        ),
                        id = 12
                    ),
                    id = 10
                )
            )
        ),
        id = 0
    )

    val mathExpression = Scope(
        operations = listOf(
            OperationVariable(
                name = "a",
                value = Value("((17 + 3) * 2 + 5)"),
                id = 4
            ),
            OperationIf(
                scope = Scope(
                    listOf(
                        OperationOutput(
                            value = Value("\"true\""),
                            id = 735,
                        )
                    )
                ),
                value = Value("a < 45"),
                id = 90,
            ),
            OperationOutput(
                value = Value("a"),
                id = 12,
            )
        ),
        id = 8,
    )

    val arr = Scope(
        operations = listOf(
            OperationArray(
                name = "arr",
                values = listOf(
                    Value("37"),
                    Value("1488"),
                    Value("920"),
                    Value("5"),
                    Value("777"),
                ),
                id = 17,
            ),
            OperationArrayIndex(
                name = "arr",
                index = Value("2"),
                value = Value("19999"),
                id = 801,
            ),
            OperationOutput(
                value = Value("arr"),
                id = 12,
            ),
            OperationOutput(
                value = Value("(14 > 12 && 18 + 46 > 3) && (15 < 3 || 7 > 9)"),
                id = 21,
            )
        ),
        id = 8,
    )

    val types = Scope(
        operations = listOf(
            OperationVariable(
                name = "a",
                value = Value("(4 < 1 || 3 != 3) && 4 > 1"),
                id = 800,
            ),
            OperationOutput(
                value = Value("a"),
                id = 12,
            ),
        ),
        id = 8,
    )

    val math = Scope(
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

    val s = Scope(
        id = 0,
        operations = listOf(
            OperationArray(
                id = 20,
                name = "arr",
                values = listOf(
                    Value("1"), Value("2"), Value("3")
                ),
            ),
            OperationVariable(
                id = 2,
                name = "a",
                value = Value("1"),
            ),
            OperationOutput(
                id = 1,
                value = Value("arr[1]"),
            ),
            OperationOutput(
                id = 1,
                value = Value("arr[0] + arr[1] + 10000"),
            ),
            OperationOutput(
                id = 17,
                value = Value("5 <= arr[a]"),
            )
        )
    )

    val nestedLoopsTest = Project(
        caption = "Nested Loops Test",
        scale = 1f,
        scopes = emptyList(),
        globalScope = Scope(
            id = 1,
            operations = listOf(
                OperationArray(
                    name = "outerArray",
                    size = 3,
                    values = List(3) { Value("0") },
                    id = 100
                ),
                OperationFor(
                    id = 101,
                    variable = Value("i = 0"),
                    condition = Value("i < 3"),
                    value = Value("i + 1"),
                    scope = Scope(
                        id = 2,
                        operations = listOf(
                            OperationArrayIndex(
                                name = "outerArray",
                                index = Value("i"),
                                value = Value("i * 10"),
                                id = 102
                            ),
                            OperationFor(
                                id = 103,
                                variable = Value("j = 0"),
                                condition = Value("j < 2"),
                                value = Value("j + 1"),
                                scope = Scope(
                                    id = 3,
                                    operations = listOf(
                                        OperationArrayIndex(
                                            name = "outerArray",
                                            index = Value("j"),
                                            value = Value("j + 1488"),
                                            id = 102
                                        ),
                                    )
                                )
                            )
                        )
                    )
                ),
                OperationOutput(
                    value = Value("outerArray"),
                    id = 105
                )
            )
        ),
        id = 0
    )

    val templateBubbleSort1 = Project(
        caption = "bbs test",
        scale = 1f,
        scopes = emptyList(),
        id = 12,
        globalScope = Scope(
            listOf(
                OperationArray(
                    name = "a",
                    size = 4,
                    values = listOf(
                        Value("500",type = Type.INT),
                        Value("300",type = Type.INT),
                        Value("100",type = Type.INT),
                        Value("200",type = Type.INT)
                    )
                ),
                OperationFor(
                    variable = Value("i = 0"),
                    condition = Value("i < 3"),
                    value = Value("i + 1"),
                    scope = Scope(
                        operations = listOf(
                            OperationFor(
                                variable = Value("j = 0"),
                                condition = Value("j < (3 - i)"),
                                value = Value("j + 1"),
                                scope = Scope(
                                    operations = listOf(
                                        OperationIf(
                                            value = Value("a[j] > a[j+1]"),
                                            scope = Scope(
                                                operations = listOf(
                                                    OperationVariable(
                                                        name = "tmp",
                                                        value = Value("a[j]")
                                                    ),
                                                    OperationArrayIndex(
                                                        name = "a",
                                                        index = Value("j"),
                                                        value = Value("a[j + 1]")
                                                    ),
                                                    OperationArrayIndex(
                                                        name = "a",
                                                        index = Value("j + 1"),
                                                        value = Value("tmp")
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                ),
                OperationOutput(value = Value("a"))
            )
        )
    )


    val fibonacci = Project(
        caption = "fibonacci test",
        scale = 1f,
        scopes = emptyList(),
        id = 12,
        globalScope = Scope(
            listOf(
                OperationVariable(
                    "fib1",
                    Value("1")
                ),
                OperationVariable(  // Добавлена инициализация fib2
                    "fib2",
                    Value("1")
                ),
                OperationVariable(
                    "n",
                    Value("8")
                ),
                OperationFor(
                    variable = Value("i = 0"),
                    condition = Value("i < n"),
                    value = Value("i + 1"),
                    scope = Scope(
                        operations = listOf(
                            OperationVariable(
                                "tmp",
                                Value("fib1")
                            ),
                            OperationVariable(
                                "fib1",
                                Value("fib2")
                            ),
                            OperationVariable(
                                "fib2",
                                Value("tmp + fib2")
                            ),
                            OperationOutput(Value("fib2"))
                        )
                    )
                ),
                OperationOutput(Value("fib2"))
            )
        )
    )
}