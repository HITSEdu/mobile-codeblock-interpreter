package hitsedu.interpreter

fun main() {
    val interpreter = InterpreterImpl()
    interpreter.process(MockData.mathExpression)
    println(interpreter.getConsole().joinToString("\n"))
    println(interpreter.arrays)
}