package br.com.ajeferson.combat.view.extension

import android.databinding.ObservableField

/**
 * Created by ajeferson on 25/02/2018.
 */
var <T> ObservableField<T>.value
    get() = this.get()
    set(value) = this.set(value)