package com.dastageer.rento.presentation.shared.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dastageer.rento.R
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.cardGradientForIndex

@Immutable
data class BannerSlide(
    val id: String,
    val title: String,
    val subtitle: String,
    val ctaText: String,
    val gradientIndex: Int,
    val emoji: String,
    val linkedListingId: String?,
)

@Composable
fun HomeBannerSlider(
    slides: List<BannerSlide>,
    onSlideTap: (listingId: String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalRentoColors.current
    val pagerState = rememberPagerState(pageCount = { slides.size })

    // Auto-advance
    LaunchedEffect(pagerState) {
        if (slides.isNotEmpty()) {
            while (true) {
                kotlinx.coroutines.delay(3200)
                if (!pagerState.isScrollInProgress) {
                    val nextPage = (pagerState.currentPage + 1) % slides.size
                    pagerState.animateScrollToPage(nextPage)
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .height(162.dp)
            .clip(RoundedCornerShape(22.dp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val slide = slides[page]
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(cardGradientForIndex(slide.gradientIndex))
                    .clickable { onSlideTap(slide.linkedListingId) }
            ) {
                // Emoji watermark
                Text(
                    text = slide.emoji,
                    fontSize = 70.sp,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp, end = 10.dp)
                        .graphicsLayer { alpha = 0.12f }
                )

                // FEATURED pill
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 14.dp, start = 14.dp)
                        .background(Color(0x2B2ECC8A), RoundedCornerShape(100.dp))
                        .border(1.dp, Color(0x542ECC8A), RoundedCornerShape(100.dp))
                        .padding(vertical = 3.dp, horizontal = 11.dp)
                ) {
                    Text(
                        text = stringResource(R.string.banner_featured_label),
                        color = colors.primary2,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.08.sp
                    )
                }

                // Content column
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, bottom = 14.dp)
                ) {
                    Text(
                        text = slide.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE4F4EC),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = slide.subtitle,
                        fontSize = 13.sp,
                        color = Color(0x94E4F4EC),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // CTA text
                        Text(
                            text = "${slide.ctaText} →",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary2
                        )

                        // Dots
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            slides.indices.forEach { index ->
                                val active = index == pagerState.currentPage
                                val dotWidth by animateDpAsState(
                                    targetValue = if (active) 20.dp else 6.dp,
                                    animationSpec = tween(320),
                                    label = "dotWidth"
                                )
                                
                                Box(
                                    modifier = Modifier
                                        .size(width = dotWidth, height = 6.dp)
                                        .clip(RoundedCornerShape(100.dp))
                                        .background(if (active) colors.primary else Color(0x47E4F4EC))
                                        .clickable {
                                            // Optional: allow tapping dots to navigate (best effort via coroutine launch)
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
