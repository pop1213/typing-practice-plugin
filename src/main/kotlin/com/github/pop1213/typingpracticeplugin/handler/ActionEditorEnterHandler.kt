package com.github.pop1213.typingpracticeplugin.handler

import com.github.pop1213.typingpracticeplugin.HighlightHelper
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
        if(!isCommentOrWhiteSpace(findElementAt,4)){
            val currentChar = editor.document.getText(TextRange.create(offset, offset + 1))[0]
            if (currentChar.code != 10) {
                typingAction.onInput(false)
                HighlightHelper.createRedHighlight(editor, offset, offset + 1)
            }else{
                typingAction.onInput(true)
                HighlightHelper.removeHighlight(editor, offset, offset + 1)
            }
        }else{
            editor.caretModel.moveToOffset(findNexTypingElementStartOffset(psiFile!!, offset))
            editor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
        }
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
            if ((element is PsiWhiteSpace
                        || element is PsiComment) || isDocumentCommentByRecursive(element, 4)
            ) {
                offset = element.textRange.endOffset
                continue
            }
            // 找到非注释、非空白的 PSI 元素
            return element.textRange.startOffset
        }
        return fileLength
    }

    private fun isCommentOrWhiteSpace(element: PsiElement?,depth: Int): Boolean {
       return element is PsiWhiteSpace
                || element is PsiComment || isDocumentCommentByRecursive(element, 4)
    }
    //递归查找是否为文档注释
    private fun isDocumentCommentByRecursive(element: PsiElement?,depth: Int): Boolean {
        if(element == null){
            return false
        }
        if(element.elementType.toString().uppercase().contains("DOC")){
            return true
        }

        if(depth <= 1){
            return element.elementType.toString().uppercase().contains("DOC")
        }
        return element.parents(false).any{
            isDocumentCommentByRecursive(it,depth-1)
        }
    }

}