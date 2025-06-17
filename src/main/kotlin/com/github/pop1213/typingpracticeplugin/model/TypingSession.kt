package com.github.pop1213.typingpracticeplugin.model

import javax.swing.Timer

class TypingSession( private var statsListener: ((TypingStats) -> Unit)) {
    enum class Status { IDLE, RUNNING, PAUSED }
    internal   var stats: TypingStats = TypingStats()

    private var currentStatus: Status = Status.IDLE
    private var timer: Timer?=null

    //定义stats get
    fun start() {
        if (currentStatus == Status.RUNNING) return
        currentStatus = Status.RUNNING
        if(timer==null){
            timer = Timer(1000) {
                //todo
                stats.timerTick()
                statsListener(this.stats)
            }
        }
        println("Session started at: ${System.currentTimeMillis()}")

        timer?.start()
    }

    fun stop() {
        if (currentStatus != Status.RUNNING) return
        currentStatus = Status.PAUSED
        timer?.stop()
        statsListener(this.stats)
    }

    fun restart() {
        stats.reset()
        currentStatus = Status.IDLE
        timer?.stop()
        statsListener(this.stats)
    }

    fun isRunning(): Boolean {
        return currentStatus == Status.RUNNING
    }


}