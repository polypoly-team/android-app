package com.github.polypoly.app.ui.theme

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material.TabPosition
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.pagerTabIndicatorOffset
import kotlinx.coroutines.launch

/**
 * A custom [ScrollableTabRow] that uses a custom indicator
 *
 * @param tabs The list of [Tab]s to display on the [ScrollableTabRow]
 * @param state The [PagerState] used to control the currently displayed tab
 *
 * @see CustomIndicator
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun CustomTabRow(tabs: List<String>, state: PagerState) {
    /**
     * If the custom indicator breaks, we can fall back to this standard one
     */
    @Suppress("UNUSED_VARIABLE")
    val defaultIndicator = @Composable { tabPositions: List<TabPosition> ->
        TabRowDefaults.Indicator(
            Modifier.pagerTabIndicatorOffset(state, tabPositions)
        )
    }

    val indicator = @Composable { tabPositions: List<TabPosition> ->
        CustomIndicator(tabPositions, state)
    }

    val coroutineScope = rememberCoroutineScope()

    ScrollableTabRow(
        modifier = Modifier
            .height(50.dp)
            .fillMaxSize()
            .testTag("tab_row"),
        selectedTabIndex = state.currentPage,
        indicator = indicator,
        backgroundColor = MaterialTheme.colors.primary,
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                modifier = Modifier.zIndex(6f),
                text = { Text(
                    text = title,
                    style = MaterialTheme.typography.button,
                    color = if (state.currentPage == index)
                        /**
                         * When the text is the one of the current page, the indicator of secondary
                         * lays behind it
                         */
                        MaterialTheme.colors.onSecondary else
                        /**
                         * Otherwise the text is on the menu bar which is of primary color
                         */
                        MaterialTheme.colors.onPrimary
                ) },
                selected = state.currentPage == index,
                onClick = { coroutineScope.launch { state.animateScrollToPage(index) } },
            )
        }
    }
}

/**
 * The custom indicator used in the [CustomTabRow]
 *
 * @param tabPositions The list of [TabPosition]s for each tab
 * @param state The [PagerState] used to drive the indicator
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
private fun CustomIndicator(tabPositions: List<TabPosition>, state: PagerState) {
    val transition = updateTransition(state.currentPage, label = "indicator_transition")

    // The indicator's start transition
    val indicatorStart by transition.animateDp(
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 50f)
            } else {
                spring(dampingRatio = 1f, stiffness = 1000f)
            }
        }, label = "indicator_start_transition"
    ) {
        tabPositions[it].left
    }

    // The indicator's end transition
    val indicatorEnd by transition.animateDp(
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 1000f)
            } else {
                spring(dampingRatio = 1f, stiffness = 50f)
            }
        }, label = "indicator_end_transition"
    ) {
        tabPositions[it].right
    }

    // The indicator
    Box(
        Modifier
            .offset(x = indicatorStart)
            .wrapContentSize(align = Alignment.BottomStart)
            .width(indicatorEnd - indicatorStart)
            .padding(Padding.medium)
            .fillMaxSize(1f)
            .background(color = MaterialTheme.colors.secondaryVariant, shape = CircleShape)
            .zIndex(1f)
    )
}