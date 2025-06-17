package com.github.pop1213.typingpracticeplugin.listener

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.colors.EditorFontType
import com.intellij.openapi.editor.event.CaretEvent
import com.intellij.openapi.editor.event.CaretListener
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.HighlighterTargetArea
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.ui.JBColor
import java.awt.Font
import java.awt.Graphics

class TpCaretListener: CaretListener{

    private var highlighter: RangeHighlighter? = null
    override fun caretPositionChanged(event: CaretEvent) {
        highlighter?.dispose()
        //获取当前光标位置字符
        val currOffset =  event.editor.caretModel.offset
        if(currOffset==event.editor.document.charsSequence.length){
            return
        }
        val currChar = event.editor.document.charsSequence[currOffset]
        if (currChar.code == 10) {
            showCharAtCaretPosition(event.editor, '⏎')
        }else if (currChar.code == 32){
            showCharAtCaretPosition(event.editor, '␣')
        }
    }

    private fun showCharAtCaretPosition(editor: Editor, c: Char) {
        val markupModel = editor.markupModel
        val caretOffset: Int = editor.caretModel.offset
        highlighter = markupModel.addRangeHighlighter(
            caretOffset, caretOffset,
            HighlighterLayer.SELECTION + 1,
            null,
            HighlighterTargetArea.EXACT_RANGE
        )
        highlighter?.setCustomRenderer { p0: Editor, _: RangeHighlighter, g: Graphics ->
            //获取当前光标坐标
            val visualPosition = editor.caretModel.visualPosition
            val pos = editor.visualPositionToXY(visualPosition)

            //获取当前编辑器的字体
            val font: Font = editor.colorsScheme.getFont(EditorFontType.PLAIN)
            g.font = font
            //和当前行对齐
            g.color = JBColor.RED

            val ascent = p0.ascent

            g.drawString(c.toString(), pos.x, pos.y + ascent)
        }


    }
}
