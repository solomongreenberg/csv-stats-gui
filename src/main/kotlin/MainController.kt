package main

import arrow.core.Either
import arrow.core.Try
import arrow.core.getOrDefault
import com.opencsv.CSVReader
import javafx.collections.ObservableList
import javafx.stage.StageStyle
import main.csv.*
import main.dates.filterDate
import main.stats.*

import tornadofx.*
import java.io.FileReader

lateinit var globalDatapoints: ObservableList<WindowStatisticFmt> // Nasty global hack I apologize for

class MainController : Controller() {
    var datapoints: List<Point> = listOf()

    fun loadFile(filename: String) {
        //println(FileReader(filename).readLines()[1])
        //CSVReader(FileReader(filename)).readAll().apply { removeAt(0) }.forEach{println(it.size)}
        when (val ret = loadCSV(filename)) {
            is Either.Left -> {
                println("ERROR at line 20: ${ret.a.message}")
                find<ErrorFragment>().errorText.value = ret.a.message
                find<ErrorFragment>().openWindow(stageStyle = StageStyle.UTILITY)
            }
            is Either.Right -> {
                datapoints = ret.b
                val l = defaultWindows
                    .mapValues { filterDate(datapoints, it.value) }
                    .map {
                        Try {
                            formatWindowStatistic(
                                WindowStatistic(
                                    window = it.key,
                                    mean = mean(it.value),
                                    median = median(it.value),
                                    min = min(it.value),
                                    max = max(it.value),
                                    stddev = stddev(it.value)
                                )
                            )
                        }.getOrDefault {
                            WindowStatisticFmt(
                                window = it.key,
                                mean = "No Data",
                                median = "No Data",
                                min = "No Data",
                                max = "No Data",
                                stddev = "No Data"
                            )
                        }
                    }.observable()
                globalDatapoints = l
                find<ResultsFragment>().openWindow(stageStyle = StageStyle.DECORATED)
            }
        }
    }
}
