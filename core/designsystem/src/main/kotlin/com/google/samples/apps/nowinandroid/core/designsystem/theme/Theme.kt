/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.nowinandroid.core.designsystem.theme

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

// 说明：每款颜色方案都不一样（亮-默认、亮-Android、暗-默认、暗-Android）
/**
 * Light default theme color scheme
 * 亮默认主题配色方案
 */
@VisibleForTesting
// 亮-默认-颜色方案
val LightDefaultColorScheme = lightColorScheme(
    // 主颜色
    // -主颜色，如背景颜色。
    primary = Purple40,
    // -在主颜色上的，如字体颜色。
    onPrimary = Color.White,
    // 主容器颜色
    primaryContainer = Purple90,
    onPrimaryContainer = Purple10,
    // 二级颜色
    secondary = Orange40,
    onSecondary = Color.White,
    // 二级容器颜色
    secondaryContainer = Orange90,
    onSecondaryContainer = Orange10,
    // 三级颜色
    tertiary = Blue40,
    onTertiary = Color.White,
    // 三级容器颜色
    tertiaryContainer = Blue90,
    onTertiaryContainer = Blue10,
    // 错误颜色
    error = Red40,
    onError = Color.White,
    // 错误容器颜色
    errorContainer = Red90,
    onErrorContainer = Red10,
    // 背景颜色
    background = DarkPurpleGray99,
    onBackground = DarkPurpleGray10,
    // 表面颜色
    surface = DarkPurpleGray99,
    onSurface = DarkPurpleGray10,
    // 表面变体颜色
    surfaceVariant = PurpleGray90,
    onSurfaceVariant = PurpleGray30,
    // 表面反颜色
    inverseSurface = DarkPurpleGray20,
    inverseOnSurface = DarkPurpleGray95,
    // 大纲颜色
    outline = PurpleGray50,
)

/**
 * Dark default theme color scheme
 * 暗默认主题配色方案
 */
@VisibleForTesting
// 暗-默认-颜色方案
val DarkDefaultColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = Purple20,
    primaryContainer = Purple30,
    onPrimaryContainer = Purple90,
    secondary = Orange80,
    onSecondary = Orange20,
    secondaryContainer = Orange30,
    onSecondaryContainer = Orange90,
    tertiary = Blue80,
    onTertiary = Blue20,
    tertiaryContainer = Blue30,
    onTertiaryContainer = Blue90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = DarkPurpleGray10,
    onBackground = DarkPurpleGray90,
    surface = DarkPurpleGray10,
    onSurface = DarkPurpleGray90,
    surfaceVariant = PurpleGray30,
    onSurfaceVariant = PurpleGray80,
    inverseSurface = DarkPurpleGray90,
    inverseOnSurface = DarkPurpleGray10,
    outline = PurpleGray60,
)

/**
 * Light Android theme color scheme
 * 亮色Android主题配色方案
 */
@VisibleForTesting
// 亮-Android-颜色方案
val LightAndroidColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = Green90,
    onPrimaryContainer = Green10,
    secondary = DarkGreen40,
    onSecondary = Color.White,
    secondaryContainer = DarkGreen90,
    onSecondaryContainer = DarkGreen10,
    tertiary = Teal40,
    onTertiary = Color.White,
    tertiaryContainer = Teal90,
    onTertiaryContainer = Teal10,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = DarkGreenGray99,
    onBackground = DarkGreenGray10,
    surface = DarkGreenGray99,
    onSurface = DarkGreenGray10,
    surfaceVariant = GreenGray90,
    onSurfaceVariant = GreenGray30,
    inverseSurface = DarkGreenGray20,
    inverseOnSurface = DarkGreenGray95,
    outline = GreenGray50,
)

/**
 * Dark Android theme color scheme
 * 暗色Android主题配色方案
 */
@VisibleForTesting
// 暗-Android-颜色方案
val DarkAndroidColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Green20,
    primaryContainer = Green30,
    onPrimaryContainer = Green90,
    secondary = DarkGreen80,
    onSecondary = DarkGreen20,
    secondaryContainer = DarkGreen30,
    onSecondaryContainer = DarkGreen90,
    tertiary = Teal80,
    onTertiary = Teal20,
    tertiaryContainer = Teal30,
    onTertiaryContainer = Teal90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = DarkGreenGray10,
    onBackground = DarkGreenGray90,
    surface = DarkGreenGray10,
    onSurface = DarkGreenGray90,
    surfaceVariant = GreenGray30,
    onSurfaceVariant = GreenGray80,
    inverseSurface = DarkGreenGray90,
    inverseOnSurface = DarkGreenGray10,
    outline = GreenGray60,
)

/**
 * Light Android gradient colors
 * 亮Android渐变色
 */
// 亮-Android-渐变色
val LightAndroidGradientColors = GradientColors(container = DarkGreenGray95)

/**
 * Dark Android gradient colors
 * 暗Android渐变色
 */
// 暗-Android-渐变色
val DarkAndroidGradientColors = GradientColors(container = Color.Black)

/**
 * Light Android background theme
 * 亮Android背景主题
 */
// 亮-Android-背景主题
val LightAndroidBackgroundTheme = BackgroundTheme(color = DarkGreenGray95)

/**
 * Dark Android background theme
 * 暗Android背景主题
 */
// 暗-Android-背景主题
val DarkAndroidBackgroundTheme = BackgroundTheme(color = Color.Black)

/**
 * Now in Android theme.
 * Now in Android 主题
 *
 * @param darkTheme Whether the theme should use a dark color scheme (follows system by default).
 *                  主题是否应该使用深色方案(默认情况下遵循系统)。
 * @param androidTheme Whether the theme should use the Android theme color scheme instead of the
 *        default theme.
 *                  主题是否应该使用Android主题配色方案而不是默认主题。
 * @param disableDynamicTheming If `true`, disables the use of dynamic theming, even when it is
 *        supported. This parameter has no effect if [androidTheme] is `true`.
 *                  如果true，禁用动态主题的使用，即使它是支持的。当[androidTheme]为“true”时，此参数不起作用。
 */
@Composable
fun NiaTheme(
    // 是否使用暗主题，默认走系统的。
    darkTheme: Boolean = isSystemInDarkTheme(),
    // 是否使用android主题，默认不是（即走默认的主题、颜色方案等）。
    androidTheme: Boolean = false,
    // 是否禁用动态主题，默认禁用，androidTheme为true时此参数不起作用。
    disableDynamicTheming: Boolean = true,
    // 内容
    content: @Composable () -> Unit,
) {
    // Color scheme
    // 颜色方案
    val colorScheme = when {
        // 使用android主题，根据darkTheme（是否使用暗主题）来判断颜色方案。
        androidTheme -> if (darkTheme) DarkAndroidColorScheme else LightAndroidColorScheme
        // 不使用android主题，即使用默认主题。
        // -不禁用动态主题，并且支持（高版本）动态主题，使用动态主题。
        !disableDynamicTheming && supportsDynamicTheming() -> {
            val context = LocalContext.current
            // 获取动态主题，高版本调用，SDK-31（android-12）及以上支持。。
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        // -禁用动态主题，或者不支持（低版本）动态主题，使用默认主题。
        else -> if (darkTheme) DarkDefaultColorScheme else LightDefaultColorScheme
    }
    // Gradient colors
    // 渐变颜色
    // -空的渐变颜色
    val emptyGradientColors = GradientColors(container = colorScheme.surfaceColorAtElevation(2.dp))
    // -默认的渐变颜色
    val defaultGradientColors = GradientColors(
        top = colorScheme.inverseOnSurface,
        bottom = colorScheme.primaryContainer,
        container = colorScheme.surface,
    )
    // -渐变颜色
    val gradientColors = when {
        // android主题，走Android的渐变颜色。
        androidTheme -> if (darkTheme) DarkAndroidGradientColors else LightAndroidGradientColors
        // 不禁用动态主题，并且支持（高版本）动态主题，使用动态主题的渐变颜色。
        !disableDynamicTheming && supportsDynamicTheming() -> emptyGradientColors
        // 禁用动态主题，或者不支持（低版本）动态主题，使用默认主题的渐变颜色。
        else -> defaultGradientColors
    }
    // Background theme
    // 背景主题
    val defaultBackgroundTheme = BackgroundTheme(
        color = colorScheme.surface,
        tonalElevation = 2.dp,
    )
    val backgroundTheme = when {
        androidTheme -> if (darkTheme) DarkAndroidBackgroundTheme else LightAndroidBackgroundTheme
        else -> defaultBackgroundTheme
    }
    val tintTheme = when {
        androidTheme -> TintTheme()
        !disableDynamicTheming && supportsDynamicTheming() -> TintTheme(colorScheme.primary)
        else -> TintTheme()
    }
    // Composition locals
    // 局部组合，提供LocalGradientColors、LocalBackgroundTheme、LocalTintTheme使用。
    CompositionLocalProvider(
        LocalGradientColors provides gradientColors,
        LocalBackgroundTheme provides backgroundTheme,
        LocalTintTheme provides tintTheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = NiaTypography,
            content = content,
        )
    }
}

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
// 是否支持动态主题，SDK-31（android-12）及以上支持。
fun supportsDynamicTheming() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
