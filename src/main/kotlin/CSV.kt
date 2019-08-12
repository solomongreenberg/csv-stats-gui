package main.csv

import arrow.core.Either
import arrow.core.Try
import com.opencsv.CSVReader
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import java.io.File
import java.io.FileReader

data class Point(val timestamp: DateTime, val tempf: Double)


fun loadCSV(path: String): Either<Throwable, List<Point>> =
    Try {
        val csv = CSVReader(FileReader(path)).readAll().apply { removeAt(0) } // Read CSV
        return@Try ISODateTimeFormat.dateTime().run {
            // Parse CSV, turning each line into a Point object
            csv
                .filter { it[2].isNotEmpty() and (it[2].toDoubleOrNull() != null) }
                .map {
                    Point(
                        parseDateTime(it[1]),
                        it[2].toDouble()
                    )
                }
        }
    }.toEither()

