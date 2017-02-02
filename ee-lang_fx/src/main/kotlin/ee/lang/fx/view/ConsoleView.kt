package ee.lang.fx.view

import javafx.application.Platform
import javafx.scene.control.ContextMenu
import tornadofx.*


open class ConsoleView : View {
    var usedBy: Any? = null
    var maxLength: Int = 0

    protected val out = textarea {
        isEditable = false

        contextMenu = ContextMenu().apply {
            menuitem("Clear") {
                clear()
            }
        }

        textProperty().addListener { ov, old, new ->
            if (maxLength > 0 && length > maxLength) {
                Platform.runLater { replaceText(0, length - maxLength, "") }
            }
        }
    }

    override val root = borderpane {
        center { add(out) }
    }

    constructor(title: String = "Console", usedBy: Any? = null, maxLength: Int = 10000) : super(title) {
        this.usedBy = usedBy
        this.maxLength = maxLength
    }

    fun clear() {
        Platform.runLater { out.clear() }
    }

    fun print(text: String) {
        Platform.runLater { out.appendText(text) }
    }

    fun println(text: String) {
        Platform.runLater { out.appendText(text + System.lineSeparator()) }
    }

    fun println() {
        Platform.runLater { out.appendText(System.lineSeparator()) }
    }
}
