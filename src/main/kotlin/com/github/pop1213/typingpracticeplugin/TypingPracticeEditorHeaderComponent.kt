package com.github.pop1213.typingpracticeplugin

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.impl.EditorHeaderComponent
import com.intellij.openapi.util.Key
import com.github.pop1213.typingpracticeplugin.model.TypingSession


var TP_SESSION_KEY = Key<TypingSession>("TP_SESSION_KEY");
class TypingPracticeEditorHeaderComponent (editor:Editor): EditorHeaderComponent(){
    init {

    }
}
