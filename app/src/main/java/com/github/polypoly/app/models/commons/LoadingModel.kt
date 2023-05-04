package com.github.polypoly.app.models.commons

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * Abstract model concept that can either be loading or not loading
 */
abstract class LoadingModel: ViewModel() {
    private val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    /**
     * Queries the loading status data
     * @return a live data that is true iff the model is loading something
     */
    fun getIsLoading(): LiveData<Boolean> {
        return isLoading
    }

    /**
     * Updates the current loading state
     * @param loading true iff the model is currently loading something
     */
    protected fun setLoading(loading: Boolean) {
        isLoading.postValue(loading)
    }
}