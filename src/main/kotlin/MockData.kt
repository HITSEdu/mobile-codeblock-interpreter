package hitsedu.interpreter

import hitsedu.interpreter.models.Project
import hitsedu.interpreter.models.Scope
import hitsedu.interpreter.models.Value
import hitsedu.interpreter.models.operation.*

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
                    value = Value("\"150\""),
                    id = 1,
                ),
                OperationVariable(
                    name = "logic",
                    value = Value("true"),
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

    val printIf = Project(
        caption = "test if",
        scale = 1f,
        scopes = emptyList(),
        globalScope = Scope(
            operations = listOf(
                OperationVariable(
                    name = "you",
                    value = Value("\"danil\""),
                    id = 40,
                ),
                OperationIf(
                    scope = Scope(
                        listOf(
                            OperationVariable(
                                name = "test",
                                value = Value("15"),
                                id = 92,
                            ),
                            OperationOutput(
                                value = Value("50"),
                                id = 3,
                            )
                        )
                    ),
                    value = Value("5 < 16"),
                    id = 1488,
                ),
                OperationOutput(
                    value = Value("you"),
                    id = 3,
                )
            ),
            id = 15,
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
                    value = Value("i = i + 1"),
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
}