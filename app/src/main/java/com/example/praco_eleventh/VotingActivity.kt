package com.example.praco_eleventh


import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.praco_eleventh.databinding.ActivityVotingBinding
import com.google.firebase.database.*
import com.google.firebase.database.Transaction

class VotingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVotingBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var candidatesList: MutableList<String>
    private lateinit var candidatesAdapter: ArrayAdapter<String>
    private var selectedCandidate: String? = null
    private var hasVoted: Boolean = false
    private lateinit var voteCountListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVotingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        candidatesList = mutableListOf()
        candidatesAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, candidatesList)
        candidatesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.candidateSpinner.adapter = candidatesAdapter

        databaseReference = FirebaseDatabase.getInstance().getReference("Candidates")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                candidatesList.clear()
                for (ds in dataSnapshot.children) {
                    val candidateName = ds.key.toString()
                    candidatesList.add(candidateName)
                }
                candidatesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@VotingActivity,
                    "Failed to read candidates from database.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        // add listener to get vote count for selected candidate
        voteCountListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val votes = dataSnapshot.getValue(Int::class.java)
                if (votes != null) {
                    val voteCountText = "Votes: $votes"
                    binding.voteCountTextView.text = voteCountText
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@VotingActivity,
                    "Failed to read vote count for $selectedCandidate.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.candidateSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedCandidate = parent.getItemAtPosition(position).toString()
                    val candidateRef = databaseReference.child(selectedCandidate!!)
                    candidateRef.child("Votes").addValueEventListener(voteCountListener)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    selectedCandidate = null
                }
            }

        binding.voteButton.setOnClickListener {
            if (selectedCandidate == null) {
                Toast.makeText(
                    this@VotingActivity,
                    "Please select a candidate to vote for.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (hasVoted) {
                Toast.makeText(
                    this@VotingActivity,
                    "You have already voted.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val candidateRef = databaseReference.child(selectedCandidate!!)
                candidateRef.child("Votes").runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        var votes = mutableData.getValue(Int::class.java)
                        if (votes == null) {
                            votes = 0
                        }
                        mutableData.value = votes + 1
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(
                        databaseError: DatabaseError?,
                        committed: Boolean,
                        dataSnapshot: DataSnapshot?
                    ) {
                        if (databaseError == null && committed) {
                            hasVoted = true
                            binding.voteButton.isVisible = false
                            binding.voteCountTextView.visibility = View.VISIBLE
                            binding.candidateSpinner.isEnabled = false
                            Toast.makeText(
                                this@VotingActivity,
                                "Vote successful.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@VotingActivity,
                                "Failed to vote.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
            }
        }
    }
}