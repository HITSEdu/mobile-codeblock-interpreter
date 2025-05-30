package hitsedu.interpreter.syntax

import hitsedu.interpreter.utils.Operators.LOGIC

object Tokenizer {
    fun tokenizeMath(exp: String): List<String> {
        val result = mutableListOf<String>()
        val buffer = StringBuilder()
        var i = 0

        while (i < exp.length) {
            val c = exp[i]
            when {
                c.isWhitespace() -> i++
                c.isDigit() || c == '.' && i + 1 < exp.length && exp[i + 1].isDigit() -> {
                    // Обработка чисел
                    buffer.append(c)
                    i++
                    while (i < exp.length) {
                        val nextChar = exp[i]
                        if (nextChar.isDigit() || nextChar == '.') {
                            buffer.append(nextChar)
                            i++
                        } else {
                            break
                        }
                    }
                    result.add(buffer.toString())
                    buffer.clear()
                }
                c.isLetter() || c == '_' -> {
                    // Обработка переменных и массивов
                    buffer.append(c)
                    i++
                    while (i < exp.length) {
                        val nextChar = exp[i]
                        if (nextChar.isLetterOrDigit() || nextChar == '_' || nextChar == '[' || nextChar == ']') {
                            buffer.append(nextChar)
                            i++
                            // Если это массив, читаем до закрывающей скобки
                            if (nextChar == '[') {
                                while (i < exp.length && exp[i] != ']') {
                                    buffer.append(exp[i])
                                    i++
                                }
                                if (i < exp.length) {
                                    buffer.append(exp[i]) // добавляем ']'
                                    i++
                                }
                            }
                        } else {
                            break
                        }
                    }
                    result.add(buffer.toString())
                    buffer.clear()
                }
                else -> {
                    if (buffer.isNotEmpty()) {
                        result.add(buffer.toString())
                        buffer.clear()
                    }
                    result.add(c.toString())
                    i++
                }
            }
        }

        if (buffer.isNotEmpty()) result.add(buffer.toString())
        return result
    }

    fun tokenizeLogic(exp: String): List<String> {
        val tokens = mutableListOf<String>()
        val buffer = StringBuilder()
        var i = 0

        while (i < exp.length) {
            val c = exp[i]
            if (c.isWhitespace()) {
                i++
                continue
            }

            if (i + 1 < exp.length) {
                val twoChar = exp.substring(i, i + 2)
                if (twoChar in LOGIC) {
                    if (buffer.isNotEmpty()) {
                        tokens.add(buffer.toString())
                        buffer.clear()
                    }
                    tokens.add(twoChar)
                    i += 2
                    continue
                }
            }

            if ("()<>!=&|".contains(c)) {
                if (buffer.isNotEmpty()) {
                    tokens.add(buffer.toString())
                    buffer.clear()
                }
                tokens.add(c.toString())
            } else {
                buffer.append(c)
            }
            i++
        }

        if (buffer.isNotEmpty()) tokens.add(buffer.toString())
        return tokens
    }
}