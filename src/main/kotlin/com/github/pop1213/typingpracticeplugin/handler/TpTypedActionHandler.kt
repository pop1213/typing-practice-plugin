package com.github.pop1213.typingpracticeplugin.handler

import com.github.pop1213.typingpracticeplugin.HighlightHelper
import com.github.pop1213.typingpracticeplugin.TYPING_ACTION
import com.github.pop1213.typingpracticeplugin.action.TP_EDITOR_KEY
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actionSystem.TypedActionHandler
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.util.TextRange


class TpTypedActionHandler(private val originRawHandler: TypedActionHandler) : TypedActionHandler {
    override fun execute(editor: Editor, c: Char, dataContext: DataContext) {
        if (editor.getUserData(TP_EDITOR_KEY) == true) {
            handleCharTyped(editor as EditorEx, c)
        } else {
            originRawHandler.execute(editor, c, dataContext)
        }
    }

    private fun handleCharTyped(editor: EditorEx, typedChar: Char) {
        //未开始按任意键开始
        //not null

        val typingAction = editor.getUserData(TYPING_ACTION)
        if (typingAction?.isTyping != true) {
            typingAction?.startTyping()
            return
        }
        val offset = editor.caretModel.currentCaret.offset
        val charInDoc = editor.document.getText(TextRange.create(offset, offset + 1))

        if (charInDoc == typedChar.toString()) {
            HighlightHelper.createGreenHighlight(editor, offset, offset + 1)
            editor.caretModel.moveToOffset(offset + 1)
            typingAction.onInput(true)
        } else {
            HighlightHelper.createRedHighlight(editor, offset, offset + 1)
            typingAction.onInput(false)
        }
    }
}
