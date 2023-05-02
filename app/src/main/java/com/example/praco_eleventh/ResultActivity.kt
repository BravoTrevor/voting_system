package com.example.praco_eleventh


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.praco_eleventh.databinding.ActivityResultBinding
import com.google.firebase.database.*

class ResultActivity : AppCompatActivity() {

    data class Candidate(val name: String = "", var votes: Int = 0)

    private lateinit var binding: ActivityResultBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var candidatesList: MutableList<Candidate>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseReference = FirebaseDatabase.getInstance().reference.child("Candidates")
        candidatesList = mutableListOf()

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    candidatesList.clear()
                    for (candidateSnapshot in snapshot.children) {
                        val candidate = candidateSnapshot.getValue(Candidate::class.java)
                        candidate?.let { candidatesList.add(it) }
                    }
                    val winner = getWinner(candidatesList)
                    displayResult(winner, candidatesList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun getWinner(candidatesList: MutableList<Candidate>): Candidate? {
        var highestVotes = 0
        var winner: Candidate? = null

        for (candidate in candidatesList) {
            if (candidate.votes > highestVotes) {
                highestVotes = candidate.votes
                winner = candidate
            }
        }

        return winner
    }

    private fun displayResult(winner: Candidate?, candidatesList: MutableList<Candidate>) {
        // Display winner
        binding.winnerTextView.text = if (winner != null) {
            "Winner: ${winner.name}"
        } else {
            "No winner"
        }

        // Display vote counts for all candidates
        var result = "\nVote counts:\n"
        for (candidate in candidatesList) {
            result += "${candidate.name}: ${candidate.votes}\n"
        }
        binding.resultTextView.text = result.trim()
    }
}
