package com.dastageer.rento.presentation.shared.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
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
                showDialog = true,
                onDismissRequest = {},
                state = GlassDialogState.IDLE,
                title = "Title",
                description = "Desc",
                primaryBtnText = "Ok"
            ) 
        }
    }
}
