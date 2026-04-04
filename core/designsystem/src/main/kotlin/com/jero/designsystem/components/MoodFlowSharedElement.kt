package com.jero.designsystem.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.SharedTransitionScope.OverlayClip
import androidx.compose.animation.SharedTransitionScope.SharedContentState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

context(scope: SharedTransitionScope)
fun Modifier.moodFlowSharedElement(
    isLocalInspectionMode: Boolean,
    state: SharedContentState,
    animatedVisibilityScope: AnimatedVisibilityScope,
    boundsTransform: BoundsTransform = DefaultBoundsTransform,
    renderInOverlayDuringTransition: Boolean = true,
    zIndexInOverlay: Float = 0f,
    clipInOverlayDuringTransition: OverlayClip = ParentClip,
): Modifier =
    if (isLocalInspectionMode) {
        this
    } else {
        with(scope) {
            this@moodFlowSharedElement.sharedElement(
                sharedContentState = state,
                animatedVisibilityScope = animatedVisibilityScope,
                boundsTransform = boundsTransform,
                renderInOverlayDuringTransition = renderInOverlayDuringTransition,
                zIndexInOverlay = zIndexInOverlay,
                clipInOverlayDuringTransition = clipInOverlayDuringTransition,
            )
        }
    }

// Reutilizable para Texts con un boundsTransform más suave
context(scope: SharedTransitionScope)
fun Modifier.moodflowSharedElementForText(
    isLocalInspectionMode: Boolean,
    state: SharedContentState,
    animatedVisibilityScope: AnimatedVisibilityScope,
): Modifier = this.moodFlowSharedElement(
    isLocalInspectionMode = isLocalInspectionMode,
    state = state,
    animatedVisibilityScope = animatedVisibilityScope,
    boundsTransform = TextBoundsTransform,
)

// Clip de overlay por defecto
private val ParentClip: OverlayClip =
    object : OverlayClip {
        override fun getClipPath(
            state: SharedContentState,
            bounds: Rect,
            layoutDirection: LayoutDirection,
            density: Density,
        ): Path? = state.parentSharedContentState?.clipPathInOverlay
    }

// Spring por defecto
private val DefaultSpring = spring(
    stiffness = Spring.StiffnessMediumLow,
    visibilityThreshold = Rect.VisibilityThreshold,
)

val DefaultBoundsTransform = BoundsTransform { _, _ -> DefaultSpring }

// Spring más suave para textos
private val TextSpring = spring(
    stiffness = Spring.StiffnessLow,
    dampingRatio = Spring.DampingRatioNoBouncy,
    visibilityThreshold = Rect.VisibilityThreshold,
)

val TextBoundsTransform = BoundsTransform { _, _ -> TextSpring }
