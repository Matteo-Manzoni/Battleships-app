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
import org.example.student.battleshipgame.StudentShip
import uk.ac.bournemouth.ap.battleshiplib.BattleshipGrid
import uk.ac.bournemouth.ap.battleshiplib.GuessResult


class StudentPlayerView : View {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    //tried to implement button
    /*
    private lateinit var changeOrientationButton: Button

    //tried adding a button but not sure how to use veritcal to then change it.
    override fun onFinishInflate() {
        super.onFinishInflate()
        changeOrientationButton = findViewById(R.id.button)

        changeOrientationButton.setOnClickListener {
            // Change the orientation of the selected ship
            val selectedShip = playerShips.lastOrNull { !it.selected }
            selectedShip?.let {
                it.isVertical = !it.isVertical
                invalidate() // Redraw the view to reflect the new ship orientation
            }
        }
    }*/



    
    
    private val opponentGridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLUE
    }
    private val shipGridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.GREEN
    }

    private val colCount: Int get() = PlayerGrid.columns
    private val rowCount: Int get() = PlayerGrid.rows
    private val squareSpacingRatio = 0.1f
    
    var PlaceShip: Boolean = false
    private val playerShips: List<StudentShip> = listOf(
        StudentShip(0,0, 0, 1),
        StudentShip(0,0, 0, 2),
        StudentShip(0,0, 0, 2),
        StudentShip(0,0, 0, 3),
        StudentShip(0,0, 0, 4)
    )
    var userPlacedShips: MutableList<StudentShip> = mutableListOf<StudentShip>()
    
    var PlayerGrid: StudentBattleshipGrid = StudentBattleshipGrid(
        BattleshipGrid.DEFAULT_COLUMNS, BattleshipGrid.DEFAULT_ROWS, StudentBattleshipOpponent(
            BattleshipGrid.DEFAULT_COLUMNS,
            BattleshipGrid.DEFAULT_ROWS,
            userPlacedShips

        )
    )

    private var squareSize = 0f
    private var squareSpacing = 0f


    var attackListener: OpponentView.AttackListener? = null

    fun opponentAttack() {
        var validMove = false
        var randomX: Int
        var randomY: Int
        var opponentGuessResult: GuessResult

        if (PlaceShip){

            while (!validMove) {
                randomX = (0 until PlayerGrid.columns).random()
                randomY = (0 until PlayerGrid.rows).random()

                try {
                    opponentGuessResult = PlayerGrid.shootAt(randomY, randomX)
                    UIForGuessResult(randomY, randomX, opponentGuessResult)
                    validMove = true

                    if (PlayerGrid.isFinished) {
                        // Handle the game over situation
                    }
                } catch (e: Exception) {
                    // Ignore the exception and continue with the loop to find a valid move
                }
            }}
    }

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

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val letters = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J")
        val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
        val labelPaint = Paint().apply {
            color = Color.BLUE
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
            val x = squareSpacing + ((squareSize + squareSpacing) * i) + (squareSize )
            val y = squareSpacing  + labelPaint.textSize
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
        /*tried for button
        for (ship in userPlacedShips) {
            val left = squareSpacing + ((squareSize + squareSpacing) * ship.left)
            val top = squareSpacing + ((squareSize + squareSpacing) * ship.top)
            val right = left + (if (ship.isVertical) squareSize else squareSize * ship.length)
            val bottom = top + (if (ship.isVertical) squareSize * ship.length else squareSize)
            canvas?.drawRect(left, top, right, bottom, shipGridPaint)
        }*/
    }
    
    private fun UIForShipPlacement(ship:StudentShip) {
        for (col in ship.columnIndices){
            for (row in ship.rowIndices) {
                val color = shipGridPaint.color
                cellColors[row][col] = color
            }

        }

    }



    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {

            MotionEvent.ACTION_DOWN -> {

                var overlaps: Boolean
                try{
                    if (!PlaceShip) {
                        val selectedShip = playerShips.lastOrNull { !it.selected }
                        selectedShip?.let {
                            val x = event.x
                            val y = event.y
                            val size = selectedShip.size

                            val horizontalOrVert = true

                            var top = (y / (squareSize + squareSpacing)).toInt()
                            var left = (x / (squareSize + squareSpacing)).toInt()

                            if (horizontalOrVert && left + size > colCount) {
                                left = colCount - size
                            }
                            if (!horizontalOrVert && top + size > rowCount) {
                                top = rowCount - size
                            }

//                        // overlaping with any of the already placed ships
                            overlaps = userPlacedShips.any {
                                it.overlapsWith(
                                    left,
                                    top,
                                    left + if (horizontalOrVert) size - 1 else 0,
                                    top + if (horizontalOrVert) 0 else size - 1
                                )
                            }

                            val right = left + if (horizontalOrVert) size - 1 else 0
                            val bottom = top + if (horizontalOrVert) 0 else size - 1

                            val candidateShip = StudentShip(top, left, bottom, right)
                            if (overlaps) {
                                throw Exception("Overlapping ships")
                            }

                            selectedShip.selected = true
                            userPlacedShips.add(candidateShip)
                            UIForShipPlacement(candidateShip)

                            // invalidate the view to redraw the grid and ships
                            invalidate()

                        }
                        if (selectedShip == null) {
                            PlaceShip = true
                            PlayerGrid = StudentBattleshipGrid(
                                BattleshipGrid.DEFAULT_COLUMNS, BattleshipGrid.DEFAULT_ROWS, StudentBattleshipOpponent(
                                    BattleshipGrid.DEFAULT_COLUMNS,
                                    BattleshipGrid.DEFAULT_ROWS,
                                    userPlacedShips

                                )
                            )
                        }
                    }
                }
                catch (e: Exception) {
                    Toast.makeText(context, "Overlapping ships", Toast.LENGTH_SHORT).show()
                }

            }
        }

        return true
    }


    private val cellColors = Array(rowCount) { Array(colCount) { Color.BLUE } }


    private fun UIForGuessResult(row: Int, col: Int, guessResult: GuessResult) {
        val color = when (guessResult) {
            is GuessResult.HIT -> Color.BLACK
            GuessResult.MISS -> Color.RED
            is GuessResult.SUNK -> Color.YELLOW
            else -> Color.BLUE
        }

        cellColors[col][row] = color
        invalidate()
    }

    

}
