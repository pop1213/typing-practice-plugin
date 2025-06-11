package com.github.pop1213.typingpracticeplugin.handler

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.github.pop1213.typingpracticeplugin.action.TP_EDITOR_KEY
import com.intellij.application.options.CodeStyle
import com.intellij.psi.PsiManager

class ActionEditorTapHandler(private val originHandler: EditorActionHandler) : EditorActionHandler() {
    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
        if (editor.getUserData(TP_EDITOR_KEY) == true) {
            //根据当前 的indent

            var psiFile = editor.project?.let { PsiManager.getInstance(it).findFile(editor.virtualFile) };

            val langSettings = psiFile?.let { CodeStyle.getLanguageSettings(it) }
            val indentOptions = langSettings?.indentOptions
            val tabSize = indentOptions?.TAB_SIZE
            caret?.caretModel?.moveToOffset((caret?.caretModel?.offset ?: 0) + tabSize!!)

        } else {
            originHandler.execute(editor, caret, dataContext)
        }
    }
}