package com.simpleenergy.staggeredpoc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.simpleenergy.staggeredpoc.ui.theme.Blue_Range
import com.simpleenergy.staggeredpoc.ui.theme.Green_Battery
import kotlin.math.max

enum class WidgetType(val height: Int) {
    Battery(190),
    Directions(240),
    Charging(220),
    Range(220),
    NewItem(190)
}

data class StaggeredModel(
    val type: WidgetType,
    val color: Color,
)

val list = listOf(
    StaggeredModel(type = WidgetType.Battery, color = Green_Battery),
    StaggeredModel(type = WidgetType.Range, color = Blue_Range),
    StaggeredModel(type = WidgetType.Directions, color = Color.DarkGray),
    StaggeredModel(type = WidgetType.NewItem, color = Color.White),
    StaggeredModel(type = WidgetType.Range, color = Blue_Range),
    StaggeredModel(type = WidgetType.Directions, color = Color.DarkGray),
    StaggeredModel(type = WidgetType.Battery, color = Green_Battery),
    StaggeredModel(type = WidgetType.Range, color = Blue_Range),
    StaggeredModel(type = WidgetType.Directions, color = Color.DarkGray),
    StaggeredModel(type = WidgetType.Range, color = Green_Battery),
    StaggeredModel(type = WidgetType.Directions, color = Blue_Range),
    StaggeredModel(type = WidgetType.Directions, color = Color.Red),
    StaggeredModel(type = WidgetType.Directions, color = Color.Cyan),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StaggeredUI()
        }
    }


    @Composable
    private fun StaggeredUI() {
        Surface(color = Color.White) {

        }
        val size = remember {
            mutableStateOf(IntSize.Zero)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .onGloballyPositioned {
                    size.value = it.size
                },
            contentAlignment = Alignment.TopCenter
        ) {
            val columns = 2
            StaggeredGridColumn(
                columns = columns
            ) {
                list.forEach {
                    if (it.type == WidgetType.NewItem) {
                        AddNewItemCard(
                            modifier = Modifier
                                .width(with(LocalDensity.current) { (size.value.width / columns).toDp() })
                                .padding(10.dp)
                        )
                    } else {
                        Chip(
                            model = it,
                            modifier = Modifier
                                .width(with(LocalDensity.current) { (size.value.width / columns).toDp() })
                                .padding(10.dp),
                        )
                    }

                }

            }
        }
    }

    @Composable
    fun AddNewItemCard(modifier: Modifier) {
        Card(
            modifier = modifier.height(180.dp),
            border = BorderStroke(color = Color.Black, width = 1.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = 10.dp
        ) {
            ConstraintLayout() {
                val (addIcon) = createRefs()
                Icon(
                    imageVector = Icons.Filled.AddCircle,
                    contentDescription = "SignIn Arrow",
                    modifier = Modifier.size(48.dp).constrainAs(addIcon)
                    {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                    tint = Green_Battery
                )
            }


        }
    }

    @Composable
    fun StaggeredGridColumn(
        modifier: Modifier = Modifier,
        columns: Int = 2,
        content: @Composable () -> Unit,
    ) {
        Layout(content = content, modifier = modifier) { measurables, constraints ->
            val columnWidths = IntArray(columns) { 0 }
            val columnHeights = IntArray(columns) { 0 }

            val placables = measurables.mapIndexed { index, measurable ->
                val placable = measurable.measure(constraints)

                val col = index % columns
                columnHeights[col] += placable.height
                columnWidths[col] = max(columnWidths[col], placable.width)
                placable
            }

            val height = columnHeights.maxOrNull()
                ?.coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))
                ?: constraints.minHeight

            val width =
                columnWidths.sumOf { it }
                    .coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth))

            val colX = IntArray(columns) { 0 }
            for (i in 1 until columns) {
                colX[i] = colX[i - 1] + columnWidths[i - 1]
            }

            layout(width, height) {
                val colY = IntArray(columns) { 0 }
                placables.forEachIndexed { index, placeable ->
                    val col = index % columns
                    placeable.placeRelative(
                        x = colX[col],
                        y = colY[col]
                    )
                    colY[col] += placeable.height
                }
            }
        }

    }


    @Composable
    fun Chip(modifier: Modifier = Modifier, model: StaggeredModel) {

        Card(
            backgroundColor = model.color,
            modifier = modifier.height(model.type.height.dp),
//            border = BorderStroke(color = Color.Black, width = 1.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = 10.dp
        ) {
            ConstraintLayout(

            ) {
                val (h1, h2, signInButton) = createRefs()


                Text(
                    modifier = Modifier.constrainAs(h1) {
                        top.linkTo(parent.top, 24.dp)
                        start.linkTo(parent.start, 21.dp)
                    },
                    color = Color.Black,
                    fontSize = 16.sp,
                    text = "model.h1",
                    style = TextStyle(color = Color.DarkGray, textAlign = TextAlign.Center)
                )



                Text(
                    modifier = Modifier.constrainAs(h1) {
                        bottom.linkTo(parent.bottom, 17.dp)
                        start.linkTo(parent.start, 0.dp)
                        end.linkTo(parent.end, 0.dp)
                    },
                    color = Color.Black,
                    fontSize = 20.sp,
                    text = "model.h1",
                    style = TextStyle(color = Color.DarkGray, textAlign = TextAlign.Center)
                )
            }
        }
    }

}
