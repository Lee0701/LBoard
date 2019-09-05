package io.github.lee0701.lboard.softkeyboard

import android.graphics.Paint

object KeyTextSizeAndPositionCalculator {
    
    val paint = Paint().apply {
        isAntiAlias = true
        textAlign = Paint.Align.CENTER
    }
    
    fun calculate(boundString: String, x: Int, y: Int, width: Int, height: Int): KeyTextParams {
        paint.textSize = 1f
        paint.textSize = paint.textSize * (if(width > height) width else height) / paint.measureText(boundString) / 2
        val textX = (x + width/2).toFloat()
        val textY = (y + height/2 - (paint.descent() + paint.ascent())/2)
        return KeyTextParams(textX, textY, paint.textSize)
    }
    
    data class KeyTextParams(
            val x: Float,
            val y: Float,
            val size: Float
    )
    
}
