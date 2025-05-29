package hitsedu.interpreter

fun main() {
    val interpreter = InterpreterImpl()
    interpreter.process(MockData.math)
    println(interpreter.getConsole().joinToString("\n"))
}