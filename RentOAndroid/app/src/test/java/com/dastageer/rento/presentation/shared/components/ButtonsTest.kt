package com.dastageer.rento.presentation.shared.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ButtonsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testPrimaryButton() {
        var clicked = false
        composeTestRule.setContent {
            PrimaryButton(text = "Click Me", onClick = { clicked = true })
        }
        composeTestRule.onNodeWithText("Click Me").assertExists()
        composeTestRule.onNodeWithText("Click Me").performClick()
        assert(clicked)
    }
}
