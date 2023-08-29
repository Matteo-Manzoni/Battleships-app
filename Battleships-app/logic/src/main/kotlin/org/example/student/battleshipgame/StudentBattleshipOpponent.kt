package org.example.student.battleshipgame

import uk.ac.bournemouth.ap.battleshiplib.BattleshipOpponent
import uk.ac.bournemouth.ap.battleshiplib.Ship
import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate
import kotlin.math.max
import kotlin.random.Random

class StudentBattleshipOpponent(
    override val columns: Int,
    override val rows: Int,
    ships : List<Ship>
) : BattleshipOpponent {
    override val ships: List<Ship> = ships

    // finding the ship at the given position
    override fun shipAt(column: Int, row: Int): BattleshipOpponent.ShipInfo<Ship>? {
        val ship =
            ships.firstOrNull { it.columnIndices.contains(column) && it.rowIndices.contains(row) }
        return ship?.let { BattleshipOpponent.ShipInfo(ships.indexOf(it), it) }
    }

    companion object {
        // function to randomly create ships of given sizes within the given grid dimensions
        fun createRandomShips(
            columns: Int,
            rows: Int,
            shipSizes: IntArray,
            random: Random
        ): List<StudentShip> {
            val shipPlaced = mutableListOf<StudentShip>()
            for (size in shipSizes) {
                var left: Int
                var top: Int
                var overlaps: Boolean
                var isHorizontal: Boolean
                do {
                    // It is randomly horizontal or vertical
                    isHorizontal = random.nextBoolean()
                    if (size>columns && isHorizontal){
                        isHorizontal = false
                    }
                    if (size>rows && !isHorizontal){
                        isHorizontal = true
                    }

                    // Choose a random position for the ship
                    left = random.nextInt(if (isHorizontal) max(columns - size + 1, 1).coerceAtLeast(1) else columns)


                    top = random.nextInt(if (isHorizontal) rows else rows - size + 1)

                    if (isHorizontal && left + size > columns) {
                        left = columns - size
                    }
                    if (!isHorizontal && top + size > rows) {
                        top = rows - size
                    }

                    // Check if the ship overlaps with any other ship
                    overlaps = shipPlaced.any {
                        it.overlapsWith(
                            left,
                            top,
                            left + if (isHorizontal) size - 1 else 0,
                            top + if (isHorizontal) 0 else size - 1
                        )
                    }
                } while (overlaps)

                val right = left + if (isHorizontal) size - 1 else 0
                val bottom = top + if (isHorizontal) 0 else size - 1

                val candidateShip = StudentShip(top, left, bottom, right)
                shipPlaced.add(candidateShip)
            }
            return shipPlaced
        }
    }

    init {
        val alreadyUsedCells = HashSet<Coordinate>()
        ships.forEach { ship ->
            // Check if the ship is out of bounds
            if (ship.topLeft.x < 0 || ship.topLeft.y < 0 || ship.bottomRight.x >= columns || ship.bottomRight.y >= rows) {
                throw IllegalArgumentException("Ship $ship is not within bounds")
            }
            // Check if the ship overlaps with another ship
            for (x in ship.topLeft.x..ship.bottomRight.x) {
                for (y in ship.topLeft.y..ship.bottomRight.y) {
                    val cell = Coordinate(x, y)
                    if (alreadyUsedCells.contains(cell)) {
                        throw IllegalArgumentException("Ship $ship overlaps with another ship")
                    }

                    alreadyUsedCells.add(cell)
                }
            }


        }
    }





    constructor(columns: Int, rows: Int, shipSizes: IntArray, random: Random)
            : this(columns, rows, createRandomShips(columns, rows, shipSizes, random))



}
