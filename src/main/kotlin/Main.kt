package hitsedu.interpreter

fun main() {
    val interpreter = InterpreterImpl()
    interpreter.process(MockData.scope)
    println(interpreter.getConsole().joinToString("\n"))
}