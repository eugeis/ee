package ee.lang.fx.view

import javafx.application.Platform
import tornadofx.View
import tornadofx.tab
import tornadofx.tabpane


open class OutputContainerView(name: String = "OutputContainerView") : View(name) {
    protected val views: MutableList<ConsoleView> = arrayListOf()
    override val root = tabpane {}

    fun takeConsole(caller: Any, title: String): ConsoleView {
        return synchronized(views) {
            var view = views.find { it.usedBy == null }
            if (view == null) {
                val newView = ConsoleView(title, caller)
                views.add(newView)
                Platform.runLater {
                    with(root) {
                        val tab = tab(title, newView.root)
                        newView.titleProperty.addListener { observable, old, new ->
                            Platform.runLater {
                                tab.text = new
                            }
                        }
                        tab.setOnCloseRequest {
                            views.remove(newView)
                        }
                    }
                }
                view = newView
            } else {
                view.title = title
                view.usedBy = caller
            }
            view
        }
    }

    fun release(view: ConsoleView) {
        view.usedBy = null
    }
}
