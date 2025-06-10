package com.github.pop1213.typingpracticeplugin.model

data class TypingStats(
    var inputCount: Int = 0,
    var correctCount: Int = 0,
    var elapsedSeconds: Long = 0
) {
    val accuracy: Double
        get() {
            // 计算准确率：正确字符数 / 总输入字符数，若总输入为0则返回1.0（避免除以零）
            return if (inputCount == 0) 0.0 else correctCount.toDouble() / inputCount
        }
    val wpm: Int get() = if (elapsedSeconds == 0L) 0 else (correctCount / 5.0 / (elapsedSeconds / 60.0)).toInt()
    fun timerTick() {
        elapsedSeconds++
    }
    fun reset() {
        inputCount = 0
        correctCount = 0
        elapsedSeconds = 0
    }

    fun onInput(isCorrect: Boolean) {
        inputCount++
        if (isCorrect) {
            correctCount++
        }
    }
}
