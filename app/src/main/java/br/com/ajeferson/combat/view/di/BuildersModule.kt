package br.com.ajeferson.combat.view.di

import br.com.ajeferson.combat.view.view.activity.GameActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by ajeferson on 25/02/2018.
 */
@Module
abstract class BuildersModule {

    @ContributesAndroidInjector
    internal abstract fun bindGameActivity(): GameActivity

}