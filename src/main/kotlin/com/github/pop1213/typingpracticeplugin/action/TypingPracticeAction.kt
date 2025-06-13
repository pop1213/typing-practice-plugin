package com.github.pop1213.typingpracticeplugin.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.TextEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.testFramework.LightVirtualFile
import com.intellij.ui.JBColor
import com.github.pop1213.typingpracticeplugin.TypingEditorToolbar
import com.github.pop1213.typingpracticeplugin.listener.TpCaretListener
import com.intellij.openapi.editor.ScrollType
import com.intellij.openapi.editor.ex.EditorEx
import java.awt.Font
import java.awt.Graphics
import java.io.IOException
internal val TP_EDITOR_KEY = Key.create<Boolean>("TP_EDITOR_KEY")
class TypingPracticeAction : AnAction() {
    private  var highlighter: RangeHighlighter? = null
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = e.getData(PlatformDataKeys.EDITOR) ?: return
        val file = e.getData(PlatformDataKeys.VIRTUAL_FILE) ?: return

        if (!isValidSourceFile(file)) {
            Messages.showInfoMessage(project, "请选择有效的源码文件", "提示")
            return
        }

        openTypingPracticeEditor(project, editor, file)
    }

    private fun openTypingPracticeEditor(project: Project, originalEditor: Editor, originalFile: VirtualFile) {
        // 获取原始文件内容
        val originalText = originalEditor.document.text

        // 在写操作中创建和修改文件
        ApplicationManager.getApplication().runWriteAction {
            try {
                val tempFileName = "${originalFile.nameWithoutExtension}.${originalFile.extension}"
                val tempFile = LightVirtualFile(tempFileName, originalText)
                val fileEditorManager = FileEditorManager.getInstance(project)
                val typingEditors = fileEditorManager.openFile(tempFile, true)

                if (typingEditors.isNotEmpty() && typingEditors[0] is TextEditor) {
                    val typingEditor = (typingEditors[0] as TextEditor).editor
                    (typingEditor as EditorEx)?.isViewer = true
                    //focus
                    //val editor = FileEditorManager.getInstance(project).setSelectedEditor(tempFile)
                    typingEditor.caretModel.moveToOffset(0)
                    typingEditor.scrollingModel.scrollToCaret(ScrollType.MAKE_VISIBLE)
                    typingEditor.putUserData(TP_EDITOR_KEY, true)
                    typingEditor.headerComponent = TypingEditorToolbar(typingEditor)
                    //在空格和回车处添加提示符号
                    typingEditor.caretModel.addCaretListener(TpCaretListener())
                }

            } catch (ex: IOException) {
                Messages.showErrorDialog(project, "创建打字练习文件失败: ${ex.message}", "错误")
            }
        }
    }

    private fun isValidSourceFile(file: VirtualFile): Boolean {
        val fileType = file.fileType
        return !fileType.isBinary
    }
    override fun update(e: AnActionEvent) {
        val project = e.project
        val editor = e.getData(PlatformDataKeys.EDITOR)
        e.presentation.isEnabledAndVisible =
            project != null && editor != null  && editor.getUserData(
                TP_EDITOR_KEY
            )==null
    }

}