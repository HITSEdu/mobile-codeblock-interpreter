package utils

import hitsedu.interpreter.models.Scope
import hitsedu.interpreter.models.ScopeGlobal
import hitsedu.interpreter.models.Value
import models.operation.OperationArray
import models.operation.OperationArrayIndex
import models.operation.OperationIf
import models.operation.OperationOutput
import models.operation.OperationVariable

object MockData {
    val printVariable = ScopeGlobal(
        operations = listOf(
            OperationVariable(
                name = "a",
                value = Value("50"),
                id = 1,
            ),
            OperationOutput(
                value = Value("50"),
                id = 3,
            )
        ),
        id = 5,
        variableUIOS = listOf(
            OperationVariable(
                name = "a",
                value = Value("50"),
                id = 1,
            )
        ),
        arrayUIOS = emptyList(),
    )

    val printIf = ScopeGlobal(
        operations = listOf(
            OperationVariable(
                name = "you",
                value = Value("danil"),
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
            )
        ),
        id = 15,
        variableUIOS = listOf(

        ),
        arrayUIOS = emptyList(),
    )

    val arrayTest = ScopeGlobal(
        id = 1,
        variableUIOS = emptyList(),
        arrayUIOS = emptyList(),
        operations = listOf(
            OperationArray(
                name = "arr",
                size = 0,
                values = listOf(
                    Value("1"),
                    Value("0"),
                    Value("14"),
                    Value("42"),
                ),
                id = 148,
            ),
            OperationArrayIndex(
                name = "arr",
                index = Value("1"),
                value = Value("1604"),
                id = 996152,
            )
        )
    )

    val nestedProgram = ScopeGlobal(
        id = 1,
        variableUIOS = emptyList(),
        arrayUIOS = emptyList(),
        operations = listOf(
            // var
            OperationVariable(
                name = "x",
                value = Value("10"),
                id = 100
            ),

            // if {
            OperationIf(
                value = Value("x > 5"),
                id = 101,
                scope = Scope(
                    id = 2,
                    operations = listOf(
                        // var
                        OperationVariable(
                            name = "y",
                            value = Value("20"),
                            id = 102
                        ),

                        // if {
                        OperationIf(
                            value = Value("y == 20"),
                            id = 103,
                            scope = Scope(
                                id = 3,
                                operations = listOf(
                                    // arr
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

                        // out
                        OperationOutput(
                            value = Value("\"Inside outer if\""),
                            id = 105
                        )
                    )
                )
            ),

            // out
            OperationOutput(
                value = Value("\"Outside if\""),
                id = 106
            )
        )
    )

}