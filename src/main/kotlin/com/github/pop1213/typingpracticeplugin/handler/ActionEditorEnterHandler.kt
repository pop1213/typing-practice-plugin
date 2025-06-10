package com.github.pop1213.typingpracticeplugin.handler

import com.github.pop1213.typingpracticeplugin.HighlightHelper
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorModificationUtil
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.util.TextRange
import com.github.pop1213.typingpracticeplugin.TYPING_ACTION
import com.github.pop1213.typingpracticeplugin.action.TP_EDITOR_KEY

class ActionEditorEnterHandler(private val originHandler: EditorActionHandler) : EditorActionHandler() {
    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
        if (editor.getUserData(TP_EDITOR_KEY) == true) {
            handleEnterKey(editor as EditorEx)
        } else {
            originHandler.execute(editor, caret, dataContext)
        }
    }
    private fun handleEnterKey(editor: EditorEx) {
        //没有开始按enter键开始
        var typingAction = editor.getUserData(TYPING_ACTION)
        if (typingAction?.isTyping != true) {
            typingAction?.startTyping()
            return
        }
        var offset = editor.caretModel.currentCaret.offset
        var currentChar = editor.document.getText(TextRange.create(offset, offset + 1)).get(0)
//        if(isCaretInComment(editor, offset)){
//            println(currentChar)
//        }

        if (currentChar.code != 10) {
            typingAction?.onInput(false)
            return
        }
        typingAction?.onInput(true)
        //移动光标到下一个非空白位置
        HighlightHelper.removeHighlight(editor, offset, offset + 1)
        EditorModificationUtil.deleteSelectedText(editor)
        val nextLineStartOffset =
            editor.document.getLineStartOffset(editor.caretModel.currentCaret.logicalPosition.line + 1)
        val nextLineEndOffset =
            editor.document.getLineEndOffset(editor.caretModel.currentCaret.logicalPosition.line + 1)
        val text = editor.document.getText(TextRange(nextLineStartOffset, nextLineEndOffset))
        var caretPosOffset = 0
        for (i in text.indices) {
            if (text[i].code != 32) {
                break
            }
            caretPosOffset++;
        }
        editor.caretModel.moveToOffset(nextLineStartOffset + caretPosOffset)
        EditorModificationUtil.scrollToCaret(editor)
        editor.selectionModel.removeSelection()
    }
}