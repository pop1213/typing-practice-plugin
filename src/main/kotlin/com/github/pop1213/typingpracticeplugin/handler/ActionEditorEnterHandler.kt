package com.github.pop1213.typingpracticeplugin.handler

import com.github.pop1213.typingpracticeplugin.HighlightHelper
import com.github.pop1213.typingpracticeplugin.PsiElementUtils
import com.github.pop1213.typingpracticeplugin.TYPING_ACTION
import com.github.pop1213.typingpracticeplugin.action.TP_EDITOR_KEY
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Caret
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.editor.actionSystem.EditorActionHandler
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.util.TextRange
import com.intellij.psi.*
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parents

class ActionEditorEnterHandler(private val originHandler: EditorActionHandler) : EditorActionHandler() {
    override fun doExecute(editor: Editor, caret: Caret?, dataContext: DataContext?) {
        if (editor.getUserData(TP_EDITOR_KEY) == true) {
            handleEnterKey(editor as EditorEx)
        } else {
            originHandler.execute(editor, caret, dataContext)
        }
    }
    private fun handleEnterKey(editor: EditorEx) {

        val typingAction = editor.getUserData(TYPING_ACTION)
        //没有开始按enter键开始
        if (typingAction?.isTyping != true) {
            typingAction?.startTyping()
            return
        }

        val offset = editor.caretModel.currentCaret.offset
        //如果打完所有行，按回车自动结束
        if (offset >= editor.document.textLength) {
            typingAction.stopTyping()
            return
        }
        val psiFile = PsiManager.getInstance(editor.project!!).findFile(editor.virtualFile)
        var findElementAt = psiFile?.findElementAt(offset)
        val typedCorrect = editor.document.getText(TextRange.create(offset, offset + 1))[0].code==10

        //只要是在注释中就一律跳过
        //如果不是注释中，正确输入，清除高亮，跳过，否则显示错误高亮
        if(PsiElementUtils.isComment(findElementAt,4)){
            skipToNextTypingElement(editor, psiFile!!, offset)
        }else{
            typingAction.onInput(typedCorrect)
            if(typedCorrect){
                HighlightHelper.removeHighlight(editor, offset, offset + 1)
                skipToNextTypingElement(editor, psiFile!!, offset)
            }else{
                HighlightHelper.createRedHighlight(editor, offset, offset + 1)
            }
        }
    }

    private fun skipToNextTypingElement(editor: EditorEx, psiFile: PsiFile, offset: Int) {
        editor.caretModel.moveToOffset(findNexTypingElementStartOffset(psiFile, offset))
        editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
    }

    private fun findNexTypingElementStartOffset(psiFile: PsiFile, startOffset: Int): Int {
        var offset = startOffset
        val fileLength = psiFile.textLength

        while (offset < fileLength) {
            val element = psiFile.findElementAt(offset)
            if (element == null) {
                offset++
                continue
            }
            // 跳过注释和空白
            if ((element is PsiWhiteSpace || PsiElementUtils.isComment(element, 4))) {
                offset = element.textRange.endOffset
                continue
            }
            // 找到非注释、非空白的 PSI 元素
            return element.textRange.startOffset
        }
        return fileLength
    }



}