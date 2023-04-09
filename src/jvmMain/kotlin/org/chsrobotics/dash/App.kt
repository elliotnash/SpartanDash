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

var columns by mutableStateOf(4)
var rows by mutableStateOf(3)

@Composable
@Preview
fun SpartanDash() {
    Row(Modifier.padding(end = 16.dp, bottom = 16.dp)) {
        for (i in 1..columns) {
            Column(Modifier.weight(1f)) {
                for (j in 1..rows) {
                    SpartanWidget(
                        text = "Drive Mode",
                        modifier = Modifier.weight(1f).fillMaxSize()
                    ) {
                        SelectorElement()
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun SpartanWidget(text: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
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

data class SelectorWidget(
    val options: Map<String, String>,
    val selected: String
)

@Composable
@Preview
fun SelectorElement() {
    Box {
        var selector by remember { mutableStateOf(SelectorWidget(
            mapOf(
                "arcade" to "Arcade",
                "curvature" to "Curvature",
                "mixed" to "Mixed Curvature-Arcade"
            ),
            "arcade"
        )) }

        fun select(selected: String) {
            selector = selector.copy(selected = selected)
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

        val modifier = Modifier.onSizeChanged {
            columns = it.width / 500
            rows = it.height / 350
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
            SpartanDash()
        }
    }
}
