package main.stats

import main.csv.Point
import org.nield.kotlinstatistics.median
import org.nield.kotlinstatistics.standardDeviation

data class WindowStatistic(
    val window: String,
    val mean: Double,
    val median: Double,
    val min: Double,
    val max: Double,
    val stddev: Double
)

data class WindowStatisticFmt(
    val window: String,
    val mean: String,
    val median: String,
    val min: String,
    val max: String,
    val stddev: String
)

private val Double.round4: String
    get() = String.format("%.4f", this)

fun formatWindowStatistic(s: WindowStatistic): WindowStatisticFmt =
    WindowStatisticFmt(
        window = s.window,
        mean = s.mean.round4,
        median = s.median.round4,
        min = s.min.round4,
        max = s.max.round4,
        stddev = s.stddev.round4
    )

fun mean(pts: List<Point>): Double =
    pts.map { it.tempf }.average()

fun median(pts: List<Point>): Double =
    pts.map { it.tempf }.median()

fun min(pts: List<Point>): Double =
    pts.map { it.tempf }.min()!!

fun max(pts: List<Point>): Double =
    pts.map { it.tempf }.max()!!

fun stddev(pts: List<Point>): Double =
    pts.map { it.tempf }.standardDeviation()
