package com.jero.database.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jero.database.model.NoteEntity
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
    
    override suspend fun createNote(note: NoteEntity): Result<NoteEntity> {
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
    
    override suspend fun updateNote(note: NoteEntity): Result<NoteEntity> {
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

    override suspend fun getNote(noteId: String, userId: String): Result<NoteEntity> {
        return try {
            val noteRef = getUserNotesRef(userId).child(noteId)
            val snapshot = noteRef.get().await()
            val note = snapshot.getValue(NoteEntity::class.java)
            
            if (note != null) {
                Result.success(note)
            } else {
                Result.failure(Exception("Nota no encontrada"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllNotes(userId: String): Result<List<NoteEntity>> {
        return try {
            val notesRef = getUserNotesRef(userId)
            val snapshot = notesRef.orderByChild("date").get().await()
            
            val notes = snapshot.children.mapNotNull { 
                it.getValue(NoteEntity::class.java) 
            }
            
            Result.success(notes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeNotes(userId: String): Flow<List<NoteEntity>> = callbackFlow {
        val notesRef = getUserNotesRef(userId).orderByChild("date")

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val notes = snapshot.children.mapNotNull { 
                    it.getValue(NoteEntity::class.java) 
                }
                trySend(notes)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        notesRef.addValueEventListener(listener)
        awaitClose { notesRef.removeEventListener(listener) }
    }
}
