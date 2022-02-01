/**
 * Author : Mani Shankar Kakumani,
 * Created on : 01 February, 2022.
 */

package com.simpleenergy.staggeredpoc

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.simpleenergy.staggeredpoc.ui.theme.Blue_Range
import com.simpleenergy.staggeredpoc.ui.theme.Green_Battery
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    val showRemoveIcon = mutableStateOf(false)
    val showShimmerEffect = mutableStateOf(false)

    val staggeredList = mutableStateListOf(
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

}