fun main() {
    val interpreter = Interpreter()
    interpreter.process()
    println(interpreter.console.joinToString("\n") { it.output })
    println(interpreter.arrays)
}