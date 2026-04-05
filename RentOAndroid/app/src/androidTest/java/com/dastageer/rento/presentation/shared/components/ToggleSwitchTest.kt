package com.dastageer.rento.presentation.shared.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ToggleSwitchTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun testToggleSwitch() {
        rule.setContent {
            ToggleSwitch(
                checked = false,
                onCheckedChange = { }
            )
        }
    }
}
