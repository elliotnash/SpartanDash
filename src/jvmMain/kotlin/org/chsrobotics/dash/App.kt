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

@Composable
@Preview
fun SpartanDash() {
    val columns = 4
    val rows = 3

    Row(Modifier.padding(end = 16.dp, bottom = 16.dp)) {
        for (i in 1..columns) {
            Column(Modifier.weight(1f)) {
                for (j in 1..rows) {
                    SpartanWidget(Modifier.weight(1f).fillMaxSize()) {
                        SelectorWidget()
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun SpartanWidget(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Column(modifier.padding(start = 16.dp, top = 16.dp)) {
        Text("Drive Mode", modifier = Modifier.padding(start = 8.dp, bottom = 6.dp))
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
fun SelectorWidget() {
    Box {
        var expanded by remember { mutableStateOf(false) }
        var selected by remember { mutableStateOf("Option 1") }
        fun close() {
            expanded = false
        }
        Button(onClick = {
            expanded = true
        }) {
            Text(selected)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = ::close) {
            DropdownMenuItem({
                close()
                selected = "Option 1"
            }) {
                Text("Option 1")
            }
            DropdownMenuItem({
                close()
                selected = "Option 2"
            }) {
                Text("Option 2")
            }
            DropdownMenuItem({
                close()
                selected = "Option 3"
            }) {
                Text("Option 3")
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

        if (customTitleBar) {
            Mica(
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(10.dp)).padding(5.dp).shadow(3.dp, RoundedCornerShape(10.dp)),
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
                content()
            } else {
                Mica(Modifier.fillMaxSize()) {
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
