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
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dastageer.rento.presentation.shared.icons.RentoIcons
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors

@Immutable
data class TenantRequestCardData(
    val id: String,
    val requesterName: String,
    val intent: String,
    val propertyType: String,
    val minBeds: Int,
    val minBaths: Int,
    val preferredAreas: List<String>,
    val radiusKm: Int,
    val budgetMax: Int,
    val moveInDate: String,
)

@Composable
fun TenantRequestCard(
    data: TenantRequestCardData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalRentoColors.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.985f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "cardScale"
    )

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
            .padding(18.dp)
            .semantics {
                contentDescription = "Request by ${data.requesterName}, budget up to ${data.budgetMax} PKR"
            }
    ) {
        // TOP ROW
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(colors.primaryTint)
                        .border(1.5.dp, colors.primaryRing, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = RentoIcons.User,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = colors.primary
                    )
                }
                
                // Name & Move-in Date
                Column {
                    Text(
                        text = data.requesterName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.t0
                    )
                    Text(
                        text = "Needs by ${data.moveInDate}",
                        fontSize = 12.sp,
                        color = colors.t2,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            
            // Intent Badge
            RentoBadge(text = data.intent, variant = BadgeVariant.BLUE)
        }

        // MIDDLE BOX
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
                .background(colors.bg3, RoundedCornerShape(14.dp))
                .padding(vertical = 12.dp, horizontal = 14.dp)
        ) {
            SectionLabel(text = "Looking For", modifier = Modifier.padding(bottom = 4.dp))
            Text(
                text = data.propertyType,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.t0,
            )
            Text(
                text = "${data.minBeds} Bed • ${data.minBaths} Bath",
                fontSize = 12.sp,
                color = colors.t1,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // BOTTOM ROW
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Location
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = RentoIcons.Pin,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(14.dp)
                )
                val areas = data.preferredAreas.take(2).joinToString()
                Text(
                    text = "$areas (+${data.radiusKm}km)",
                    fontSize = 12.sp,
                    color = colors.t1
                )
            }
            
            // Budget
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Up to",
                    fontSize = 11.sp,
                    color = colors.t2
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "${data.budgetMax / 1000}k",
                        fontSize = 18.sp,
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
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TenantRequestCardPreview() {
    val sampleData = TenantRequestCardData(
        id = "1",
        requesterName = "Ahmad R.",
        intent = "Rent",
        propertyType = "Apartment",
        minBeds = 2,
        minBaths = 1,
        preferredAreas = listOf("DHA", "Gulberg"),
        radiusKm = 5,
        budgetMax = 80000,
        moveInDate = "Oct 15",
    )
    com.dastageer.rento.presentation.shared.theme.RentoTheme(darkTheme = true) {
        Column(modifier = Modifier.padding(16.dp)) {
            TenantRequestCard(data = sampleData, onClick = {})
        }
    }
}
