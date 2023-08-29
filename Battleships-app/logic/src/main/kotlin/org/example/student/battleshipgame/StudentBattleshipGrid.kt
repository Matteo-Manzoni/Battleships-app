package org.example.student.battleshipgame

import uk.ac.bournemouth.ap.battleshiplib.*
import uk.ac.bournemouth.ap.lib.matrix.MutableMatrix

class StudentBattleshipGrid(
    override val columns: Int,
    override val rows: Int,
    override val opponent: BattleshipOpponent,
    override val shipsSunk: BooleanArray
) : BattleshipGrid {
    //setting variables to then use
    private val grid = MutableMatrix<GuessCell>(columns, rows, GuessCell.UNSET)
    private val hits = IntArray(opponent.ships.size)
    private val listeners = mutableListOf<BattleshipGrid.BattleshipGridListener>()


    constructor(columns: Int, rows: Int, opponent: BattleshipOpponent) : this(
        columns,
        rows,
        opponent,
        BooleanArray(opponent.ships.size)
    )



    //when ship is all sunk
    override val isFinished: Boolean
        get() = shipsSunk.all { it }

    override fun get(column: Int, row: Int): GuessCell {
        return grid[column, row]
    }


    override fun shootAt(x: Int, y: Int): GuessResult {
        // Coordinates within the grid check

        if (x !in 0 until columns || y !in 0 until rows) {
            throw IllegalArgumentException("Coordinates ($x, $y) are not within the grid.")
        }

        // Check if the cell has already been shot at
        if (grid[x, y] != GuessCell.UNSET) {
            throw Exception("Duplicate move")
        }

       
        val shipInfo = opponent.shipAt(x, y)
        return if (shipInfo != null) {
            
            // guess matrix with a hit
            grid[x, y] = GuessCell.HIT(shipInfo.index)

            // Update the hits array for the hit ship
            hits[shipInfo.index]++

            
            if (hits[shipInfo.index] == shipInfo.ship.size) {
                shipsSunk[shipInfo.index] = true

                // guess matrix Sunk
                shipInfo.ship.forEachIndex { x, y -> grid[x, y] = GuessCell.SUNK(shipInfo.index) }

                GuessResult.SUNK(shipInfo.index)
            } else {
                GuessResult.HIT(shipInfo.index)
            }
        } else {
            // guess Matrix Miss
            grid[x, y] = GuessCell.MISS
            GuessResult.MISS
        }.also {
            listeners.forEach { it.onGridChanged(this, x, y) }
        }
    }



    override fun addOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        listeners.add(listener)
    }

    override fun removeOnGridChangeListener(listener: BattleshipGrid.BattleshipGridListener) {
        listeners.remove(listener)
    }

}



/*Tried a way of making the computer better by getting the neighbouring cells but wasnt succesful.
private fun getNeighbors(x: Int, y: Int): List<GuessCell> {
        val neighbors = mutableListOf<GuessCell>()
        for (i in -1..1) {
            for (j in -1..1) {
                val neighborX = x + i
                val neighborY = y + j
                // Check if the neighboring cell is within the bounds of the grid and not the current cell
                if (neighborX in 0 until columns && neighborY in 0 until rows && !(i == 0 && j == 0)) {
                    neighbors.add(grid[neighborX, neighborY])
                }
            }
        }
        return neighbors
    }


    override fun shootAt(x: Int, y: Int): GuessResult {
        // Coordinates within the grid check

        if (x !in 0 until columns || y !in 0 until rows) {
            throw IllegalArgumentException("Coordinates ($x, $y) are not within the grid.")
        }

        // Check if the cell has already been shot at
        if (grid[x, y] != GuessCell.UNSET) {
            throw Exception("Duplicate move")
        }

        val previousGuessCell = getNeighbors(x, y).filter { it != GuessCell.UNSET }.firstOrNull()
        if (previousGuessCell != null && previousGuessCell is GuessCell.HIT) {
            val previousX = previousGuessCell.shipIndex.x
            val previousY = previousGuessCell.shipIndex.y
            val offsets = listOf(-1, 1)
            for (i in offsets.indices) {
                val newX = previousX + offsets[i]
                val newY = previousY + offsets[i]
                if (newX in 0 until columns && newY in 0 until rows && grid[newX, newY] == GuessCell.UNSET) {
                    val shipInfo = opponent.shipAt(newX, newY)
                    return if (shipInfo != null) {
                        // guess matrix with a hit
                        grid[newX, newY] = GuessCell.HIT(shipInfo.index)
                        // Update the hits array for the hit ship
                        hits[shipInfo.index]++
                        if (hits[shipInfo.index] == shipInfo.ship.size) {
                            shipsSunk[shipInfo.index] = true
                            // guess matrix Sunk
                            shipInfo.ship.forEachIndex { x, y -> grid[x, y] = GuessCell.SUNK(shipInfo.index) }
                            GuessResult.SUNK(shipInfo.index)
                        } else {
                            GuessResult.HIT(shipInfo.index)
                        }
                    } else {
                        // guess Matrix Miss
                        grid[newX, newY] = GuessCell.MISS
                        GuessResult.MISS
                    }.also {
                        listeners.forEach { it.onGridChanged(this, newX, newY) }
                    }
                }
            }
        }

        val shipInfo = opponent.shipAt(x, y)
        return if (shipInfo != null) {
            // guess matrix with a hit
            grid[x, y] = GuessCell.HIT(shipInfo.index)
            // Update the hits array for the hit ship
            hits[shipInfo.index]++
            if (hits[shipInfo.index] == shipInfo.ship.size) {
                shipsSunk[shipInfo.index] = true
                // guess matrix Sunk
                shipInfo.ship.forEachIndex { x, y -> grid[x, y] = GuessCell.SUNK(shipInfo.index) }
                GuessResult.SUNK(shipInfo.index)
            } else {
                GuessResult.HIT(shipInfo.index)
            }
        } else {
            // guess Matrix Miss
            grid[x, y] = GuessCell.MISS
            GuessResult.MISS
        }.also {
            listeners.forEach { it.onGridChanged(this, x, y) }
        }
    }
 */