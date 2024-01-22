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

package com.google.samples.apps.nowinandroid.feature.foryou

import com.google.samples.apps.nowinandroid.core.model.data.FollowableTopic

/**
 * A sealed hierarchy describing the onboarding state for the for you screen.
 * 描述for you屏幕的用户引导的密封层次结构。
 */
// 用户引导-UiState
sealed interface OnboardingUiState {
    /**
     * The onboarding state is loading.
     * 用户引导状态加载中。
     */
    data object Loading : OnboardingUiState

    /**
     * The onboarding state was unable to load.
     * 用户引导状态无法加载。
     */
    data object LoadFailed : OnboardingUiState

    /**
     * There is no onboarding state.
     * 没有用户引导状态。
     */
    data object NotShown : OnboardingUiState

    /**
     * There is a onboarding state, with the given lists of topics.
     * 有一个用户引导状态，带有给定的主题列表。
     */
    data class Shown(
        val topics: List<FollowableTopic>,
    ) : OnboardingUiState {
        /**
         * True if the onboarding can be dismissed.
         * 如果可以取消用户引导，则为True。
         */
        val isDismissable: Boolean get() = topics.any { it.isFollowed }
    }
}
