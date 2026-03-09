package com.dastageer.rento.presentation.shared.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dastageer.rento.presentation.shared.animations.RentoAnimations
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoDimens
import com.dastageer.rento.presentation.shared.theme.LocalRentoTypography
import com.dastageer.rento.presentation.shared.theme.RentoShapes

/**
 * UnderlineInputField — transparent bg, 2dp bottom line only.
 * Idle: DarkIpl underline. Focused: DarkPri underline. 220ms transition.
 */
@Composable
fun UnderlineInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
) {
    val colors = LocalRentoColors.current
    val typo = LocalRentoTypography.current
    val dimens = LocalRentoDimens.current
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val underlineColor by animateColorAsState(
        targetValue = if (isFocused) colors.primary else colors.inputUnderlineIdle,
        animationSpec = tween(RentoAnimations.CHIP_TRANSITION_DURATION),
        label = "underlineColor",
    )

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val strokeWidth = dimens.inputUnderlineWidth.toPx()
                drawLine(
                    color = underlineColor,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = strokeWidth,
                )
            }
            .padding(vertical = 10.dp),
        textStyle = typo.bodyL.copy(color = colors.t0, fontSize = 15.sp),
        cursorBrush = SolidColor(colors.primary),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        visualTransformation = visualTransformation,
        singleLine = singleLine,
        interactionSource = interactionSource,
        decorationBox = { innerTextField ->
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = typo.bodyL.copy(color = colors.t3, fontSize = 15.sp),
                    )
                }
                innerTextField()
            }
        },
    )
}

/**
 * BoxedInputField — DarkBg2 fill, DarkBd border, 16dp corner.
 * Leading icon + optional trailing icon.
 */
@Composable
fun BoxedInputField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    onTrailingIconClick: (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    singleLine: Boolean = true,
) {
    val colors = LocalRentoColors.current
    val typo = LocalRentoTypography.current
    val dimens = LocalRentoDimens.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RentoShapes.inputWrapper)
            .background(colors.bg2)
            .border(dimens.inputBorderWidth, colors.border, RentoShapes.inputWrapper)
            .padding(vertical = dimens.inputPadV, horizontal = dimens.inputPadH),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = colors.t2,
            )
            Spacer(modifier = Modifier.width(12.dp))
        }

        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            textStyle = typo.bodyL.copy(color = colors.t0, fontSize = 15.sp),
            cursorBrush = SolidColor(colors.primary),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            visualTransformation = visualTransformation,
            singleLine = singleLine,
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = typo.bodyL.copy(color = colors.t3, fontSize = 15.sp),
                        )
                    }
                    innerTextField()
                }
            },
        )

        if (trailingIcon != null) {
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { onTrailingIconClick?.invoke() },
                modifier = Modifier.size(24.dp),
            ) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = colors.t2,
                )
            }
        }
    }
}
