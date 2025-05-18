package com.example.mindwell.app.di

import com.example.mindwell.app.domain.repositories.AssessmentRepository
import com.example.mindwell.app.domain.repositories.CheckInRepository
import com.example.mindwell.app.domain.usecases.assessment.CreateAssessmentUseCase
import com.example.mindwell.app.domain.usecases.assessment.CreateAssessmentUseCaseImpl
import com.example.mindwell.app.domain.usecases.assessment.SaveAssessmentUseCase
import com.example.mindwell.app.domain.usecases.assessment.SaveAssessmentUseCaseImpl
import com.example.mindwell.app.domain.usecases.checkin.CreateCheckInUseCase
import com.example.mindwell.app.domain.usecases.checkin.CreateCheckInUseCaseImpl
import com.example.mindwell.app.domain.usecases.checkin.GetRecentCheckInsUseCase
import com.example.mindwell.app.domain.usecases.checkin.GetRecentCheckInsUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Módulo para injeção de dependências no escopo de ViewModels.
 * As dependências declaradas aqui estarão disponíveis apenas para ViewModels
 * e terão o ciclo de vida associado aos mesmos.
 */
@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    // Aqui serão adicionadas as dependências específicas para os ViewModels
    
    @Provides
    @ViewModelScoped
    fun provideCreateCheckInUseCase(repository: CheckInRepository): CreateCheckInUseCase {
        return CreateCheckInUseCaseImpl(repository)
    }
    
    @Provides
    @ViewModelScoped
    fun provideGetRecentCheckInsUseCase(repository: CheckInRepository): GetRecentCheckInsUseCase {
        return GetRecentCheckInsUseCaseImpl(repository)
    }
    
    @Provides
    @ViewModelScoped
    fun provideSaveAssessmentUseCase(repository: AssessmentRepository): SaveAssessmentUseCase {
        return SaveAssessmentUseCaseImpl(repository)
    }
    
    @Provides
    @ViewModelScoped
    fun provideCreateAssessmentUseCase(repository: AssessmentRepository): CreateAssessmentUseCase {
        return CreateAssessmentUseCaseImpl(repository)
    }
} 