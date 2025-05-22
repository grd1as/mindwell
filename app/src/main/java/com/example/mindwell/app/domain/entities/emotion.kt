package com.example.mindwell.app.domain.entities

/**
 * Entidade de domínio representando uma emoção/estado de humor.
 */
data class Emotion(
    val id: Long,
    val name: String,
    val emoji: String,
    val value: Int
) 