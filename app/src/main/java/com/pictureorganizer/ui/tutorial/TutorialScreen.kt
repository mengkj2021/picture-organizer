package com.pictureorganizer.ui.tutorial

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pictureorganizer.R
import com.pictureorganizer.ui.theme.PictureOrganizerTheme
import kotlinx.coroutines.launch

private data class TutorialPage(
    @StringRes val titleRes: Int,
    @StringRes val bodyRes: Int,
)

private val tutorialPages =
    listOf(
        TutorialPage(R.string.tutorial_page1_title, R.string.tutorial_page1_body),
        TutorialPage(R.string.tutorial_page2_title, R.string.tutorial_page2_body),
        TutorialPage(R.string.tutorial_page3_title, R.string.tutorial_page3_body),
    )

@Composable
fun TutorialScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pagerState = rememberPagerState(pageCount = { tutorialPages.size })
    val scope = rememberCoroutineScope()
    val lastIndex = tutorialPages.lastIndex

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onFinished) {
                Text(stringResource(R.string.tutorial_skip))
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
        ) { page ->
            val item = tutorialPages[page]
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(item.titleRes),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(item.bodyRes),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(tutorialPages.size) { index ->
                val selected = pagerState.currentPage == index
                Surface(
                    modifier =
                        Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (selected) 10.dp else 8.dp),
                    shape = CircleShape,
                    color =
                        if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outlineVariant
                        },
                ) {}
            }
        }

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (pagerState.currentPage < lastIndex) {
                Button(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                ) {
                    Text(stringResource(R.string.tutorial_next))
                }
            } else {
                Button(onClick = onFinished) {
                    Text(stringResource(R.string.tutorial_start))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TutorialScreenPreview() {
    PictureOrganizerTheme {
        TutorialScreen(onFinished = {})
    }
}
