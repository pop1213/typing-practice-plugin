package com.github.pop1213.typingpracticeplugin

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.markup.EffectType
import com.intellij.openapi.editor.markup.HighlighterLayer
import com.intellij.openapi.editor.markup.RangeHighlighter
import com.intellij.openapi.editor.markup.TextAttributes

object HighlightHelper {
    val colorsScheme = com.intellij.openapi.editor.colors.EditorColorsManager.getInstance().globalScheme
    private val GREEN_HIGHLIGHT_KEY = TextAttributesKey.createTextAttributesKey(
        "TYPING_PRACTICE_CORRECT",
        DefaultLanguageHighlighterColors.STRING
    )

    private val RED_HIGHLIGHT_KEY = TextAttributesKey.createTextAttributesKey(
        "TYPING_PRACTICE_INCORRECT",
        DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE
    )

    init {
        // 注册自定义高亮属性
        // 正确字符高亮 - 绿色背景
        val greenAttributes = TextAttributes()
        greenAttributes.backgroundColor = java.awt.Color(0xCC, 0xFF, 0xCC)
        colorsScheme.setAttributes(GREEN_HIGHLIGHT_KEY, greenAttributes)

        // 错误字符高亮 - 红色背景和波浪线
        val redAttributes = TextAttributes()

        //e63d3d
        redAttributes.backgroundColor = java.awt.Color(0xFF, 0xCC, 0xCC)
        redAttributes.effectType = EffectType.WAVE_UNDERSCORE
        redAttributes.effectColor = java.awt.Color.RED
        colorsScheme.setAttributes(RED_HIGHLIGHT_KEY, redAttributes)
    }


    private fun switchHighlightBetweenRedAndGreen(
        editor: EditorEx,
        startOffset: Int,
        endOffset: Int,
        textAttributesKey: TextAttributesKey
    ) {
        val toRemove = mutableListOf<RangeHighlighter>()
        val toRemoveKey = if (textAttributesKey == GREEN_HIGHLIGHT_KEY) RED_HIGHLIGHT_KEY else GREEN_HIGHLIGHT_KEY
        var hasCurrentHighlighter = false
        editor.markupModel.processRangeHighlightersOverlappingWith(startOffset, endOffset) { highlighter ->
            if (highlighter.startOffset == startOffset && highlighter.endOffset == endOffset) {
                if (highlighter.textAttributesKey == toRemoveKey) {
                    toRemove.add(highlighter)
                } else if (highlighter.textAttributesKey == textAttributesKey) {
                    hasCurrentHighlighter = true
                }
            }
            true
        }

        if (toRemove.isNotEmpty()) {
            toRemove.forEach {
                editor.markupModel.removeHighlighter(it)
            }
        }
        if (!hasCurrentHighlighter) {
            val highlighter = editor.markupModel.addRangeHighlighter(
                textAttributesKey,
                startOffset,
                endOffset,
                HighlighterLayer.SELECTION - 1,
                com.intellij.openapi.editor.markup.HighlighterTargetArea.EXACT_RANGE
            )
        }
    }

    fun createGreenHighlight(editor: EditorEx, startOffset: Int, endOffset: Int) {
        switchHighlightBetweenRedAndGreen(editor, startOffset, endOffset, GREEN_HIGHLIGHT_KEY)
    }

    fun createRedHighlight(editor: EditorEx, startOffset: Int, endOffset: Int) {
        switchHighlightBetweenRedAndGreen(editor, startOffset, endOffset, RED_HIGHLIGHT_KEY)
    }

    fun removeHighlight(editor: EditorEx, startOffset: Int, endOffset: Int) {
        val toRemove = mutableListOf<RangeHighlighter>()
        editor.markupModel.processRangeHighlightersOverlappingWith(startOffset, endOffset) { highlighter ->
            if (highlighter.startOffset == startOffset && highlighter.endOffset == endOffset) {
                if (highlighter.textAttributesKey == GREEN_HIGHLIGHT_KEY || highlighter.textAttributesKey == RED_HIGHLIGHT_KEY)
                    toRemove.add(highlighter)
            }
            true
        }
        toRemove.forEach {
            editor.markupModel.removeHighlighter(it)
        }
    }

}