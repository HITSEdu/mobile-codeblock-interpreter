package hitsedu.interpreter

fun main() {
    val interpreter = InterpreterImpl()
    interpreter.process(MockData.arr)
    println(interpreter.getConsole().joinToString("\n"))
}