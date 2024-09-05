package com.example.soundmixer.utils
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class AudioVisualizerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val paint = Paint().apply {
        color = Color.GREEN
        strokeWidth = 2f
    }

    private var audioData: ByteArray? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        audioData?.let {
            drawVisualizer(canvas, it)
        }
    }

    private fun drawVisualizer(canvas: Canvas, data: ByteArray) {
        val width = width
        val height = height
        val step = width / data.size
        val halfHeight = height / 2

        canvas.drawColor(Color.BLACK) // Background color

        for (i in data.indices) {
            val amplitude = ((data[i].toInt() and 0xFF) - 128) * 2
            val x = i * step
            val y = halfHeight - amplitude
            canvas.drawLine(x.toFloat(), halfHeight.toFloat(), x.toFloat(), y.toFloat(), paint)
        }
    }

    fun setAudioData(data: ByteArray) {
        audioData = data
        invalidate()
    }
}
