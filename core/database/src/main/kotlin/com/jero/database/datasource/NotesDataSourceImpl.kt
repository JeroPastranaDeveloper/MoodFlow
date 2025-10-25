package com.jero.database.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jero.database.model.NoteDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NotesDataSourceImpl(
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth
) : NotesDataSource {

    private fun getUserNotesRef(userId: String): DatabaseReference {
        return database.reference.child("users").child(userId).child("notes")
    }
    
    override suspend fun createNote(note: NoteDto): Result<NoteDto> {
        return try {
            val userId = auth.currentUser?.uid 
                ?: return Result.failure(Exception("Usuario no autenticado"))
            
            val noteRef = getUserNotesRef(userId).child(note.id)
            noteRef.setValue(note).await()
            Result.success(note)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateNote(note: NoteDto): Result<NoteDto> {
        return try {
            val userId = auth.currentUser?.uid 
                ?: return Result.failure(Exception("Usuario no autenticado"))
            
            val noteRef = getUserNotesRef(userId).child(note.id)
            noteRef.setValue(note).await()
            Result.success(note)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNote(noteId: String, userId: String): Result<Unit> {
        return try {
            val noteRef = getUserNotesRef(userId).child(noteId)
            noteRef.removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNote(noteId: String, userId: String): Result<NoteDto> {
        return try {
            val noteRef = getUserNotesRef(userId).child(noteId)
            val snapshot = noteRef.get().await()
            val note = snapshot.getValue(NoteDto::class.java)
            
            if (note != null) {
                Result.success(note)
            } else {
                Result.failure(Exception("Nota no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getAllNotes(userId: String): Flow<Result<List<NoteDto>>> = callbackFlow {
        val notesRef = getUserNotesRef(userId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val notes = snapshot.children.mapNotNull {
                        it.getValue(NoteDto::class.java)
                    }

                    trySend(Result.success(notes))
                } catch (e: Exception) {
                    trySend(Result.failure(e))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                trySend(Result.failure(error.toException()))
            }
        }

        notesRef.addValueEventListener(listener)

        awaitClose {
            notesRef.removeEventListener(listener)
        }
    }

    override suspend fun syncNote(userId: String, note: NoteDto): Result<Unit> {
        return try {
            getUserNotesRef(userId).child(note.id).setValue(note).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
