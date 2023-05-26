package com.github.polypoly.app.viewmodels.commons

import com.github.polypoly.app.commons.PolyPolyTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoadingModelTest: PolyPolyTest(false, false) {
    @Test
    fun loadingModelIsNotLoadingByDefault() {
        val model = MockLoadingModel()
        assertFalse(model.getIsLoading().value!!)
    }

    @Test
    fun loadingModelUpdatesLoadingDataWhenAsked() {
        val model = MockLoadingModel()

        model.setLoadingValue(true)
        assertTrue(waitForDataUpdate(model.getIsLoading()))

        model.setLoadingValue(false)
        assertFalse(waitForDataUpdate(model.getIsLoading()))
    }

    /**
     * Mock implementation of LoadingModel for testing
     */
    class MockLoadingModel: LoadingModel() {
        /**
         * Updates the mocked loading value
         * @param loading true iff the mock model is currently mocking loading
         */
        fun setLoadingValue(loading: Boolean) {
            setLoading(loading)
        }
    }
}