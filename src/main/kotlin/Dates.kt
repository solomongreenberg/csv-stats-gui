package main.dates

import main.csv.Point
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.ISODateTimeFormat

//fun main() {
//    println(timestampCurrent)
//    println(timestampPast)
//}

//fun parseDate(date: DateTime): String =
//    ISODateTimeFormat.dateTime().print(date)

fun parseDate(date: DateTime): String =
    DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss").print(date)

val timestampCurrent: String
    get() = parseDate(DateTime())

val timestampPast: String
    get() = parseDate(DateTime().minusMonths(6))


val queryText: String
    get() {
        return "SELECT deviceid,\n" +
                "         timestamp,\n" +
                "         tempf\n" +
                "FROM \"wtw-telemetric-temp\".\"wtwtelemetricdev\"\n" +
                "WHERE deviceid = 'DEVICE_ID'\n" +
                "        AND from_iso8601_timestamp(timestamp)\n" +
                "    BETWEEN timestamp'$timestampPast'\n" +
                "        AND timestamp'$timestampCurrent'"
    }

fun filterDate(points: List<Point>, start: DateTime): List<Point> {
    val ret = points.filter {it.timestamp.isAfter(start)}
    println("Filter date: $start, remaining size ${ret.size}")
    return ret
}
