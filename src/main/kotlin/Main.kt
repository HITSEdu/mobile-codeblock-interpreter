package hitsedu.interpreter

fun main() {
    val interpreter = InterpreterImpl()
    interpreter.process(MockData.types)
    println(interpreter.getConsole().joinToString("\n"))
}