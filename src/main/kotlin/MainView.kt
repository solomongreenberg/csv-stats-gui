package main

import arrow.core.Try
import javafx.beans.property.ReadOnlyListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.StageStyle
import main.csv.Point
import org.apache.commons.io.IOUtils
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import tornadofx.*
import java.net.URL
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import main.dates.queryText
import main.stats.WindowStatistic
import main.stats.WindowStatisticFmt


const val FILE_EMOJI_URL =
    "https://emojipedia-us.s3.dualstack.us-west-1.amazonaws.com/thumbs/120/google/146/file-folder_1f4c1.png"

class MainView : View() {

    val controller: MainController by inject()
    val csv_filename =
        SimpleStringProperty().apply { value = "/home/solomon/Downloads/9b46a193-309b-4907-af39-301a9368bc54.csv" }

    var loadButtonEnabled = false

    override val root = vbox {
        padding = Insets(10.0, 10.0, 10.0, 10.0)
        form {
            fieldset {
                vbox {
                    hbox {
                        text("Filename: ") {
                        }
                        field("") {
                            textfield(csv_filename) {
                                //isEditable = false
                            }
                        }
                        button("Open"/*, graphic = imageview(Image(FILE_EMOJI_URL, 10.0, 10.0, true, false))*/) {
                            action {
                                // On click button
                                val files = chooseFile( // Open file picker dialog
                                    filters = arrayOf(
                                        ExtensionFilter("CSV files (*.csv)", "*.csv")
                                    ), // Only allow CSVs to be loaded
                                    mode = FileChooserMode.Single // Only allow single file to be selected
                                )

                                if (files.isNotEmpty()) {// Make sure we've only selected one file.
                                    csv_filename.value = files[0].absolutePath
                                }

                                //controller.loadFile(csv_filename.value)
                            }
                        }
                        button("?") {
                            action {
                                find<HelpFragment>().openWindow(stageStyle = StageStyle.UTILITY)
                            }
                        }
                    }
                    button("Load") {
                        disableProperty().bind(csv_filename.isEmpty)
                        action {
                            controller.loadFile(csv_filename.value)
                        }
                    }
                }
            }
        }
    }
}

class HelpFragment : Fragment() {
    override val root = borderpane {
        padding = Insets(10.0, 10.0, 10.0, 10.0)
        minWidth = 425.0
        maxWidth = 425.0
        minHeight = 288.0
        maxHeight = 288.0
        center = vbox {
            textflow {
                text("Step 1. Paste the following SQL query into the Athena Query Editor, replacing ") {
                    font = Font(14.0)
                    wrappingWidth = 400.0
                }
                text("DEVICE_ID") {
                    font = Font.font("monospaced", 14.0)
                    wrappingWidth = 400.0
                }
                text(" with the sensor ID") {
                    font = Font(14.0)
                    wrappingWidth = 400.0
                }
            }
            text("Device IDs are of the format XX-XX-XX-XX-XX-XX-XX-XX") {
                font = Font(10.0)
            }

            textarea(SimpleStringProperty().apply { this.value = queryText }) {
                isEditable = false
                font = Font.font("monospaced")
                //this.text("Aaaaaa")
            }
            text("Step 2. Press \"Run query\", wait for execution") {
                font = Font(14.0)
                wrappingWidth = 400.0
            }
            text("Step 3. Download results to CSV") {
                font = Font(14.0)
                wrappingWidth = 400.0
            }
        }
        bottom = button("OK") {
            useMaxWidth = true
            alignment = Pos.CENTER

            action {
                close()
            }
        }
    }
}

class ErrorFragment : Fragment() {
    var errorText = SimpleStringProperty()
    override val root = borderpane {
        padding = Insets(10.0, 10.0, 10.0, 10.0)
        top = text("Error loading CSV") {
            font = Font(18.0)
        }
        center = text("$errorText") {
            font = Font(12.0)
        }
        bottom = button("OK") {
            useMaxWidth = true
            alignment = Pos.CENTER
            action {
                close()
            }
        }
    }
}

class ResultsFragment : Fragment() {
    override val root = borderpane {
        minWidth = 600.0
        padding = Insets(10.0, 10.0, 10.0, 10.0)
        top = text("Parsed data") { font = Font(14.0) }
        center = tableview(globalDatapoints) {
            readonlyColumn("Window", WindowStatisticFmt::window)
            readonlyColumn("Mean", WindowStatisticFmt::mean)
            readonlyColumn("Median", WindowStatisticFmt::median)
            readonlyColumn("Min", WindowStatisticFmt::min)
            readonlyColumn("Max", WindowStatisticFmt::max)
            readonlyColumn("Standard Deviation", WindowStatisticFmt::stddev)

            smartResize()
        }
        bottom = button("Close") {
            useMaxWidth = true
            action {
                close()
            }
        }
    }
}