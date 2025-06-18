package com.github.pop1213.typingpracticeplugin

import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement
import com.intellij.psi.util.elementType
import com.intellij.psi.util.parents

class PsiElementUtils {
    companion object {
        fun isComment(element: PsiElement?,depth: Int): Boolean {
            return element is PsiComment || isDocumentCommentByRecursive(element, depth)
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
}