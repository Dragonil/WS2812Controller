package de.oligorges.ws2812editor.navigation.home

import android.app.Application
import androidx.lifecycle.*
import de.oligorges.ws2812editor.Room.AppDatabase
import de.oligorges.ws2812editor.Room.StripeRepository
import de.oligorges.ws2812editor.Room.stripe
import kotlinx.coroutines.launch

class HomeViewModel(app: Application): AndroidViewModel(app) {

    private var app: Application
    private val repository: StripeRepository
    val allStripes: LiveData<List<stripe>>

    init {
        this.app = app
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val stripeDao = AppDatabase.getInstance(app).stripeDao()
        repository = StripeRepository(stripeDao)
        allStripes = repository.allStripes
    }

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on
     * the main thread, blocking the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called
     * viewModelScope which we can use here.
     */
    fun insert(stripe: stripe) = viewModelScope.launch {
        repository.insert(stripe)
    }

}