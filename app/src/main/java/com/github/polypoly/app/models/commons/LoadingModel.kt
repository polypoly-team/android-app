package com.github.polypoly.app.models.commons

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.CompletableFuture

/**
 * Abstract model concept that can either be loading or not loading
 */
abstract class LoadingModel: ViewModel() {
    private val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)

    private val waitingForSyncPromise: ArrayList<CompletableFuture<Boolean>> = arrayListOf()

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
        if (isLoading.value == true && !loading) {
            for (future in waitingForSyncPromise) {
                future.complete(true)
            }
            waitingForSyncPromise.clear()
        }
    }

    /**
     * Registers a callback that will be called next time a data is synchronized with the storage
     * This function is primarily but not exclusively aimed for used in unit tests
     * @return a promise that completes when the data is synced again with the storage
     */
    fun waitForSync(): CompletableFuture<Boolean> {
        waitingForSyncPromise.add(CompletableFuture())
        return waitingForSyncPromise.last()
    }
}