package com.jero.localdatabase.mapper

interface NoteEntityMapper<Domain, Entity> {
    fun asEntity(domain: Domain): Entity
    fun asDomain(entity: Entity): Domain
}
