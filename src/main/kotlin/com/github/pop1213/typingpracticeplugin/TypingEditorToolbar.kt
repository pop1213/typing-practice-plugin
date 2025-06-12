package com.github.pop1213.typingpracticeplugin

import com.intellij.icons.AllIcons
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.impl.EditorHeaderComponent
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.github.pop1213.typingpracticeplugin.model.TypingSession
import java.awt.Component
import java.awt.Dimension
import java.awt.FlowLayout
import java.awt.Font
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import javax.swing.*

class TypingEditorToolbar(
    editor: Editor,

    ) : EditorHeaderComponent(), TypingAction {

    private val typingSession = TypingSession { this.updateStats() }
    private val timeLabel = createValueLabel("0:00")
    private val inputLabel = createValueLabel("0")
    private val wpmLabel = createValueLabel("0")
    private val correctLabel = createValueLabel("0")
    private val accuracyLabel = createValueLabel("0.0%")
    private val startBtn = createIconButton(AllIcons.Actions.Execute, "Start");

    init {
        layout = FlowLayout(FlowLayout.LEFT)
        //preferredSize = Dimension(0, 48)
        //border = EmptyBorder(6, 12, 6, 12)
        background = JBColor.PanelBackground

        val buttonPanel = JPanel(FlowLayout(FlowLayout.LEFT, 8, 0)).apply {
            isOpaque = false
            alignmentX = Component.LEFT_ALIGNMENT
            alignmentY = Component.CENTER_ALIGNMENT

        }
        // Buttons with icons
        startBtn.apply {
            addActionListener {
                if (!typingSession.isRunning()) {
                    startTyping()
                } else {
                   stopTyping()
                }
            }
        }
        val pauseBtn = createIconButton(AllIcons.Actions.Restart, "Reset").apply {
            addActionListener {
                resetTyping()
            }
        }

        buttonPanel.add(startBtn)
        buttonPanel.add(pauseBtn)
        val statsPanel = JPanel(FlowLayout(FlowLayout.LEFT, 20, 0)).apply {
            isOpaque = false
            alignmentY = Component.CENTER_ALIGNMENT
        }

        statsPanel.add(createLabeledStat(MyBundle.message("label.text.time"), timeLabel))
        statsPanel.add(createLabeledStat(MyBundle.message("label.text.input"), inputLabel))
        statsPanel.add(createLabeledStat("WPM", wpmLabel))
        statsPanel.add(createLabeledStat(MyBundle.message("label.text.correct"), correctLabel))
        statsPanel.add(createLabeledStat(MyBundle.message("label.text.accuracy"), accuracyLabel))

        add(buttonPanel)
        add(statsPanel)
        //editor.putUserData(TYPING_STATS_PANEL_KEY, this)

        editor.contentComponent.addFocusListener(object : FocusAdapter() {
            override fun focusLost(e: FocusEvent?) {
               if(e?.oppositeComponent!=startBtn){
                   stopTyping()
               }
            }
        })

        editor.putUserData(TYPING_ACTION,this)
    }


    fun updateStats() {
        SwingUtilities.invokeLater {
            timeLabel.text = formatTime(typingSession.stats.elapsedSeconds)
            inputLabel.text = typingSession.stats.inputCount.toString()
            correctLabel.text = typingSession.stats.correctCount.toString()
            wpmLabel.text = typingSession.stats.wpm.toString()
            accuracyLabel.text = String.format("%.1f%%", 100.0 * typingSession.stats.accuracy)
        }
    }


    // === Component Factories ===

    private fun createIconButton(icon: Icon, tooltip: String, name: String? = null): JButton {
        return JButton(icon).apply {
            this.name = name ?: tooltip
            toolTipText = tooltip
            isFocusPainted = false
            isBorderPainted = false
            isContentAreaFilled = false
            horizontalAlignment = SwingConstants.LEFT
            preferredSize = Dimension(icon.iconWidth + 4, icon.iconHeight)
        }
    }

    private fun createValueLabel( value:String): JBLabel {
        return JBLabel(value).apply {
            font = font.deriveFont(Font.BOLD, 13f)
        }
    }

    private fun createLabeledStat(labelText: String, valueLabel: JBLabel): JPanel {
        val label = JBLabel("$labelText:").apply {
            foreground = JBColor.GRAY
            font = font.deriveFont(Font.PLAIN, 12f)
        }
        return JPanel(FlowLayout(FlowLayout.LEFT, 4, 0)).apply {
            isOpaque = false
            add(label)
            add(valueLabel)
        }
    }

    private fun formatTime(seconds: Long): String {
        val m = seconds / 60
        val s = seconds % 60
        return "%d:%02d".format(m, s)
    }

    override fun startTyping() {
        startBtn.name = "Pause"
        startBtn.toolTipText = "Pause"
        startBtn.icon = AllIcons.Actions.Pause
        //startSession()
        typingSession.start();
        updateStats()
    }

    override fun stopTyping() {
        startBtn.name = "Start"
        startBtn.toolTipText = "Start"
        startBtn.icon = AllIcons.Actions.Execute
        //startSession()
        typingSession.stop();
        updateStats()
    }

    override fun resetTyping() {
        typingSession.restart();
        updateStats()
    }
    override fun onInput(isCorrect: Boolean) {
        typingSession.stats.onInput(isCorrect)
        updateStats()
    }

    override val isTyping: Boolean
        get() = typingSession.isRunning()
}