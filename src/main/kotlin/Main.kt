package hitsedu.interpreter

fun main() {
    val interpreter = InterpreterImpl()
    interpreter.process(MockData.fibonacci.globalScope)
    println(interpreter.getConsole().joinToString("\n"))
    println(interpreter.arrays)
}