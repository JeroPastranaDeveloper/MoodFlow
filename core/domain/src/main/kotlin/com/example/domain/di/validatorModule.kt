package com.example.domain.di

import com.example.domain.validator.EmailValidator
import com.example.domain.validator.EmailValidatorImpl
import com.example.domain.validator.PasswordValidator
import com.example.domain.validator.PasswordValidatorImpl
import org.koin.dsl.module

val validatorModule = module {
    single<EmailValidator> { EmailValidatorImpl(get()) }

    single<PasswordValidator> { PasswordValidatorImpl(get()) }
}
