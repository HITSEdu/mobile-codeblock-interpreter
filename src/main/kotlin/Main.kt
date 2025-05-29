package hitsedu.interpreter

fun main() {
    val interpreter = InterpreterImpl()
    interpreter.process(MockData.elseTest.globalScope)
    println(interpreter.getConsole().joinToString("\n"))
}