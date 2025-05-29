package hitsedu.interpreter

fun main() {
    val interpreter = InterpreterImpl()
    interpreter.process(MockData.nestedLoopsTest.globalScope)
    println(interpreter.getConsole().joinToString("\n"))
}