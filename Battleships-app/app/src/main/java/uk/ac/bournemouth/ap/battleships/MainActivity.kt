package uk.ac.bournemouth.ap.battleships

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity




class MainActivity : AppCompatActivity(), OpponentView.AttackListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playerGridView = findViewById<StudentPlayerView>(R.id.player_gridview)
        val opponentGridView = findViewById<OpponentView>(R.id.opponent_gridview)

        opponentGridView.attackListener = this
    }

    override fun attack() {
        val opponentGridView = findViewById<StudentPlayerView>(R.id.player_gridview)
        opponentGridView.opponentAttack()
    }
}
