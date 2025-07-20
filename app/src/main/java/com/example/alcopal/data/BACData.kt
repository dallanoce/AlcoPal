package com.example.alcopal.data

import java.util.*

data class Drink(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val alcoholContent: Double, // percentage (e.g., 5.0 for 5%)
    val volume: Double, // in ml
    val timestamp: Long = System.currentTimeMillis()
) {
    // Calculate grams of pure alcohol
    fun getAlcoholGrams(): Double {
        return (volume * alcoholContent / 100) * 0.789 // 0.789 g/ml is alcohol density
    }
}

data class UserProfile(
    val weight: Double, // in kg
    val gender: Gender,
    val age: Int
)

enum class Gender(val distributionFactor: Double) {
    MALE(0.68),
    FEMALE(0.55)
}

data class BACReading(
    val bac: Double,
    val timestamp: Long,
    val status: BACStatus
)

enum class BACStatus(val description: String, val color: Int) {
    SOBER("Sober", 0xFF4CAF50.toInt()),
    SLIGHTLY_IMPAIRED("Slightly Impaired", 0xFFFFEB3B.toInt()),
    IMPAIRED("Impaired", 0xFFFF9800.toInt()),
    LEGALLY_DRUNK("Legally Drunk", 0xFFF44336.toInt()),
    SEVERELY_IMPAIRED("Severely Impaired", 0xFF9C27B0.toInt());

    companion object {
        fun fromBAC(bac: Double): BACStatus {
            return when {
                bac < 0.02 -> SOBER
                bac < 0.05 -> SLIGHTLY_IMPAIRED
                bac < 0.08 -> IMPAIRED
                bac < 0.15 -> LEGALLY_DRUNK
                else -> SEVERELY_IMPAIRED
            }
        }
    }
}