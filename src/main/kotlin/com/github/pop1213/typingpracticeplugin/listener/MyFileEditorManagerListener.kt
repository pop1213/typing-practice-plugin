package com.github.pop1213.typingpracticeplugin.listener

import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.fileEditor.impl.text.PsiAwareTextEditorImpl
import com.github.pop1213.typingpracticeplugin.TYPING_ACTION

class MyFileEditorManagerListener : FileEditorManagerListener {
    override fun selectionChanged(event: FileEditorManagerEvent) {
        (event.oldEditor as? PsiAwareTextEditorImpl)
            ?.editor
            ?.getUserData(TYPING_ACTION)
            ?.stopTyping()
        // super.selectionChanged(event)
    }
}