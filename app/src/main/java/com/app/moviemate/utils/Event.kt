package com.app.moviemate.utils

import androidx.lifecycle.Observer

// Klasa Event służy jako opakowanie dla danych, które są udostępniane za pomocą LiveData i reprezentują zdarzenie
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    // Zwraca zawartość i uniemożliwia jej ponowne użycie.
    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Zwraca zawartość, nawet jeśli została już obsłużona.
     */
    fun peekContent(): T = content
}

// służy do obserwowania obiektów typu Event<T>
/**
 * An [Observer] for [Event]s, simplifying the pattern of checking if the [Event]'s content has
 * already been handled.
 *
 * [onEventUnhandledContent] is *only* called if the [Event]'s contents has not been handled.
 */
class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(value: Event<T>) {
        // zostanie wywołana tylko wtedy, gdy zawartość zdarzenia nie została jeszcze obsłużona.
        value.getContentIfNotHandled()?.let { value ->
            onEventUnhandledContent(value)
        }
    }
}