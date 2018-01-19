package ee.lang.fx.view

import javafx.application.Platform
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import tornadofx.borderpane
import tornadofx.bottom
import tornadofx.center
import tornadofx.textfield
import java.util.*
import java.util.function.Consumer

class ConsoleInputView : ConsoleView("ConsoleInputView") {
    private val history: MutableList<String> = ArrayList()
    private var historyPointer = 0
    private var onMessageReceivedHandler: Consumer<String>? = null

    private val input = textfield {
        addEventHandler(KeyEvent.KEY_RELEASED) { keyEvent ->
            when (keyEvent.code) {
                KeyCode.ENTER -> {
                    val text = text
                    out.appendText(text + System.lineSeparator())
                    history.add(text)
                    historyPointer++
                    if (onMessageReceivedHandler != null) {
                        onMessageReceivedHandler!!.accept(text)
                    }
                    clear()
                }
                KeyCode.UP    -> {
                    if (historyPointer > 0) {
                        historyPointer--
                        Platform.runLater {
                            text = history[historyPointer]
                            selectAll()
                        }
                    }
                }
                KeyCode.DOWN  -> {
                    if (historyPointer < history.size - 1) {
                        historyPointer++
                        Platform.runLater {
                            text = history[historyPointer]
                            selectAll()
                        }
                    }
                }
                else          -> {
                }
            }
        }
    }

    override val root = borderpane {
        center { add(out) }
        bottom { add(input) }
    }
}
