package uk.ac.bournemouth.ap.battleships

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import org.example.student.battleshipgame.StudentBattleshipGrid
import org.example.student.battleshipgame.StudentBattleshipOpponent
import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid
import kotlin.math.min
import kotlin.random.Random


var View.defaultFocusHighlightEnabledCompat: Boolean
    get() = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        -> Api26Compat.defaultFocusHighlightEnabled(this)

        else -> false
    }
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Api26Compat.setDefaultFocusHighlightEnabled(this, value)
        }
    }

@RequiresApi(Build.VERSION_CODES.O)
private object Api26Compat {
    fun defaultFocusHighlightEnabled(view: View): Boolean {
        return view.defaultFocusHighlightEnabled
    }

    fun setDefaultFocusHighlightEnabled(view: View, value: Boolean) {
        view.defaultFocusHighlightEnabled=value
    }

}

class ViewCompatX: View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var grid: StudentBattleshipGrid = StudentBattleshipGrid(
        BattleshipGrid.DEFAULT_COLUMNS, BattleshipGrid.DEFAULT_ROWS, StudentBattleshipOpponent(
            BattleshipGrid.DEFAULT_COLUMNS,
            BattleshipGrid.DEFAULT_ROWS,
            BattleshipGrid.DEFAULT_SHIP_SIZES,
            Random

        )
    )
        set(value) {
            // unregister listener from existing
            field = value
            // register listener to new
            recalculateDimensions()
            invalidate()
        }

    private var circleSize: Float = 0f
    private val gridPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        color = Color.BLACK
    }
    private val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 50f
        color = Color.BLACK
    }

    private val shipPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.GREEN
    }

    private var squareSize: Float = 0f
    private var textSize: Float = 0f
    private var labelPadding: Float = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        recalculateDimensions(w, h)
    }

    private fun recalculateDimensions(w: Int = width, h: Int = height) {
        val minSize = min(w, h)
        squareSize =
            (minSize / maxOf(grid.columns, grid.rows) + 1).toFloat() // 10 squares per row/column
        textSize = squareSize / 2
        labelPadding = textSize / 5
        textPaint.textSize = textSize
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw game board background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), gridPaint)

        // Draw first grid
        val squareSize1 = squareSize
        for (i in 0 until grid.columns) {
            for (j in 0 until grid.rows) {
                val x = (i * squareSize1) + squareSize1
                val y = (j * squareSize1) + squareSize1
                val rectF = RectF(x, y, x + squareSize1, y + squareSize1)
                canvas.drawRect(rectF, gridPaint)
            }
        }

        // Draw text labels
        for (i in 1..grid.columns) {
            // draw vertical labels (letters)
            val label = ('A' + i - 1).toString()
            canvas.drawText(
                label,
                (i * squareSize1) + (squareSize1 / 2) - (textPaint.measureText(label) / 2),
                squareSize1 - (textPaint.descent() + textPaint.ascent()) / 5 - labelPadding,
                textPaint
            )

            // draw horizontal labels (numbers)
            canvas.drawText(
                i.toString(),
                squareSize1 - (textPaint.measureText(i.toString()) / 1) - labelPadding,
                (i * squareSize1) + (squareSize1 / 2) - (textPaint.descent() + textPaint.ascent()) / 2,
                textPaint
            )
        }

        // Draw space between the two grids
        val space = squareSize1 * 11f

        // Draw second grid
        val squareSize2 = squareSize1 / 1.5f
        for (i in 0 until grid.columns) {
            for (j in 0 until grid.rows) {
                val x = (i * squareSize2) + (squareSize1 * 1.5f)
                val y = (j * squareSize2) + squareSize1 + space
                val rectF = RectF(x, y, x + squareSize2, y + squareSize2)
                canvas.drawRect(rectF, gridPaint)
            }
        }

        // Draw text labels for second grid
        for (i in 1..grid.columns) {
            // draw vertical labels (letters)
            val label = ('A' + i - 1).toString()
            canvas.drawText(
                label,
                (i * squareSize2) - (squareSize2 / 2) - (textPaint.measureText(label) / 2) + (squareSize1 * 1.5f),
                squareSize1 + space - (textPaint.descent() + textPaint.ascent()) / 5 - labelPadding,
                textPaint
            )

            // draw horizontal labels (numbers)
            canvas.drawText(
                i.toString(),
                squareSize1 * 1.5f - (textPaint.measureText(i.toString()) / 1) - labelPadding,
                (i * squareSize2) + squareSize1 + space + (squareSize2 / 2) - (textPaint.descent() + textPaint.ascent()) / 2,
                textPaint
            )
        }

    }
}









































































/*
class ViewCompatX: View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var grid: StudentBattleshipGrid = StudentBattleshipGrid(BattleshipGrid.DEFAULT_COLUMNS, BattleshipGrid.DEFAULT_ROWS, StudentBattleshipOpponent(
        BattleshipGrid.DEFAULT_COLUMNS,
        BattleshipGrid.DEFAULT_ROWS,
        BattleshipGrid.DEFAULT_SHIP_SIZES,
        Random

    ))
        set(value) {
            // unregister listener from existing
            field  = value
            // register listener to new
            recalculateDimensions()
            invalidate()
        }

    private var circleSize: Float = 0f
    private val gridPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 5f
        color = Color.BLACK
    }
    private val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 50f
        color = Color.BLACK
    }

    private val shipPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.GREEN
    }

    private var squareSize: Float = 0f
    private var textSize: Float = 0f
    private var labelPadding: Float = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        recalculateDimensions(w, h)
    }

    private fun recalculateDimensions(w: Int = width, h: Int = height) {
        val minSize = min(w, h)
        squareSize = (minSize / maxOf(grid.columns, grid.rows)+1).toFloat() // 10 squares per row/column
        textSize = squareSize / 2
        labelPadding = textSize / 5
        textPaint.textSize = textSize
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw game board background
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), gridPaint)

        // Draw first grid
        val squareSize1 = squareSize
        for (i in 0 until grid.columns) {
            for (j in 0 until grid.rows) {
                val x = (i * squareSize1) + squareSize1
                val y = (j * squareSize1) + squareSize1
                val rectF = RectF(x, y, x + squareSize1, y + squareSize1)
                canvas.drawRect(rectF, gridPaint)
            }
        }

        // Draw text labels
        for (i in 1..grid.columns) {
            // draw vertical labels (letters)
            val label = ('A' + i - 1).toString()
            canvas.drawText(
                label,
                (i * squareSize1) + (squareSize1 / 2) - (textPaint.measureText(label) / 2),
                squareSize1 - (textPaint.descent() + textPaint.ascent()) / 5 - labelPadding,
                textPaint
            )

            // draw horizontal labels (numbers)
            canvas.drawText(
                i.toString(),
                squareSize1 - (textPaint.measureText(i.toString()) / 1) - labelPadding,
                (i * squareSize1) + (squareSize1 / 2) - (textPaint.descent() + textPaint.ascent()) / 2,
                textPaint
            )
        }

        // Draw space between the two grids
        val space = squareSize1 * 11f

        // Draw second grid
        val squareSize2 = squareSize1 / 1.5f
        for (i in 0 until grid.columns) {
            for (j in 0 until grid.rows) {
                val x = (i * squareSize2) + (squareSize1 * 1.5f)
                val y = (j * squareSize2) + squareSize1 + space
                val rectF = RectF(x, y, x + squareSize2, y + squareSize2)
                canvas.drawRect(rectF, gridPaint)
            }
        }

        // Draw text labels for second grid
        for (i in 1..grid.columns) {
            // draw vertical labels (letters)
            val label = ('A' + i - 1).toString()
            canvas.drawText(
                label,
                (i * squareSize2) - (squareSize2 / 2) - (textPaint.measureText(label) / 2) + (squareSize1 * 1.5f),
                squareSize1 + space - (textPaint.descent() + textPaint.ascent()) / 5 - labelPadding,
                textPaint
            )

            // draw horizontal labels (numbers)
            canvas.drawText(
                i.toString(),
                squareSize1 * 1.5f - (textPaint.measureText(i.toString()) / 1) - labelPadding,
                (i * squareSize2) + squareSize1 + space + (squareSize2 / 2) - (textPaint.descent() + textPaint.ascent()) / 2,
                textPaint
            )
        }
        for (i in 0 until grid.columns) {
            for (j in 0 until grid.rows) {
                val x = (i * squareSize1) + squareSize1
                val y = (j * squareSize1) + squareSize1
                val rectF = RectF(x, y, x + squareSize1, y + squareSize1)
                canvas.drawRect(rectF, gridPaint)

                // Draw ship if cell is occupied
                if (grid.getCell(i, j).isOccupied) {
                    canvas.drawRect(rectF, shipPaint)
                }
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val column = (event.x / squareSize1).toInt()
            val row = (event.y / squareSize1).toInt()
            grid.handleUserInput(column, row)
            invalidate()
            return true
        }
        return super.onTouchEvent(event)
    }
}
*/