Online Voting System - Android Application
The Online Voting System is a secure and user-friendly Android application designed to facilitate remote voting. This app ensures privacy, transparency, and ease of access for voters, enabling them to cast votes electronically from anywhere. Built with Android Studio and Kotlin, the app emphasizes security, scalability, and usability.

Features
1. User Authentication:
	Secure login with multi-factor authentication.
	Role-based access (voter, admin, or election organizer).
2. Vote Casting:
	Simple, intuitive UI for casting votes.
	Ensures each voter can vote only once using unique credentials.
3. Secure Ballot Box:
	End-to-end encryption for vote data.
	Prevents tampering or duplication of votes.
4. Real-time Results:
	Display election progress and results securely.
	Role-restricted access for viewing results.
5. Offline/Online Synchronization:
	Works offline and syncs data once reconnected to the internet.


Technical Stack
1. Frontend: Kotlin with Jetpack Compose for a modern UI.
2. Backend: Firebase Firestore for real-time database functionality and authentication.
3. Security:
	Encrypted vote storage and transmission.
	Hash-based verification to ensure data integrity.
Architecture: MVVM (Model-View-ViewModel) for scalable and testable design.
