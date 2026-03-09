package com.dastageer.rento.presentation.shared.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RentoChipTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun testRentoChip() {
        var clicked = false
        rule.setContent { RentoChip(text = "Chip", isSelected = false, onClick = { clicked = true }) }
        rule.onNodeWithText("Chip").performClick()
        assert(clicked)
    }

    @Test
    fun testRentoChip_Disabled() {
        var clicked = false
        rule.setContent { RentoChip(text = "Chip Disabled", isSelected = false, isEnabled = false, onClick = { clicked = true }) }
        rule.onNodeWithText("Chip Disabled").performClick()
        assert(!clicked)
    }
}
