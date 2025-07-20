package com.example.alcopal.calculator

import com.example.alcopal.data.Drink
import com.example.alcopal.data.UserProfile
import com.example.alcopal.data.BACReading
import com.example.alcopal.data.BACStatus
import kotlin.math.max

class BACCalculator {

    companion object {
        // Alcohol elimination rate (grams per hour per kg body weight)
        private const val ELIMINATION_RATE = 0.15 // per hour

        // Convert BAC percentage to g/100ml
        private const val BAC_CONVERSION_FACTOR = 100.0
    }

    /**
     * Calculate current BAC based on drinks consumed and user profile
     * Uses the Widmark formula with time-based elimination
     */
    fun calculateBAC(drinks: List<Drink>, userProfile: UserProfile, currentTime: Long = System.currentTimeMillis()): BACReading {
        if (drinks.isEmpty()) {
            return BACReading(0.0, currentTime, BACStatus.SOBER)
        }

        var totalBAC = 0.0

        for (drink in drinks) {
            val drinkBAC = calculateDrinkBAC(drink, userProfile, currentTime)
            totalBAC += drinkBAC
        }

        // Ensure BAC doesn't go below 0
        totalBAC = max(0.0, totalBAC)

        val status = BACStatus.fromBAC(totalBAC)
        return BACReading(totalBAC, currentTime, status)
    }

    /**
     * Calculate BAC contribution from a single drink
     */
    private fun calculateDrinkBAC(drink: Drink, userProfile: UserProfile, currentTime: Long): Double {
        val alcoholGrams = drink.getAlcoholGrams()
        val bodyWeight = userProfile.weight
        val distributionFactor = userProfile.gender.distributionFactor

        // Initial BAC using Widmark formula
        // BAC = (grams of alcohol) / (body weight in grams × distribution factor) × 100
        val initialBAC = (alcoholGrams) / (bodyWeight * 1000 * distributionFactor) * BAC_CONVERSION_FACTOR

        // Calculate time elapsed since drink was consumed
        val hoursElapsed = (currentTime - drink.timestamp) / (1000.0 * 60.0 * 60.0)

        // Calculate BAC after elimination
        val eliminatedBAC = ELIMINATION_RATE * hoursElapsed

        return max(0.0, initialBAC - eliminatedBAC)
    }

    /**
     * Calculate time until BAC reaches 0
     */
    fun calculateTimeToSober(drinks: List<Drink>, userProfile: UserProfile): Long {
        val currentBAC = calculateBAC(drinks, userProfile)

        if (currentBAC.bac <= 0.0) return 0L

        // Time = BAC / elimination rate (in hours)
        val hoursToSober = currentBAC.bac / ELIMINATION_RATE

        // Convert to milliseconds
        return (hoursToSober * 60 * 60 * 1000).toLong()
    }

    /**
     * Calculate estimated time until BAC drops below legal limit (0.08)
     */
    fun calculateTimeToLegalLimit(drinks: List<Drink>, userProfile: UserProfile): Long {
        val currentBAC = calculateBAC(drinks, userProfile)
        val legalLimit = 0.08

        if (currentBAC.bac <= legalLimit) return 0L

        val bacOverLimit = currentBAC.bac - legalLimit
        val hoursToLegalLimit = bacOverLimit / ELIMINATION_RATE

        return (hoursToLegalLimit * 60 * 60 * 1000).toLong()
    }
}