package com.jero.database.repository

import android.util.Log
import com.example.domain.repository.NotesRepository
import com.google.firebase.auth.FirebaseAuth
import com.jero.core.model.Note
import com.jero.database.datasource.NotesDataSource
import com.jero.database.mapper.toDomain
import com.jero.database.mapper.toDto
import com.jero.database.mapper.toEntity
import com.jero.localdatabase.dao.NoteDao
import com.jero.network.NetworkMonitor
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class NotesRepositoryImpl(
    private val notesDataSource: NotesDataSource,
    private val auth: FirebaseAuth,
    private val notesDao: NoteDao,
    private val networkMonitor: NetworkMonitor,
) : NotesRepository {

    private fun getCurrentUserId(): String? = auth.currentUser?.uid

    // Observar notas con sincronización bidireccional
     override suspend fun getAllNotes(userId: String) = channelFlow {
        val userId = getCurrentUserId()
            ?: throw Exception("Usuario no autenticado")

        // 1. Observar cambios en Room (fuente única de verdad local)
        launch {
            notesDao.getNotesFlow().collect { entities ->
                send(entities.map { it.toDomain() })
            }
        }

        // 2. Sincronizar cambios pendientes cuando hay conexión
        launch {
            networkMonitor.observeConnectivity()
                .filter { it } // Solo cuando hay conexión
                .collect {
                    syncPendingChanges(userId)
                }
        }

        // 3. Recibir actualizaciones de Firebase
        launch {
            notesDataSource.getAllNotes(userId).collect { result ->
                result.onSuccess { notesDto ->
                    // Merge inteligente: no sobrescribir cambios pendientes
                    val pendingIds = notesDao.getPendingNotes().map { it.id }.toSet()

                    val notesToInsert = notesDto
                        .filter { it.id !in pendingIds }
                        .map { dto ->
                            dto.toEntity().copy(
                                pendingSync = false,
                                date = System.currentTimeMillis()
                            )
                        }

                    if (notesToInsert.isNotEmpty()) {
                        notesDao.insertNotes(notesToInsert)
                    }
                }
            }
        }
    }

    override suspend fun createNote(note: Note): Result<Note> {
        return try {
            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("Usuario no autenticado"))

            // Guardar en Room inmediatamente con pendingSync = true
            val entity = note.toEntity().copy(
                userId = userId,
                pendingSync = true
            )
            notesDao.insertNote(entity)

            // Intentar sincronizar con Firebase si hay conexión
            if (networkMonitor.isConnected()) {
                val syncResult = notesDataSource.syncNote(userId, entity.toDto())
                syncResult.onSuccess {
                    notesDao.markAsSynced(entity.id)
                }
            }

            Result.success(note)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateNote(note: Note): Result<Note> {
        return try {
            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("Usuario no autenticado"))

            // Actualizar en Room con pendingSync = true
            val entity = note.toEntity().copy(
                userId = userId,
                pendingSync = true,
                date = System.currentTimeMillis()
            )
            notesDao.insertNote(entity)

            // Intentar sincronizar con Firebase si hay conexión
            if (networkMonitor.isConnected()) {
                val syncResult = notesDataSource.syncNote(userId, entity.toDto())
                syncResult.onSuccess {
                    notesDao.markAsSynced(entity.id)
                }
            }

            Result.success(note)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNote(noteId: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("Usuario no autenticado"))

            // Eliminar de Room inmediatamente
            notesDao.deleteNote(noteId)

            // Intentar eliminar de Firebase si hay conexión
            if (networkMonitor.isConnected()) {
                notesDataSource.deleteNote(noteId, userId)
            }
            // TODO: Guardar operación de eliminación pendiente para sincronizar después

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNote(noteId: String): Result<Note> {
        return try {
            // Primero intentar desde Room
            val entity = notesDao.getNoteById(noteId)
            if (entity != null) {
                return Result.success(entity.toDomain())
            }

            // Si no está en Room, buscar en Firebase
            val userId = getCurrentUserId()
                ?: return Result.failure(Exception("Usuario no autenticado"))

            notesDataSource.getNote(noteId, userId)
                .map { it.toDomain() }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Sincronizar notas pendientes con Firebase
    private suspend fun syncPendingChanges(userId: String) {
        try {
            val pendingNotes = notesDao.getPendingNotes()

            pendingNotes.forEach { noteEntity ->
                val result = notesDataSource.syncNote(userId, noteEntity.toDto())

                result.onSuccess {
                    notesDao.markAsSynced(noteEntity.id)
                }.onFailure {
                    Log.e("NotesRepository", "Failed to sync note ${noteEntity.id}", it)
                }
            }
        } catch (e: Exception) {
            Log.e("NotesRepository", "Error syncing pending changes", e)
        }
    }
}
