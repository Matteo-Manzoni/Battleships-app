package uk.ac.bournemouth.ap.battleships


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import org.example.student.battleshipgame.StudentBattleshipGrid
import org.example.student.battleshipgame.StudentBattleshipOpponent
import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid
import kotlin.random.Random
import uk.ac.bournemouth.ap.battleshiplib.GuessResult


class OpponentView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )



    private val opponentGridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
    }

    var opponentGrid: StudentBattleshipGrid = StudentBattleshipGrid(
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


    private val colCount: Int get() = opponentGrid.columns
    private val rowCount: Int get() = opponentGrid.rows
    private val squareSpacingRatio = 0.1f

    private var squareSize = 0f
    private var squareSpacing = 0f


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        recalculateDimensions()
    }

    private fun recalculateDimensions() {


        val sizeX = width / (colCount + (colCount + 1) * squareSpacingRatio)
        val sizeY = height / (rowCount + (rowCount + 1) * squareSpacingRatio)
        squareSize = minOf(sizeX, sizeY)
        squareSpacing = squareSize * squareSpacingRatio
    }
    private var cachedCanvas: Canvas? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        cachedCanvas = canvas
        val letters = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
        val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val labelPaint = Paint().apply {
            color = Color.BLACK
            textSize = 40f
            textAlign = Paint.Align.CENTER
        }

        // Draw numbers on the left side of the grid
        for (i in 0 until rowCount) {
            val number = numbers[i]
            val x = squareSpacing
            val y = squareSpacing + ((squareSize + squareSpacing) * i) + (squareSize ) - ((labelPaint.descent() + labelPaint.ascent()) / 2)
            canvas?.drawText(number, x, y, labelPaint)
        }

        // Draw letters above the grid
        for (i in 0 until colCount) {
            val letter = letters[i]
            val x = squareSpacing + ((squareSize + squareSpacing) * i) + (squareSize / 2f)
            val y = squareSpacing + labelPaint.textSize
            canvas?.drawText(letter, x, y, labelPaint)
        }

        // Draw the grid and cells
        for (row in 0 until rowCount) {
            for (col in 0 until colCount) {
                val left = squareSpacing + ((squareSize + squareSpacing) * col)
                val top = squareSpacing + ((squareSize + squareSpacing) * row)
                val right = left + squareSize
                val bottom = top + squareSize
                opponentGridPaint.color = cellColors[row][col]
                canvas?.drawRect(left, top, right, bottom, opponentGridPaint)
            }
        }
        val crossPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 8f
            color = Color.RED
        }
        for (row in 0 until rowCount) {
            for (col in 0 until colCount) {
                val left = squareSpacing + ((squareSize + squareSpacing) * col)
                val top = squareSpacing + ((squareSize + squareSpacing) * row)
                val right = left + squareSize
                val bottom = top + squareSize
                opponentGridPaint.color = cellColors[row][col]
                canvas?.drawRect(left, top, right, bottom, opponentGridPaint)
                if (cellsAttacked[row][col]) {
                    canvas?.drawLine(left, top, right, bottom, crossPaint)
                    canvas?.drawLine(right, top, left, bottom, crossPaint)
                }
            }
        }
    }

    private val cellsAttacked = Array(rowCount) { BooleanArray(colCount) }







    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {


                val x = event.x
                val y = event.y

                val row = (y / (squareSize + squareSpacing)).toInt()
                val col = (x / (squareSize + squareSpacing)).toInt()

                // Check cell attacked
                if (cellColors[row][col] != Color.BLACK) {
                    Toast.makeText(context, "Can not attack same square twice", Toast.LENGTH_SHORT).show()
                    return true
                }

                try {
                    val guessResult = opponentGrid.shootAt(row, col)
                    UIForGuessResult(row, col, guessResult)

                    cellsAttacked[row][col] = true

                    if (opponentGrid.isFinished) {
                        // Should work with game over
                    } else {
                        attackListener?.attack()
                    }

                    // Draw a cross on the cell that was attacked
                    val crossPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                        style = Paint.Style.STROKE
                        strokeWidth = 8f
                        color = Color.RED
                    }
                    val left = squareSpacing + ((squareSize + squareSpacing) * col)
                    val top = squareSpacing + ((squareSize + squareSpacing) * row)
                    val right = left + squareSize
                    val bottom = top + squareSize
                    cachedCanvas?.drawLine(left, top, right, bottom, crossPaint)
                    cachedCanvas?.drawLine(right, top, left, bottom, crossPaint)
                } catch (e: Exception) {
                    Toast.makeText(context, "Duplicate move", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return true
    }

    private val cellColors = Array(rowCount) { Array(colCount) { Color.BLACK } }

    interface AttackListener {
        fun attack()
    }

    var attackListener: OpponentView.AttackListener? = null



    private fun UIForGuessResult(row: Int, col: Int, guessResult: GuessResult) {
        val color = when (guessResult) {
            is GuessResult.HIT -> Color.RED
            GuessResult.MISS -> Color.BLUE
            is GuessResult.SUNK -> Color.YELLOW
            else -> Color.BLACK
        }

        cellColors[row][col] = color
        if (color != Color.BLACK) {
            cellColors[row][col] = color
            UIForGuessResultWithCross(row, col, color)
        } else {
            invalidate()
        }
    }

    private fun UIForGuessResultWithCross(row: Int, col: Int, color: Int) {
        val left = squareSpacing + ((squareSize + squareSpacing) * col)
        val top = squareSpacing + ((squareSize + squareSpacing) * row)
        val right = left + squareSize
        val bottom = top + squareSize

        val crossSize = squareSize / 2f
        val crossPaint = Paint().apply {
            strokeWidth = 10f
            this.color = color
        }

        cachedCanvas?.drawLine(
            left + crossSize, top + crossSize,
            right - crossSize, bottom - crossSize,
            crossPaint
        )
        cachedCanvas?.drawLine(
            left + crossSize, bottom - crossSize,
            right - crossSize, top + crossSize,
            crossPaint
        )

        invalidate()
    }
}

