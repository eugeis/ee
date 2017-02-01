package ee.design.fx.view

import javafx.application.Platform
import javafx.geometry.Orientation
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import tornadofx.*

class ModelMainView : View("ee") {
    override val root = VBox()
    val composite: CompositeView by inject()
    val tasksView: TasksView by inject()
    val outputContainerView: OutputContainerView by inject()
    val optionsContainerView: OptionsContainerView by inject()

    init {
        with(root) {
            menubar {
                menu("File") { menuitem("Close") { Platform.exit() } }
                menu("Edit") { }
                menu("Help") { }
            }
            splitpane {
                splitpane {
                    stackpane(listOf(composite.root))
                    stackpane(listOf(tasksView.root))
                    stackpane(listOf(optionsContainerView.root))
                    orientation = Orientation.HORIZONTAL
                    setDividerPositions(0.3, 0.8)
                }
                stackpane(listOf(outputContainerView.root))
                orientation = Orientation.VERTICAL
                setDividerPositions(0.7, 0.3)
                vboxConstraints { vGrow = Priority.ALWAYS }
            }
            hbox { label("Status") }
            prefHeight = 600.0
            prefWidth = 900.0
        }
    }
}