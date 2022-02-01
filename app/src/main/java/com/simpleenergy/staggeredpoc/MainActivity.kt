package com.simpleenergy.staggeredpoc

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.simpleenergy.staggeredpoc.ui.theme.Green_Battery
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import com.valentinilk.shimmer.unclippedBoundsInWindow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
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


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StaggeredUI()
            changeUIState()
        }
    }


    private fun changeUIState() {
        val viewModel: MainViewModel by viewModels()
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.showShimmerEffect.value = true
            delay(10000)
            viewModel.showShimmerEffect.value = false
        }
    }


    @Composable
    private fun StaggeredUI() {
        val viewModel: MainViewModel = viewModel()

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
                viewModel.staggeredList.forEachIndexed { index, staggeredModel ->
                    if (staggeredModel.type == WidgetType.NewItem) {
                        AddNewItemCard(
                            modifier = Modifier
                                .width(with(LocalDensity.current) { (size.value.width / columns).toDp() })
                                .padding(10.dp)
                        )
                    } else {
                        Chip(
                            index,
                            model = staggeredModel,
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

        val viewModel: MainViewModel = viewModel()
        if (viewModel.showShimmerEffect.value) {
            showCardShimmer(modifier)
        } else {
            hideCardShimmer(modifier)
        }
    }

    @Composable
    private fun hideCardShimmer(modifier: Modifier) {

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
                    modifier = Modifier
                        .size(48.dp)
                        .constrainAs(addIcon)
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
    private fun showCardShimmer(modifier: Modifier) {
        val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.Custom)
        Card(
            modifier = modifier
                .height(180.dp)
                .verticalScroll(rememberScrollState())
                .onGloballyPositioned { layoutCoordinates ->
                    // Util function included in the library
                    val position = layoutCoordinates.unclippedBoundsInWindow()
                    shimmerInstance
                        .updateBounds(position)
                }
                .shimmer(shimmerInstance)
                .shadow(0.dp),
            backgroundColor = Color.LightGray,
            shape = RoundedCornerShape(20.dp),

            ) {}

    }

    @Composable
    fun Chip(
        index: Int, modifier: Modifier = Modifier, model: StaggeredModel
    ) {

        val viewModel: MainViewModel = viewModel()
        val haptic = LocalHapticFeedback.current

        if (viewModel.showShimmerEffect.value) {
            showShimmer(index, modifier, model, haptic, viewModel)
        } else {
            hideShimmer(index, modifier, model, haptic, viewModel)
        }

    }

    @Composable
    private fun hideShimmer(
        index: Int,
        modifier: Modifier,
        model: StaggeredModel,
        haptic: HapticFeedback,
        viewModel: MainViewModel
    ) {

        Card(
            backgroundColor = model.color,
            modifier = modifier
                .height(model.type.height.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            Log.d("TAG_LONG", index.toString())
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.showRemoveIcon.value = !viewModel.showRemoveIcon.value
                        }
                    )
                },
//            border = BorderStroke(color = Color.Black, width = 1.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = 10.dp
        ) {
            ConstraintLayout(

            ) {
                val (h1, h2, signInButton, removeButton) = createRefs()

                if (viewModel.showRemoveIcon.value) {
                    Image(
                        modifier = Modifier
                            .clickable {
                                viewModel.staggeredList.removeAt(index)
                            }
                            .constrainAs(removeButton) {
                                end.linkTo(parent.end, 5.dp)
                                top.linkTo(parent.top, 5.dp)
                            },
                        painter = painterResource(id = R.drawable.ic_round_remove_circle),
                        contentDescription = null
                    )
                }

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

    @Composable
    private fun showShimmer(
        index: Int,
        modifier: Modifier,
        model: StaggeredModel,
        haptic: HapticFeedback,
        viewModel: MainViewModel
    ) {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.Custom)

        Card(
            backgroundColor = Color.LightGray,
            modifier = modifier
                .height(model.type.height.dp)
                .verticalScroll(rememberScrollState())
                .onGloballyPositioned { layoutCoordinates ->
                    // Util function included in the library
                    val position = layoutCoordinates.unclippedBoundsInWindow()
                    shimmerInstance
                        .updateBounds(position)
                }
                .shimmer(shimmerInstance)
                .shimmer()
                .shadow(0.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            Log.d("TAG_LONG", index.toString())
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.showRemoveIcon.value = !viewModel.showRemoveIcon.value
                        }
                    )
                },
//            border = BorderStroke(color = Color.Black, width = 1.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = 10.dp
        ) {
            ConstraintLayout(

            ) {
                val (h1, h2, signInButton, removeButton) = createRefs()

                if (viewModel.showRemoveIcon.value) {
                    Image(
                        modifier = Modifier
                            .clickable {
                                viewModel.staggeredList.removeAt(index)
                            }
                            .constrainAs(removeButton) {
                                end.linkTo(parent.end, 5.dp)
                                top.linkTo(parent.top, 5.dp)
                            },
                        painter = painterResource(id = R.drawable.ic_round_remove_circle),
                        contentDescription = null
                    )
                }

                Text(
                    modifier = Modifier.constrainAs(h1) {
                        top.linkTo(parent.top, 24.dp)
                        start.linkTo(parent.start, 21.dp)
                    },
                    color = Color.Black,
                    fontSize = 16.sp,
                    text = "model.h1",
//                    style = TextStyle(color = Color.DarkGray, textAlign = TextAlign.Center)
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
//                    style = TextStyle(color = Color.DarkGray, textAlign = TextAlign.Center)
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


}
