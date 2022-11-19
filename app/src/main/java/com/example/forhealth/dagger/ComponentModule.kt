package com.example.forhealth.dagger

import com.example.forhealth.bluetooth.Bluetooth
import com.example.forhealth.common.Common
import dagger.Binds
import dagger.Module

@Module
 abstract class ComponentModule(){

    @CommonQualifier
    @Binds
    abstract fun getCommon(common: Common) : Services

    @BluetoothQualifier
    @Binds
    abstract fun getBluetooth(bluetooth: Bluetooth) : Services

}