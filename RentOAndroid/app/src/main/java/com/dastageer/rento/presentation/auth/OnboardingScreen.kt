package com.dastageer.rento.presentation.auth

import android.Manifest
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dastageer.rento.R
import com.dastageer.rento.domain.model.AccountType
import com.dastageer.rento.domain.model.UserMode
import com.dastageer.rento.presentation.shared.components.ErrorBanner
import com.dastageer.rento.presentation.shared.components.GhostButton
import com.dastageer.rento.presentation.shared.components.PrimaryButton
import com.dastageer.rento.presentation.shared.components.ProgressStepBar
import com.dastageer.rento.presentation.shared.components.RentoChip
import com.dastageer.rento.presentation.shared.components.SectionLabel
import com.dastageer.rento.presentation.shared.components.UnderlineInputField
import com.dastageer.rento.presentation.shared.icons.RentoIcons
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.LocalRentoTypography
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = koinViewModel(),
) {
    val colors = LocalRentoColors.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var previousStep by remember { mutableIntStateOf(0) }

    LaunchedEffect(uiState.currentStep) {
        if (uiState.currentStep == 4) onComplete()
    }

    Column(
        modifier = Modifier.fillMaxSize().background(colors.bg0),
    ) {
        Spacer(Modifier.height(16.dp))
        ProgressStepBar(
            currentStep = uiState.currentStep.coerceAtMost(3),
            totalSteps = 4,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        val isForward = uiState.currentStep >= previousStep
        LaunchedEffect(uiState.currentStep) { previousStep = uiState.currentStep }

        AnimatedContent(
            targetState = uiState.currentStep,
            transitionSpec = {
                if (isForward) {
                    (slideInHorizontally { it } + fadeIn()) togetherWith (slideOutHorizontally { -it } + fadeOut())
                } else {
                    (slideInHorizontally { -it } + fadeIn()) togetherWith (slideOutHorizontally { it } + fadeOut())
                }
            },
            modifier = Modifier.weight(1f),
            label = "onboardingStep",
        ) { step ->
            when (step) {
                0 -> Step1Content(uiState, viewModel)
                1 -> Step2Content(uiState, viewModel)
                2 -> Step3Content(uiState, viewModel)
                3 -> Step4Content(uiState, viewModel)
                else -> Box(Modifier.fillMaxSize())
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            AnimatedVisibility(visible = uiState.error != null, enter = expandVertically() + fadeIn()) {
                ErrorBanner(
                    message = uiState.error ?: "",
                    modifier = Modifier.padding(bottom = 12.dp),
                )
            }
            PrimaryButton(
                text = if (uiState.currentStep < 3) stringResource(R.string.onboarding_continue)
                else stringResource(R.string.onboarding_finish),
                onClick = { if (uiState.currentStep < 3) viewModel.nextStep() else viewModel.finish() },
                enabled = !(uiState.currentStep == 3 && uiState.isLoading),
            )
            if (uiState.currentStep > 0) {
                Spacer(Modifier.height(10.dp))
                GhostButton(
                    text = stringResource(R.string.onboarding_back),
                    onClick = { viewModel.prevStep() },
                )
            }
        }
    }
}

@Composable
private fun Step1Content(uiState: OnboardingUiState, viewModel: OnboardingViewModel) {
    val colors = LocalRentoColors.current
    val typography = LocalRentoTypography.current

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(32.dp))
        Text(stringResource(R.string.onboarding_step1_title), style = typography.displayM, color = colors.t0)
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.onboarding_step1_subtitle), fontSize = 14.sp, color = colors.t2)
        Spacer(Modifier.height(36.dp))

        OnboardingSelectionCard(
            icon = RentoIcons.Search,
            title = stringResource(R.string.onboarding_step1_looking_title),
            subtitle = stringResource(R.string.onboarding_step1_looking_subtitle),
            selected = uiState.defaultMode == UserMode.LOOKING,
            onClick = { viewModel.selectMode(UserMode.LOOKING) },
        )
        Spacer(Modifier.height(16.dp))
        OnboardingSelectionCard(
            icon = RentoIcons.Home,
            title = stringResource(R.string.onboarding_step1_hosting_title),
            subtitle = stringResource(R.string.onboarding_step1_hosting_subtitle),
            selected = uiState.defaultMode == UserMode.HOSTING,
            onClick = { viewModel.selectMode(UserMode.HOSTING) },
        )

        Spacer(Modifier.height(24.dp))
        SectionLabel(stringResource(R.string.onboarding_step1_note_label))
        Spacer(Modifier.height(6.dp))
        Text(stringResource(R.string.onboarding_step1_note), fontSize = 12.sp, color = colors.t2)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step2Content(uiState: OnboardingUiState, viewModel: OnboardingViewModel) {
    val colors = LocalRentoColors.current
    val typography = LocalRentoTypography.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showProvincePicker by remember { mutableStateOf(false) }
    var showCityPicker by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    val provinces = remember { context.resources.getStringArray(R.array.provinces).toList() }

    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            scope.launch {
                try {
                    val loc = LocationServices.getFusedLocationProviderClient(context).lastLocation.await()
                    if (loc != null) {
                        viewModel.updateLocation(loc.latitude, loc.longitude)
                        @Suppress("DEPRECATION")
                        val addresses = Geocoder(context, Locale.getDefault()).getFromLocation(loc.latitude, loc.longitude, 1)
                        val addr = addresses?.firstOrNull()
                        if (addr != null) {
                            addr.adminArea?.let { viewModel.updateProvince(it) }
                            addr.locality?.let { viewModel.updateCity(it) }
                        }
                    }
                } catch (_: Exception) { }
            }
        }
    }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(24.dp))
        Text(stringResource(R.string.onboarding_step2_title), style = typography.displayM, color = colors.t0)
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.onboarding_step2_subtitle), fontSize = 14.sp, color = colors.t2)
        Spacer(Modifier.height(28.dp))

        SectionLabel(stringResource(R.string.onboarding_step2_name_label))
        Spacer(Modifier.height(6.dp))
        UnderlineInputField(
            value = uiState.name, onValueChange = { viewModel.updateName(it) },
            placeholder = stringResource(R.string.auth_full_name_placeholder),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
        )

        Spacer(Modifier.height(20.dp))
        SectionLabel(stringResource(R.string.onboarding_step2_phone_label))
        Spacer(Modifier.height(6.dp))
        UnderlineInputField(
            value = uiState.phone, onValueChange = { viewModel.updatePhone(it) },
            placeholder = stringResource(R.string.onboarding_step2_phone_placeholder),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
        )

        Spacer(Modifier.height(20.dp))
        SectionLabel(stringResource(R.string.onboarding_step2_dob_label))
        Spacer(Modifier.height(6.dp))
        PickerRow(
            icon = RentoIcons.Clock,
            text = uiState.dateOfBirth ?: stringResource(R.string.onboarding_step2_dob_placeholder),
            hasValue = uiState.dateOfBirth != null,
            onClick = { showDatePicker = true },
        )

        Spacer(Modifier.height(24.dp))
        SectionLabel(stringResource(R.string.onboarding_step2_province_label))
        Spacer(Modifier.height(6.dp))
        PickerRow(
            icon = RentoIcons.Pin,
            text = uiState.province.ifBlank { stringResource(R.string.onboarding_step2_province_placeholder) },
            hasValue = uiState.province.isNotBlank(),
            trailing = RentoIcons.Chevron,
            onClick = { showProvincePicker = true },
        )

        Spacer(Modifier.height(16.dp))
        SectionLabel(stringResource(R.string.onboarding_step2_city_label))
        Spacer(Modifier.height(6.dp))
        PickerRow(
            icon = RentoIcons.Pin,
            text = uiState.city.ifBlank { stringResource(R.string.onboarding_step2_city_placeholder) },
            hasValue = uiState.city.isNotBlank(),
            trailing = RentoIcons.Chevron,
            enabled = uiState.province.isNotBlank(),
            onClick = { showCityPicker = true },
        )

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { locationLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(RentoIcons.Pin, null, tint = colors.primary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(8.dp))
            Text(stringResource(R.string.onboarding_step2_location_link), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = colors.primary)
        }
        Spacer(Modifier.height(24.dp))
    }

    if (showProvincePicker) {
        PickerSheet(
            items = provinces,
            selected = uiState.province,
            onSelect = { viewModel.updateProvince(it); showProvincePicker = false },
            onDismiss = { showProvincePicker = false },
        )
    }

    if (showCityPicker && uiState.province.isNotBlank()) {
        val cityArrayName = "cities_${uiState.province.replace(" ", "_").replace("-", "_")}"
        val resId = context.resources.getIdentifier(cityArrayName, "array", context.packageName)
        val cities = if (resId != 0) context.resources.getStringArray(resId).toList() else emptyList()
        PickerSheet(
            items = cities,
            selected = uiState.city,
            onSelect = { viewModel.updateCity(it); showCityPicker = false },
            onDismiss = { showCityPicker = false },
        )
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(millis))
                        viewModel.updateDob(formatted)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun Step3Content(uiState: OnboardingUiState, viewModel: OnboardingViewModel) {
    val typography = LocalRentoTypography.current
    val colors = LocalRentoColors.current

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(32.dp))
        Text(stringResource(R.string.onboarding_step3_title), style = typography.displayM, color = colors.t0)
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.onboarding_step3_subtitle), fontSize = 14.sp, color = colors.t2)
        Spacer(Modifier.height(36.dp))

        OnboardingSelectionCard(
            icon = RentoIcons.User,
            title = stringResource(R.string.onboarding_step3_individual_title),
            subtitle = stringResource(R.string.onboarding_step3_individual_subtitle),
            selected = uiState.accountType == AccountType.INDIVIDUAL,
            onClick = { viewModel.selectAccountType(AccountType.INDIVIDUAL) },
        )
        Spacer(Modifier.height(16.dp))
        OnboardingSelectionCard(
            icon = RentoIcons.Building,
            title = stringResource(R.string.onboarding_step3_business_title),
            subtitle = stringResource(R.string.onboarding_step3_business_subtitle),
            selected = uiState.accountType == AccountType.BUSINESS,
            onClick = { viewModel.selectAccountType(AccountType.BUSINESS) },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Step4Content(uiState: OnboardingUiState, viewModel: OnboardingViewModel) {
    val typography = LocalRentoTypography.current
    val colors = LocalRentoColors.current
    val sources = listOf(
        stringResource(R.string.onboarding_step4_social),
        stringResource(R.string.onboarding_step4_friend),
        stringResource(R.string.onboarding_step4_google),
        stringResource(R.string.onboarding_step4_tv),
        stringResource(R.string.onboarding_step4_appstore),
        stringResource(R.string.onboarding_step4_other),
    )

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
    ) {
        Spacer(Modifier.height(32.dp))
        Text(stringResource(R.string.onboarding_step4_title), style = typography.displayM, color = colors.t0)
        Spacer(Modifier.height(8.dp))
        Text(stringResource(R.string.onboarding_step4_subtitle), fontSize = 14.sp, color = colors.t2)
        Spacer(Modifier.height(32.dp))

        SectionLabel(stringResource(R.string.onboarding_step4_label))
        Spacer(Modifier.height(12.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            sources.forEach { source ->
                RentoChip(
                    label = source,
                    selected = source in uiState.referralSources,
                    onClick = { viewModel.toggleReferral(source) },
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        SectionLabel(stringResource(R.string.onboarding_step4_skip_note))
    }
}

@Composable
private fun OnboardingSelectionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalRentoColors.current
    val borderColor by animateColorAsState(
        if (selected) colors.primary else colors.border2,
        tween(220), label = "cardBorder",
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.bg2, RoundedCornerShape(24.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(24.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, onClick = onClick,
            )
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(colors.primaryTint, RoundedCornerShape(18.dp))
                .border(1.5.dp, colors.primaryRing, RoundedCornerShape(18.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = colors.primary, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.width(18.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = colors.t0)
            Spacer(Modifier.height(4.dp))
            Text(subtitle, fontSize = 13.sp, color = colors.t2)
        }
        Icon(
            if (selected) RentoIcons.Check else RentoIcons.Chevron,
            null,
            tint = if (selected) colors.primary else colors.t3,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun PickerRow(
    icon: ImageVector,
    text: String,
    hasValue: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: ImageVector? = null,
    enabled: Boolean = true,
) {
    val colors = LocalRentoColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, enabled = enabled, onClick = onClick,
            )
            .drawBehind {
                drawLine(
                    color = colors.inputUnderlineIdle,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 2.dp.toPx(),
                )
            }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = colors.t2, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(
            text, fontSize = 14.sp,
            color = if (hasValue) colors.t0 else colors.t3,
            modifier = Modifier.weight(1f),
        )
        if (trailing != null) {
            Icon(trailing, null, tint = colors.t3, modifier = Modifier.size(18.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PickerSheet(
    items: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = LocalRentoColors.current
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.bg1,
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) { onSelect(item) }
                        .padding(vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(item, fontSize = 15.sp, color = colors.t0, modifier = Modifier.weight(1f))
                    if (item == selected) {
                        Icon(RentoIcons.Check, null, tint = colors.primary, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}
