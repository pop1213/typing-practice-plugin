package com.github.pop1213.typingpracticeplugin.startup

import com.intellij.openapi.actionSystem.IdeActions
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.actionSystem.EditorActionManager
import com.intellij.openapi.editor.actionSystem.TypedAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.github.pop1213.typingpracticeplugin.handler.ActionEditorEnterHandler
import com.github.pop1213.typingpracticeplugin.handler.ActionEditorTapHandler
import com.github.pop1213.typingpracticeplugin.handler.DoNothingActionHandler
import com.github.pop1213.typingpracticeplugin.handler.TpTypedActionHandler
import com.github.pop1213.typingpracticeplugin.listener.TpEditorFactoryListener

class MyProjectActivity : ProjectActivity {

    override suspend fun execute(project: Project) {

        EditorFactory.getInstance().addEditorFactoryListener(TpEditorFactoryListener(),project)

        var enterActionHandler = EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_ENTER)
        EditorActionManager.getInstance().setActionHandler(IdeActions.ACTION_EDITOR_ENTER, ActionEditorEnterHandler(enterActionHandler))

        var tapActionHandler = EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_TAB)
        EditorActionManager.getInstance().setActionHandler(IdeActions.ACTION_EDITOR_TAB, ActionEditorTapHandler(tapActionHandler))

        var backspaceActionHandler = EditorActionManager.getInstance().getActionHandler(IdeActions.ACTION_EDITOR_BACKSPACE)
        EditorActionManager.getInstance().setActionHandler(IdeActions.ACTION_EDITOR_BACKSPACE, DoNothingActionHandler(backspaceActionHandler))

        var originRawHandler = TypedAction.getInstance().rawHandler
        TypedAction.getInstance().setupRawHandler(TpTypedActionHandler(originRawHandler))


    }
}