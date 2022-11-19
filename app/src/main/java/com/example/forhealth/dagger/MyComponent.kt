package com.example.forhealth.dagger

import android.content.Context
import com.example.forhealth.activity.*
import com.example.forhealth.datamodel.ModelPairedDevices
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ComponentModule::class,DatabaseModule::class])
interface MyComponent {

    fun inject(splashScreen: SplashScreen)
    fun inject(choiceDoctorOrPatient: ChoiceDoctorOrPatient)
    fun inject(doctorsLoginPage:DoctorsLoginPage)
    fun inject(signup: Signup)
    fun inject(login: Login)
    fun inject(forgotPassword: ForgotPassword)
    fun inject(existingUsers: ExistingUsers)
    fun inject(doctorsLandingPage: DoctorsLandingPage)
    fun inject(createNewPatient: CreateNewPatient)
    fun inject(existingPatient: ExistingPatient)
    fun inject(patientProfilePage: PatientProfilePage)
    fun inject(sessionInformation: SessionInformation)
    fun inject(guestMode: GuestMode)
    fun inject(sessionControlPanel: SessionControlPanel)

    @Component.Factory
    interface Factory{
            fun create(@BindsInstance context: Context): MyComponent
    }

}