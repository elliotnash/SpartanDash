package org.chsrobotics.dash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density

interface GridScope {
    @Stable
    fun Modifier.grid(column: Int, row: Int, columnSpan: Int = 1, rowSpan: Int = 1) = this.then(
        GridLayout(column, row, columnSpan, rowSpan)
    )
    @Stable
    fun Modifier.grid(layout: GridLayout) = this.then(layout)

    companion object : GridScope
}

data class GridLayout(
    val column: Int,
    val row: Int,
    val columnSpan: Int,
    val rowSpan: Int,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any = this@GridLayout
}

private val Measurable.gridData: GridLayout?
    get() = parentData as? GridLayout

private val Measurable.columnSpan: Int
    get() = gridData?.columnSpan ?: 1

private val Measurable.rowSpan: Int
    get() = gridData?.rowSpan ?: 1

data class GridInfo(
    val numChildren: Int,
    val columnSpan: Int,
    val rowSpan: Int,
)

@Composable
fun Grid(
    columns: Int,
    rows: Int,
    modifier: Modifier = Modifier,
    content: @Composable GridScope.() -> Unit,
) {
    check(columns > 0) { "Columns must be greater than 0" }
    check(rows > 0) { "Rows must be greater than 0" }
    Layout(
        content = { GridScope.content() },
        modifier = modifier,
    ) { measurables, constraints ->
        val standardGrid = GridLayout(0, 0, 1, 1)
        val spans = measurables.map { measurable -> measurable.gridData ?: standardGrid }

        // build constraints
        val baseConstraints = Constraints.fixed(
            width = constraints.maxWidth / columns,
            height = constraints.maxHeight / rows,
        )
        val cellConstraints = measurables.map { measurable ->
            val columnSpan = measurable.columnSpan
            val rowSpan = measurable.rowSpan
            Constraints.fixed(
                width = baseConstraints.maxWidth * columnSpan,
                height = baseConstraints.maxHeight * rowSpan
            )
        }

        // measure children
        val placeables = measurables.mapIndexed { index, measurable ->
            measurable.measure(cellConstraints[index])
        }

        layout(
            width = constraints.maxWidth,
            height = constraints.maxHeight,
        ) {
            placeables.forEachIndexed { i, placeable ->
                placeable.placeRelative(
                    x = spans[i].column * baseConstraints.maxWidth,
                    y = spans[i].row * baseConstraints.maxHeight
                )
            }
        }
    }
}
