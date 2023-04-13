package org.chsrobotics.dash

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.konyaco.fluent.FluentTheme
import com.konyaco.fluent.background.Layer
import com.konyaco.fluent.background.Mica
import com.konyaco.fluent.component.*
import com.konyaco.fluent.darkColors
import com.konyaco.fluent.lightColors
import com.mayakapps.compose.windowstyler.WindowBackdrop
import com.mayakapps.compose.windowstyler.WindowStyle
import java.util.UUID
import kotlin.math.max

var columns by mutableStateOf(4)
var rows by mutableStateOf(3)

abstract class SpartanWidget {
    abstract val name: String
    val uuid = UUID.randomUUID()
}

class SelectorWidget (
    override val name: String,
    val options: Map<String, String>,
    selected: String
) : SpartanWidget() {
    var selected by mutableStateOf(selected)
}

data class DashLayout internal constructor(
    val columns: Int,
    val rows: Int
) {
    private var _items: MutableMap<SpartanWidget, GridLayout> = mutableMapOf()
    val items: Map<SpartanWidget, GridLayout> get() = _items
    fun place(widget: SpartanWidget, column: Int, row: Int, columnSpan: Int = 1, rowSpan: Int = 1) {
        place(widget, GridLayout(column, row, columnSpan, rowSpan))
    }
    fun place(widget: SpartanWidget, layout: GridLayout) {
        _items[widget] = layout
    }
}

class SpartanDash(
    private val layoutBuilder: DashLayout.() -> Unit
) {
    fun buildLayout(columns: Int, rows: Int): DashLayout {
        val layout = DashLayout(columns, rows)
        layoutBuilder(layout)
        return layout
    }
}

var driveModeWidget = SelectorWidget(
    "Drive Mode",
    mapOf(
        "arcade" to "Arcade",
        "curvature" to "Curvature",
        "mixed" to "Mixed Curvature-Arcade"
    ),
    "arcade"
)

var driveModeWidget2 = SelectorWidget(
    "Drive Mode 2",
    mapOf(
        "arcade" to "Arcade",
        "curvature" to "Curvature",
        "mixed" to "Mixed Curvature-Arcade"
    ),
    "arcade"
)

val dashboard = SpartanDash {
    place(driveModeWidget, 0, 0)
    place(driveModeWidget2, columns-1, rows-1, 1, 1)
}

@Composable
@Preview
fun SpartanDashPage() {
    val layout = dashboard.buildLayout(columns, rows)
    Grid(
        columns = columns,
        rows = rows,
        modifier = Modifier.padding(end = 16.dp, bottom = 16.dp)
    ) {
        layout.items.forEach { (widget, layout) ->
            SpartanWidgetFrame(
                text = widget.name,
                modifier = Modifier.fillMaxSize().grid(layout)
            ) {
                if (widget is SelectorWidget) {
                    SelectorWidgetElement(widget)
                }
            }
        }
    }
}

@Composable
@Preview
fun SpartanWidgetFrame(text: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier.padding(start = 16.dp, top = 16.dp)) {
        Text(text, modifier = Modifier.padding(start = 16.dp, bottom = 6.dp))
        Layer(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, FluentTheme.colors.stroke.control.default)
        ) {
            Column(modifier = Modifier.padding(all = 16.dp)) {
                Box(Modifier.align(Alignment.CenterHorizontally)) {
                    content()
                }
            }
        }
    }
}

@Composable
@Preview
fun SelectorWidgetElement(selector: SelectorWidget) {
    Box {
        fun select(selected: String) {
            selector.selected = selected
        }
        fun selectedName() = selector.options[selector.selected]?:""

        var expanded by remember { mutableStateOf(false) }
        fun close() {
            expanded = false
        }
        fun toggle() {
            expanded = !expanded
        }

        Button(
            onClick = ::toggle,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selectedName())
        }

        DropdownMenu(expanded = expanded, onDismissRequest = ::close) {
            for (option in selector.options) {
                DropdownMenuItem({
                    close()
                    select(option.key)
                }) {
                    Text(option.value)
                }
            }
        }
    }
}

@Composable
fun SpartanTheme(content: @Composable () -> Unit) {
    val darkMode = isSystemInDarkTheme()
    FluentTheme(colors = if (darkMode) darkColors() else lightColors()) {
        content()
    }
}

@Composable
fun SpartanAppBar() {
    Box(Modifier.fillMaxWidth().height(30.dp).background(Color.Red))
}

@Composable
fun SpartanWindow(onCloseRequest: () -> Unit, content: @Composable () -> Unit) {
    val darkMode = isSystemInDarkTheme()
    val customTitleBar = false
    val isWindows = false
    Window(
        onCloseRequest = onCloseRequest,
        transparent = customTitleBar,
        undecorated = customTitleBar
    ) {
        WindowStyle(
            isDarkTheme = !darkMode,
            backdropType = WindowBackdrop.Acrylic(FluentTheme.colors.background.layer.default)
        )

        val density = LocalDensity.current
        val modifier = Modifier.onSizeChanged {
            density.apply {
                columns = max((it.width.toDp() / WIDGET_WIDTH).toInt(), 1)
                rows = max((it.height.toDp() / WIDGET_HEIGHT).toInt(), 1)
            }
        }

        if (customTitleBar) {
            Mica(
                modifier = modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)).padding(5.dp).shadow(3.dp, RoundedCornerShape(10.dp)),
            ) {
                Column {
                    WindowDraggableArea {
                        SpartanAppBar()
                    }
                    content()
                }
            }
        } else {
            if (isWindows) {
                Box(modifier) {
                    content()
                }
            } else {
                Mica(modifier.fillMaxSize()) {
                    content()
                }
            }
        }
    }
}

fun main() = application {
    SpartanTheme {
        SpartanWindow(onCloseRequest = ::exitApplication) {
            SpartanDashPage()
        }
    }
}
