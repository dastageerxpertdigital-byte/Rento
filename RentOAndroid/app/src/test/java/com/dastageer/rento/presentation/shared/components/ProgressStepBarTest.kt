package com.dastageer.rento.presentation.shared.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProgressStepBarTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun testProgressStepBar() {
        rule.setContent { 
            ProgressStepBar(
                steps = listOf("Step1", "Step2"),
                currentStep = 0
            ) 
        }
        rule.onNodeWithText("Step1").assertExists()
    }
}
