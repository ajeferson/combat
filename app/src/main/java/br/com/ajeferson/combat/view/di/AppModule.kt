package br.com.ajeferson.combat.view.di

import android.content.Context
import br.com.ajeferson.combat.view.common.App
import br.com.ajeferson.combat.view.service.connection.GameService
import br.com.ajeferson.combat.view.service.connection.RmiGameService
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Created by ajeferson on 25/02/2018.
 */
@Module
class AppModule {

    @Provides
    fun provideContext(application: App): Context = application.applicationContext

    @Singleton
    @Provides
    fun provideConnectionManager(app: App): GameService = RmiGameService(app, SERVER_IP, SERVER_PORT)

    companion object {

        private const val SERVER_IP = "192.168.1.126"
        private const val SERVER_PORT = 7777

    }

}