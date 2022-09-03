package com.wy8162.utils

import org.apache.commons.text.CharacterPredicates
import org.apache.commons.text.RandomStringGenerator
import java.security.SecureRandom

object UniqueIdGenerator {
    private val generator = RandomStringGenerator
        .Builder()
        .withinRange(charArrayOf('0', '9'), charArrayOf('a', 'f'))
        .filteredBy(CharacterPredicates.DIGITS, CharacterPredicates.LETTERS)
        .usingRandom(SecureRandom()::nextInt)
        .build()

    fun nextUniqueId(num: Int): String = generator.generate(num)
}
