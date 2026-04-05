package com.dastageer.rento.presentation.shared.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InputFieldsTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun testUnderlineInputField() {
        rule.setContent {
            UnderlineInputField(
                value = "",
                onValueChange = {},
                placeholder = "Ph1"
            )
        }
        rule.onNodeWithText("Ph1").assertExists()
    }
}
