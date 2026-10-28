package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

/**
 * Custom FlingBehavior that provides silky smooth momentum and realistic inertia,
 * mimicking physical weight with extended gliding deceleration.
 */
@Composable
fun rememberSmoothMomentumFlingBehavior(): FlingBehavior {
  val splineDecay = rememberSplineBasedDecay<Float>()

  return remember(splineDecay) {
    object : FlingBehavior {
      override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
        // Scale velocity slightly to feel responsive and glide with pleasant inertia
        val velocity = initialVelocity * 1.05f
        var velocityLeft = velocity
        var lastValue = 0f

        val animationState = AnimationState(
          initialValue = 0f,
          initialVelocity = velocity
        )

        animationState.animateDecay(splineDecay) {
          val delta = value - lastValue
          val consumed = scrollBy(delta)
          lastValue = value
          velocityLeft = this.velocity

          // If content stopped accepting scroll, finish fling
          if (abs(delta - consumed) > 0.5f) {
            cancelAnimation()
          }
        }

        return velocityLeft
      }
    }
  }
}

/**
 * A container that adds rubber-banding (spring bounce) overscroll at the top and bottom boundaries,
 * reacting to both finger drag resistance and fling momentum impact.
 */
@Composable
fun RubberBandBouncyContainer(
  modifier: Modifier = Modifier,
  maxBouncePx: Float = 260f,
  content: @Composable (FlingBehavior) -> Unit
) {
  val coroutineScope = rememberCoroutineScope()
  val overscrollOffset = remember { Animatable(0f) }
  val smoothFling = rememberSmoothMomentumFlingBehavior()

  val springSpec = remember {
    spring<Float>(
      dampingRatio = 0.72f, // Springy rubber bounce
      stiffness = Spring.StiffnessMediumLow
    )
  }

  val nestedScrollConnection = remember {
    object : NestedScrollConnection {

      override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        // If we are currently stretched in an overscroll state and user drags in reverse direction,
        // consume the drag towards zero before scrolling the list.
        val current = overscrollOffset.value

        if (current > 0f && available.y < 0f) {
          val consumedY = max(available.y, -current)
          coroutineScope.launch {
            overscrollOffset.snapTo(current + consumedY)
          }
          return Offset(0f, consumedY)
        } else if (current < 0f && available.y > 0f) {
          val consumedY = min(available.y, -current)
          coroutineScope.launch {
            overscrollOffset.snapTo(current + consumedY)
          }
          return Offset(0f, consumedY)
        }

        return Offset.Zero
      }

      override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
      ): Offset {
        // When content reaches top or bottom limit and still has scroll leftover:
        if (source == NestedScrollSource.UserInput && available.y != 0f) {
          val current = overscrollOffset.value
          // Asymptotic resistance formula mimicking physical rubber band tension:
          val progress = (abs(current) / maxBouncePx).coerceIn(0f, 1f)
          val resistance = (1f - progress * 0.75f).coerceIn(0.12f, 0.55f)
          val newOffset = (current + available.y * resistance).coerceIn(-maxBouncePx, maxBouncePx)

          coroutineScope.launch {
            overscrollOffset.snapTo(newOffset)
          }

          return Offset(0f, available.y)
        }

        return Offset.Zero
      }

      override suspend fun onPreFling(available: Velocity): Velocity {
        // If release occurred while stretched, spring smoothly back to zero
        if (overscrollOffset.value != 0f) {
          coroutineScope.launch {
            overscrollOffset.animateTo(
              targetValue = 0f,
              animationSpec = springSpec,
              initialVelocity = available.y * 0.35f
            )
          }
          return Velocity(0f, available.y)
        }
        return Velocity.Zero
      }

      override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
        // When momentum inertia collides with the top or bottom boundary:
        if (abs(available.y) > 100f) {
          val impactVelocity = available.y
          val sign = sign(impactVelocity)
          val bounceTarget = (sign * (abs(impactVelocity) * 0.055f)).coerceIn(-maxBouncePx * 0.7f, maxBouncePx * 0.7f)

          coroutineScope.launch {
            // First penetrate into rubber-band with spring
            overscrollOffset.animateTo(
              targetValue = bounceTarget,
              animationSpec = spring(
                dampingRatio = 0.65f,
                stiffness = Spring.StiffnessMedium
              ),
              initialVelocity = impactVelocity * 0.4f
            )
            // Then rebound back to zero with realistic rubber-band recoil
            overscrollOffset.animateTo(
              targetValue = 0f,
              animationSpec = springSpec
            )
          }

          return available
        } else if (overscrollOffset.value != 0f) {
          coroutineScope.launch {
            overscrollOffset.animateTo(
              targetValue = 0f,
              animationSpec = springSpec
            )
          }
        }

        return Velocity.Zero
      }
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .nestedScroll(nestedScrollConnection)
      .graphicsLayer {
        translationY = overscrollOffset.value
      }
  ) {
    content(smoothFling)
  }
}
