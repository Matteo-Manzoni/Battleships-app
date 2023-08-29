package org.example.student.battleshipgame

import uk.ac.bournemouth.ap.battleshiplib.Ship
import uk.ac.bournemouth.ap.lib.matrix.ext.Coordinate

data class StudentShip(
    override val top: Int,
    override val left: Int,
    override val bottom: Int,
    override val right: Int
) : Ship {
    var selected: Boolean = false
    //added to try button
    var isVertical: Boolean = false
    val length: Int
        get() = if (isVertical) bottom - top + 1 else right - left + 1

    init {
        require(top <= bottom) { "Invalid cooredinates for ship: top=$top, bottom=$bottom" }
        require(left <= right) { "Invalid cooredinates for ship: left=$left, right=$right" }
        require(width == 1 || height == 1) { "Invalid ship" }
    }




    fun overlapsWith(left: Int, top: Int, right: Int, bottom: Int): Boolean {
        return !(right < this.left || left > this.right || bottom < this.top || top > this.bottom)
    }

    override val topLeft: Coordinate get() = Coordinate(x = left, y = top)
    override val bottomRight: Coordinate get() = Coordinate(x = right, y = bottom)
}