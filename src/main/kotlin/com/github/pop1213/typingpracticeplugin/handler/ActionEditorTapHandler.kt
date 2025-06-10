package com.github.pop1213.typingpracticeplugin.handler

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.psi.codeStyle.CodeStyleSettingsManager
import com.github.pop1213.typingpracticeplugin.action.TP_EDITOR_KEY

class ActionEditorTapHandler(private val originHandler: EditorActionHandler) : EditorActionHandler() {
    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
        if (editor.getUserData(TP_EDITOR_KEY) == true) {
            //根据当前 的indent
            val fileSettings = CodeStyleSettingsManager.getInstance().localSettings
            val indentOptions = fileSettings?.getIndentOptions(editor.virtualFile.fileType)

            val tabSize = indentOptions?.TAB_SIZE
            caret?.caretModel?.moveToOffset((caret?.caretModel?.offset ?: 0) + tabSize!!)

        } else {
            originHandler.execute(editor, caret, dataContext)
        }
    }
}