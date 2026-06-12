package me.hodders.hitt.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val HiitTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 64.sp,
        color = TextPrimary
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        color = TextPrimary
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        color = TextPrimary
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        color = TextPrimary
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        color = TextPrimary
    ),
    labelSmall = TextStyle(
        fontSize = 12.sp,
        color = TextSecondary
    )
)
