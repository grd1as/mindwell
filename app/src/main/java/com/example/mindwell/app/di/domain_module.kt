package com.example.mindwell.app.di

import com.example.mindwell.app.domain.usecases.auth.*
import com.example.mindwell.app.domain.usecases.checkin.GetCheckinsUseCase
import com.example.mindwell.app.domain.usecases.checkin.GetCheckinsUseCaseImpl
import com.example.mindwell.app.domain.usecases.checkin.GetLastCheckinUseCase
import com.example.mindwell.app.domain.usecases.checkin.GetLastCheckinUseCaseImpl
import com.example.mindwell.app.domain.usecases.checkin.GetMonthlySummaryUseCase
import com.example.mindwell.app.domain.usecases.checkin.GetMonthlySummaryUseCaseImpl
import com.example.mindwell.app.domain.usecases.form.*
import com.example.mindwell.app.domain.usecases.onboarding.*
import com.example.mindwell.app.domain.usecases.preference.*
import com.example.mindwell.app.domain.usecases.reminder.*
import com.example.mindwell.app.domain.usecases.report.*
import com.example.mindwell.app.domain.usecases.summary.GetWeeklySummaryUseCase
import com.example.mindwell.app.domain.usecases.summary.GetWeeklySummaryUseCaseImpl
import com.example.mindwell.app.domain.usecases.resource.*
import com.example.mindwell.app.domain.usecases.feeling.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DomainModule {
    
    // Auth
    @Binds
    @Singleton
    abstract fun bind_login_use_case(impl: LoginUseCaseImpl): LoginUseCase
    
    @Binds
    @Singleton
    abstract fun bind_check_auth_status_use_case(impl: CheckAuthStatusUseCaseImpl): CheckAuthStatusUseCase
    
    @Binds
    @Singleton
    abstract fun bind_logout_use_case(impl: LogoutUseCaseImpl): LogoutUseCase
    
    // Form
    @Binds
    @Singleton
    abstract fun bind_get_forms_use_case(impl: GetFormsUseCaseImpl): GetFormsUseCase
    
    @Binds
    @Singleton
    abstract fun bind_get_form_detail_use_case(impl: GetFormDetailUseCaseImpl): GetFormDetailUseCase
    
    @Binds
    @Singleton
    abstract fun bind_submit_form_responses_use_case(impl: SubmitFormResponsesUseCaseImpl): SubmitFormResponsesUseCase
    
    @Binds
    @Singleton
    abstract fun bind_get_pending_forms_use_case(impl: GetPendingFormsUseCaseImpl): GetPendingFormsUseCase
    
    // Checkin
    @Binds
    @Singleton
    abstract fun bind_get_checkins_use_case(impl: GetCheckinsUseCaseImpl): GetCheckinsUseCase
    
    @Binds
    @Singleton
    abstract fun bind_get_last_checkin_use_case(impl: GetLastCheckinUseCaseImpl): GetLastCheckinUseCase
    
    @Binds
    @Singleton
    abstract fun bind_get_monthly_summary_use_case(impl: GetMonthlySummaryUseCaseImpl): GetMonthlySummaryUseCase
    
    // Summary
    @Binds
    @Singleton
    abstract fun bind_get_weekly_summary_use_case(impl: GetWeeklySummaryUseCaseImpl): GetWeeklySummaryUseCase
    
    // Preference
    @Binds
    @Singleton
    abstract fun bind_get_user_preferences_use_case(impl: GetUserPreferencesUseCaseImpl): GetUserPreferencesUseCase
    
    @Binds
    @Singleton
    abstract fun bind_get_preferences_use_case(impl: GetPreferencesUseCaseImpl): GetPreferencesUseCase
    
    @Binds
    @Singleton
    abstract fun bind_update_preferences_use_case(impl: UpdatePreferencesUseCaseImpl): UpdatePreferencesUseCase
    
    // Onboarding
    @Binds
    @Singleton
    abstract fun bind_get_onboarding_pages_use_case(impl: GetOnboardingPagesUseCaseImpl): GetOnboardingPagesUseCase
    
    @Binds
    @Singleton
    abstract fun bind_get_onboarding_state_use_case(impl: GetOnboardingStateUseCaseImpl): GetOnboardingStateUseCase
    
    @Binds
    @Singleton
    abstract fun bind_complete_onboarding_use_case(impl: CompleteOnboardingUseCaseImpl): CompleteOnboardingUseCase
    
    @Binds
    @Singleton
    abstract fun bind_is_first_time_use_case(impl: IsFirstTimeUseCaseImpl): IsFirstTimeUseCase
    
    // Reminder
    @Binds
    @Singleton
    abstract fun bind_get_reminders_use_case(impl: GetRemindersUseCaseImpl): GetRemindersUseCase
    
    // Report
    @Binds
    @Singleton
    abstract fun bind_submit_report_use_case(impl: SubmitReportUseCaseImpl): SubmitReportUseCase
    
    // Resource
    @Binds
    @Singleton
    abstract fun bind_get_resources_use_case(impl: GetResourcesUseCaseImpl): GetResourcesUseCase
    
    @Binds
    @Singleton
    abstract fun bind_get_resource_detail_use_case(impl: GetResourceDetailUseCaseImpl): GetResourceDetailUseCase
    
    @Binds
    @Singleton
    abstract fun bind_get_resource_categories_use_case(impl: GetResourceCategoriesUseCaseImpl): GetResourceCategoriesUseCase
    
    // Feeling
    @Binds
    @Singleton
    abstract fun bind_get_feelings_use_case(impl: GetFeelingsUseCaseImpl): GetFeelingsUseCase
} 