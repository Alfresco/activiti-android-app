package com.alfresco.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    protected val _isLoading = MutableLiveData<Boolean>()

    protected val _hasNavigation = MutableLiveData<Boolean>()

    val isLoading: LiveData<Boolean> get() = _isLoading

    val hasNavigation: LiveData<Boolean> get() = _hasNavigation

    public fun setHasNavigation(enableNavigation: Boolean) {
        _hasNavigation.value = enableNavigation
    }
}