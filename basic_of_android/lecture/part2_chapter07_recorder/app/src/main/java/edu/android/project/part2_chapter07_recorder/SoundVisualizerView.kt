package edu.android.project.part2_chapter07_recorder

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class SoundVisualizerView(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    // 빈 값에서 Int를 전달받도록!
    var onRequestCurrentAmplitude: (() -> Int)? = null

    private val amplitudePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = context.getColor(R.color.colorPrimaryDark)
        strokeWidth = LINE_WIDTH
        // 양 옆
        strokeCap = Paint.Cap.ROUND
    }
    private var drawingWidth: Int = 0
    private var drawingHeight: Int = 0
    private var drawingAmplitudes: List<Int> = emptyList()
    // var drawingAmplitudes: List<Int> = (0..10).map { Random.nextInt(Short.MAX_VALUE.toInt()) }
    private var isReplaying: Boolean = false
    private var relayingPosition: Int = 0

    private val visualizerRepeatAction: Runnable = object : Runnable {
        override fun run() {
            if(!isReplaying) {
                // Amplitude, Draw
                val currentAmplitude = onRequestCurrentAmplitude?.invoke() ?: 0

                // 시간에 맞춰서 오른쪽부터 넣는다.
                drawingAmplitudes = listOf(currentAmplitude) + drawingAmplitudes
            } else {
                relayingPosition++
            }
            // 이걸 해야지 ondraw가 다시 호출된다
            invalidate()

            // 나를 20ms 이후에 불러주세요
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
        // canvas가 null일 경우 제외
        canvas ?: return

        val centerY = drawingHeight / 2F
        var offsetX = drawingWidth.toFloat()

        drawingAmplitudes
            .let { amplitudes ->
                if(isReplaying) {
                    // 가장 뒤에부터 position까지 받아온다
                    amplitudes.takeLast(relayingPosition)
                } else {
                    // 기존을 그대로 가져온다
                    amplitudes
                }
            }
            .forEach { amplituede ->
            val lineLength = amplituede / MAX_AMPLITUDE * drawingHeight * 0.8F

            offsetX -= LINE_SPACE
            if(offsetX < 0) return@forEach

            // 여기부터 이제 그림 그리기 !!
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
        // 두번째, 세번째 리플레이 생각하여 초기화하기
        relayingPosition = 0
        handler?.removeCallbacks(visualizerRepeatAction)
    }

    fun clearVisualization() {
        drawingAmplitudes = emptyList()
        invalidate()
    }

    companion object {
        private const val LINE_WIDTH = 10F
        private const val LINE_SPACE = 15F
        private const val MAX_AMPLITUDE = Short.MAX_VALUE.toFloat()
        private const val ACTION_INTERVAL = 20L
    }
}