import utils.MockData

fun main() {
    val interpreter = Interpreter()
    interpreter.process(MockData.arrayTest.globalScope)
    println(interpreter.getConsole().joinToString("\n"))
}