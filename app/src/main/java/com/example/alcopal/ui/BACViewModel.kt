package com.example.alcopal.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.alcopal.calculator.BACCalculator
import com.example.alcopal.data.Drink
import com.example.alcopal.data.UserProfile
import com.example.alcopal.data.BACReading
import com.example.alcopal.data.BACStatus

class BACViewModel : ViewModel() {

    private val bacCalculator = BACCalculator()

    private val _drinks = mutableListOf<Drink>()
    private val _userProfile = MutableLiveData<UserProfile?>(null)
    val userProfile: LiveData<UserProfile?> = _userProfile

    private val _currentBAC = MutableLiveData<BACReading>()
    val currentBAC: LiveData<BACReading> = _currentBAC

    private val _drinkCount = MutableLiveData<Int>()
    val drinkCount: LiveData<Int> = _drinkCount

    private val _timeToSober = MutableLiveData<Long>()
    val timeToSober: LiveData<Long> = _timeToSober

    private val _timeToLegal = MutableLiveData<Long>()
    val timeToLegal: LiveData<Long> = _timeToLegal

    private val _hasUserProfile = MutableLiveData<Boolean>()
    val hasUserProfile: LiveData<Boolean> = _hasUserProfile

    init {
        updateBACReading() // This will also initialize _hasUserProfile
    }

    fun addDrink(drink: Drink) {
        _drinks.add(drink)
        updateBACReading()
    }

    fun removeDrink(drink: Drink) {
        _drinks.remove(drink)
        updateBACReading()
    }

    fun clearDrinks() {
        _drinks.clear()
        updateBACReading()
    }

    // Add this method
    fun getUserProfile(): UserProfile? {
        return _userProfile.value
    }
    fun setUserProfile(profile: UserProfile) {
        _userProfile.value = profile
        updateBACReading() // This will update _hasUserProfile
    }

    fun getDrinks(): List<Drink> = _drinks.toList()

    fun getCurrentUserProfile(): UserProfile? = _userProfile.value

    private fun updateBACReading() {
        _drinkCount.value = _drinks.size

        val profile = _userProfile.value
        if (profile == null) {
            _currentBAC.value = BACReading(0.0, System.currentTimeMillis(), BACStatus.SOBER)
            _timeToSober.value = 0L
            _timeToLegal.value = 0L
            _hasUserProfile.value = false // Update hasUserProfile
            return
        }

        _hasUserProfile.value = true // Update hasUserProfile
        val bacReading = bacCalculator.calculateBAC(_drinks, profile)
        _currentBAC.value = bacReading

        _timeToSober.value = bacCalculator.calculateTimeToSober(_drinks, profile)
        _timeToLegal.value = bacCalculator.calculateTimeToLegalLimit(_drinks, profile)
    }

    /**
     * Call this method periodically to update BAC as time passes
     */
    fun refreshBAC() {
        updateBACReading()
    }
}
