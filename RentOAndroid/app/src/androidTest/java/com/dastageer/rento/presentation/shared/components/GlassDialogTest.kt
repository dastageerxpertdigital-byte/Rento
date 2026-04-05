package com.dastageer.rento.presentation.shared.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dastageer.rento.presentation.shared.icons.RentoIcons
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GlassDialogTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun testGlassDialog() {
        rule.setContent {
            GlassDialog(
                title = "Title",
                body = "Desc",
                visible = true,
                phase = GlassDialogPhase.CONFIRM,
                iconVector = RentoIcons.Check,
                iconCircleColor = Color(0xFF4CAF50),
                confirmText = "Ok",
                cancelText = "Cancel",
                successTitle = "Success",
                errorTitle = "Error",
                onConfirm = {},
                onCancel = {},
                onRetry = {},
            )
        }
        rule.onNodeWithText("Title").assertExists()
    }
}
