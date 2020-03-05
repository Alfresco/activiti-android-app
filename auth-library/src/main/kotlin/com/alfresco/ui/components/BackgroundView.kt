package com.alfresco.ui.components

import android.content.Context
import android.graphics.Matrix
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.Nullable


// TODO: View should support multiple alignments
public class BackgroundView(context: Context?, @Nullable attrs: AttributeSet?, defStyleAttr: Int,
                            defStyleRes: Int) : ImageView(context, attrs, defStyleAttr, defStyleRes) {
    protected override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val d: Drawable? = this.drawable
        if (d != null) {
            val matrix = Matrix()
            val src = RectF(0F, 0F, d.intrinsicWidth.toFloat(), d.intrinsicHeight.toFloat())

            // Align left
            matrix.setTranslate((d.intrinsicWidth - w) / 2F, 0F)

            // Scale to fill
            if (h > w) {
                val yscale = h / d.intrinsicHeight.toFloat()
                matrix.setScale(yscale, yscale)
            } else {
                val xscale = w / d.intrinsicWidth.toFloat()
                matrix.setScale(xscale, xscale)
            }
            imageMatrix = matrix
        }
    }

    constructor(context: Context?) : this(context, null, 0, 0) {

    }

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0, 0) {
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, 0, 0) {
    }

    init {
        scaleType = ScaleType.MATRIX
    }
}
