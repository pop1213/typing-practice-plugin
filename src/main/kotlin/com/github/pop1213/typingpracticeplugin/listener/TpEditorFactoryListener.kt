package com.github.pop1213.typingpracticeplugin.listener

import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.fileEditor.FileEditorManager
import com.github.pop1213.typingpracticeplugin.action.TP_EDITOR_KEY

class TpEditorFactoryListener: EditorFactoryListener
{
    override fun editorCreated(event: EditorFactoryEvent)
    {
        if (event.editor.getUserData(TP_EDITOR_KEY) == true) {
            val fileEditorManager = event.editor.project?.let { FileEditorManager.getInstance(it) }
            fileEditorManager?.closeFile(event.editor.virtualFile)
        }
    }
}
