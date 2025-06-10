package com.github.pop1213.typingpracticeplugin.handler

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.github.pop1213.typingpracticeplugin.action.TP_EDITOR_KEY

class DoNothingActionHandler(private val originHandler: EditorActionHandler) : EditorActionHandler() {
    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
        if (editor.getUserData(TP_EDITOR_KEY) == true) {
          //  do nothing
        } else {
            originHandler.execute(editor, caret, dataContext)
        }
    }
}