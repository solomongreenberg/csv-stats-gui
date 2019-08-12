package main

import org.joda.time.DateTime

val defaultWindows = mapOf (
    "1 Day" to DateTime().minusDays(1),
    "1 Week" to DateTime().minusWeeks(1),
    "1 Month" to DateTime().minusMonths(1),
    "3 Months" to DateTime().minusMonths(3)
)