package com.dastageer.rento.presentation.shared.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TabPillTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun testTabPill_SelectsCorrectTab() {
        var selectedIndex = 0
        rule.setContent { 
            TabPill(
                tabs = listOf("One", "Two"),
                selectedIndex = 0,
                onTabSelected = { selectedIndex = it }
            ) 
        }
        rule.onNodeWithText("Two").performClick()
        assert(selectedIndex == 1)
    }
}
