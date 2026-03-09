package com.dastageer.rento.presentation.shared.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dastageer.rento.R
import com.dastageer.rento.presentation.shared.animations.BounceEffect
import com.dastageer.rento.presentation.shared.icons.RentoIcons
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoTypography
import com.dastageer.rento.presentation.shared.theme.cardGradientForIndex
import com.dastageer.rento.presentation.shared.theme.gradientImageOverlay
import java.text.NumberFormat
import java.util.Locale

@Immutable
data class PropertyCardData(
    val id: String,
    val title: String,
    val area: String,
    val city: String,
    val price: Int,
    val beds: Int,
    val baths: Int,
    val propertyType: String,
    val intent: String,
    val furnished: String,
    val imageUrls: List<String>,
    val cardGradientIndex: Int,
    val emoji: String,
    val isSaved: Boolean,
)

@Composable
fun PropertyCard(
    data: PropertyCardData,
    onClick: () -> Unit,
    onSaveToggle: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val colors = LocalRentoColors.current
    val typography = LocalRentoTypography.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.985f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )

    val imageHeight = if (compact) 108.dp else 190.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(24.dp))
            .background(colors.bg2)
            .border(1.5.dp, colors.border, RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = androidx.compose.material3.ripple(color = colors.t1),
                onClick = onClick
            )
    ) {
        // IMAGE AREA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .background(cardGradientForIndex(data.cardGradientIndex))
        ) {
            // Emoji watermark
            Text(
                text = data.emoji,
                fontSize = if (compact) 50.sp else 72.sp,
                modifier = Modifier
                    .align(Alignment.Center)
                    .graphicsLayer { alpha = 0.10f }
            )

            // AsyncImage if URL exists
            if (data.imageUrls.isNotEmpty()) {
                AsyncImage(
                    model = data.imageUrls.first(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Overlay gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(gradientImageOverlay(colors))
            )

            // TOP-LEFT — Type badge
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 14.dp, start = 14.dp)
                    .background(Color(0x8C04100C), RoundedCornerShape(100.dp))
                    .border(1.dp, Color(0x332ECC8A), RoundedCornerShape(100.dp))
                    .padding(vertical = 3.dp, horizontal = 11.dp)
            ) {
                Text(
                    text = data.propertyType,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // TOP-RIGHT — Heart button
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 14.dp, end = 14.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color(0x8C04100C))
                    .clickable(onClick = onSaveToggle),
                contentAlignment = Alignment.Center
            ) {
                BounceEffect(trigger = data.isSaved) { animModifier ->
                    Icon(
                        imageVector = if (data.isSaved) RentoIcons.HeartFilled else RentoIcons.Heart,
                        contentDescription = "Save property",
                        tint = if (data.isSaved) colors.primary else Color.White,
                        modifier = animModifier.size(18.dp)
                    )
                }
            }

            // BOTTOM-LEFT — Location row
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(bottom = 14.dp, start = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = RentoIcons.Pin,
                    contentDescription = null,
                    tint = colors.primary2.copy(alpha = 0.9f),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${data.area}, ${data.city}",
                    fontSize = 12.sp,
                    color = Color(0xD1E4F4EC)
                )
            }
        }

        // CONTENT AREA
        Column(
            modifier = Modifier.padding(
                top = if (compact) 10.dp else 14.dp,
                start = if (compact) 13.dp else 16.dp,
                end = if (compact) 13.dp else 16.dp,
                bottom = if (compact) 14.dp else 16.dp
            )
        ) {
            Text(
                text = data.title,
                fontSize = if (compact) 12.sp else 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.t0,
                lineHeight = if (compact) 16.2.sp else 19.5.sp, // ~1.35 and ~1.3
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(bottom = if (compact) 4.dp else 10.dp)
            )

            val priceFormatted = NumberFormat.getNumberInstance(Locale("en", "PK")).format(data.price)

            if (compact) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${data.price / 1000}k",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                    Text(
                        text = " PKR/mo",
                        fontSize = 10.sp,
                        color = colors.t2,
                        modifier = Modifier.padding(bottom = 1.dp)
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(RentoIcons.Bed, contentDescription = null, tint = colors.t2, modifier = Modifier.size(14.dp))
                            Text("${data.beds} bed", fontSize = 12.5.sp, color = colors.t2)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(RentoIcons.Bath, contentDescription = null, tint = colors.t2, modifier = Modifier.size(14.dp))
                            Text("${data.baths} bath", fontSize = 12.5.sp, color = colors.t2)
                        }
                    }
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = priceFormatted,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                        Text(
                            text = " PKR/mo",
                            fontSize = 11.sp,
                            color = colors.t2,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    RentoBadge(text = data.intent, variant = BadgeVariant.PRIMARY)
                    RentoBadge(text = data.furnished, variant = BadgeVariant.NEUTRAL)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PropertyCardPreview() {
    val sampleData = PropertyCardData(
        id = "1",
        title = "Modern 2-bed apartment in DHA Phase 6 with pool",
        area = "DHA Phase 6",
        city = "Lahore",
        price = 145000,
        beds = 2,
        baths = 2,
        propertyType = "Apartment",
        intent = "Full Rent",
        furnished = "Furnished",
        imageUrls = emptyList(),
        cardGradientIndex = 1,
        emoji = "🏢",
        isSaved = false
    )
    com.dastageer.rento.presentation.shared.theme.RentoTheme(darkTheme = true) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            PropertyCard(data = sampleData, onClick = {}, onSaveToggle = {})
            Row {
                Box(modifier = Modifier.weight(1f)) {
                    PropertyCard(data = sampleData, onClick = {}, onSaveToggle = {}, compact = true)
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
