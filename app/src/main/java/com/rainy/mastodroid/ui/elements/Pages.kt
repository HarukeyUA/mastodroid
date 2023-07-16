/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.ui.elements

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetpack.pages.PagesScrollAnimation
import com.arkivanov.decompose.router.pages.ChildPages

@OptIn(ExperimentalFoundationApi::class, ExperimentalDecomposeApi::class)
@Composable
fun <T : Any> Pages(
    pages: ChildPages<*, T>,
    onPageSelected: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
    scrollAnimation: PagesScrollAnimation = PagesScrollAnimation.Disabled,
    pagerState: PagerState = rememberPagerState(initialPage = pages.selectedIndex, pageCount = {
        pages.items.size
    }),
    pageContent: @Composable (index: Int, page: T) -> Unit,
) {
    val selectedIndex = pages.selectedIndex

    LaunchedEffect(selectedIndex) {
        if (pagerState.currentPage != selectedIndex) {
            when (scrollAnimation) {
                is PagesScrollAnimation.Disabled -> pagerState.scrollToPage(selectedIndex)
                is PagesScrollAnimation.Default -> pagerState.animateScrollToPage(page = selectedIndex)
                is PagesScrollAnimation.Custom -> pagerState.animateScrollToPage(
                    page = selectedIndex,
                    animationSpec = scrollAnimation.spec
                )
            }
        }
    }

    DisposableEffect(pagerState.settledPage) {
        onPageSelected(pagerState.settledPage)
        onDispose {}
    }

    val items = pages.items

    HorizontalPager(
        modifier = modifier.fillMaxSize(),
        state = pagerState,
        key = { items[it].configuration },
    ) { pageIndex ->
        items[pageIndex].instance?.also { page ->
            pageContent(pageIndex, page)
        }
    }
}