package com.example.domain.usecase.di

import com.example.domain.usecase.tags.implementations.AssignTagsToNoteUseCaseImpl
import com.example.domain.usecase.tags.implementations.CreateTagUseCaseImpl
import com.example.domain.usecase.tags.implementations.DeleteTagUseCaseImpl
import com.example.domain.usecase.tags.implementations.GetTagsUseCaseImpl
import com.example.domain.usecase.tags.implementations.SeedDefaultTagsUseCaseImpl
import com.example.domain.usecase.tags.implementations.UpdateTagUseCaseImpl
import com.example.domain.usecase.tags.interfaces.AssignTagsToNoteUseCase
import com.example.domain.usecase.tags.interfaces.CreateTagUseCase
import com.example.domain.usecase.tags.interfaces.DeleteTagUseCase
import com.example.domain.usecase.tags.interfaces.GetTagsUseCase
import com.example.domain.usecase.tags.interfaces.SeedDefaultTagsUseCase
import com.example.domain.usecase.tags.interfaces.UpdateTagUseCase
import org.koin.dsl.module

val tagsUseCaseModule = module {
    factory<GetTagsUseCase> { GetTagsUseCaseImpl(get(), get()) }
    factory<CreateTagUseCase> { CreateTagUseCaseImpl(get(), get(), get()) }
    factory<UpdateTagUseCase> { UpdateTagUseCaseImpl(get(), get()) }
    factory<DeleteTagUseCase> { DeleteTagUseCaseImpl(get(), get(), get()) }
    factory<AssignTagsToNoteUseCase> { AssignTagsToNoteUseCaseImpl(get(), get(), get()) }
    factory<SeedDefaultTagsUseCase> { SeedDefaultTagsUseCaseImpl(get()) }
}
