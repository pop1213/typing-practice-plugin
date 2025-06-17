package com.github.pop1213.typingpracticeplugin

import com.intellij.openapi.util.Key

internal var TYPING_ACTION = Key<TypingAction>("TYPING_ACTION")
interface TypingAction {
    fun startTyping()
    fun stopTyping()
    fun resetTyping()
    fun onInput(isCorrect: Boolean)
    val isTyping: Boolean
}
