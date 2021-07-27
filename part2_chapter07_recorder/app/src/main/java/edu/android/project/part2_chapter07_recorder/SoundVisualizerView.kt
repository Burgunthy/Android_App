package edu.android.project.part2_chapter07_recorder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SoundVisualizerView(
    context: Context,
    attrs : AttributeSet
): View(context, attrs) {

    var onRequestCurrentAmplitude: (() -> Int)? = null

    private var drawingWidth: Int = 0
    private var drawingHeight: Int = 0
    private var drawingAmplitudes: List<Int> = emptyList()

    private var isReplaying: Boolean = false
    private var relayingPosition: Int = 0

    private val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.colorPrimaryDark)
        strokeWidth = LINE_WIDTH
        strokeCap = Paint.Cap.ROUND
    }

    private val visualizerRepeatAction: Runnable = object : Runnable {
        override fun run() {
            if(!isReplaying) {
                // 이 함수를 호출할게!
                val currentAmplitude = onRequestCurrentAmplitude?.invoke() ?: 0

                // 왜 왼쪽부터 넣는 것 같지...
                drawingAmplitudes = listOf(currentAmplitude) + drawingAmplitudes
            } else {
                relayingPosition++
            }
            // onDraw 부르기
            invalidate()

            handler?.postDelayed(this, ACTION_INTERVAL)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawingWidth = w
        drawingHeight = h
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas ?: return

        val centerY = drawingHeight / 2F
        var offsetX = drawingWidth.toFloat()

        drawingAmplitudes
            .let { amplitudes ->
                if(isReplaying) {
                    amplitudes.takeLast(relayingPosition)
                } else {
                    amplitudes
                }
            }
            .forEach { amplitude ->
                val lineLength = amplitude / MAX_AMPLITUDE * drawingHeight + 0.8F

                offsetX -= LINE_SPACE
                if(offsetX < 0) return@forEach

                canvas.drawLine(
                    offsetX,
                    centerY - lineLength / 2F,
                    offsetX,
                    centerY + lineLength / 2F,
                    amplitudePaint
                )
            }
    }

    fun startVisualizing(isReplaying: Boolean) {
        this.isReplaying = isReplaying
        handler?.post(visualizerRepeatAction)
    }

    fun stopVisualizing() {
        relayingPosition = 0
        handler?.removeCallbacks(visualizerRepeatAction)
    }

    fun clearVisualization() {
        drawingAmplitudes = emptyList()
        invalidate()
    }

    companion object {
        private const val ACTION_INTERVAL = 20L

        private const val LINE_WIDTH = 10F
        private const val LINE_SPACE = 15F

        private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat()
    }
}