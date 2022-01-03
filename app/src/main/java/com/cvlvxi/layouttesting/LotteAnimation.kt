package com.cvlvxi.layouttesting

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import kotlin.random.Random



data class DirectionalLottie(
    val lottieId: Int,
    val imageIsLeft: Boolean,
)

data class MovingLottie(
    val lottieId: Int,
    val imageIsLeft: Boolean,
    var startX: Float,
    var endX: Float,
    var walkingDuration: Int
) {
    var prevOffsetX: Float? = null

    fun shouldMirror(newOffsetX: Float): Boolean {
        val isRight = (prevOffsetX?: startX) < newOffsetX
        return imageIsLeft == isRight
    }
}

@Composable
fun SimpleLottie(
    lottieId: Int,
    modifier: Modifier,
    size: Dp = 100.dp
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottieId))
    LottieAnimation(
        composition,
        iterations = LottieConstants.IterateForever,
        modifier = modifier
            .size(size)
    )
}

fun generateLotties(lottiePool: List<DirectionalLottie>, howMany: Int, boxMaxWidth: Dp): List<MovingLottie> {
    val maxWidth = boxMaxWidth.value.toInt()
    val halfMaxWidth = maxWidth / 2


    val lotties = mutableListOf<MovingLottie>()
    (0 until howMany).forEach { _ ->
        val startX = Random.nextInt(0, maxWidth)
        val endX = if (startX < halfMaxWidth) (maxWidth+startX) else (0 - startX)
        val randIdx = Random.nextInt(0, lottiePool.size)
        lotties.add(MovingLottie(
            lottieId=lottiePool[randIdx].lottieId,
            imageIsLeft=lottiePool[randIdx].imageIsLeft,
            startX=startX.toFloat(),
            endX=endX.toFloat(),
            walkingDuration = Random.nextInt(5,30) * 1000,
        ))
    }
    return lotties
}

@Composable
fun FlyingLotties(modifier: Modifier, maxWidth: Dp) {
    val lottiePool= listOf(
        DirectionalLottie(R.raw.birds_flying1, false)
    )
    val lotties = generateLotties(lottiePool, 2,  maxWidth)
    MoveXLotties(lotties, modifier = modifier)
}


@Composable
fun RunWalkingLotties(numLotties: Int, modifier: Modifier, maxWidth: Dp) {
    val lottiePool= listOf(
        DirectionalLottie(R.raw.walking_cat1, false),
        DirectionalLottie(R.raw.walking_girl1, false),
        DirectionalLottie(R.raw.walking_girl2, false),
        DirectionalLottie(R.raw.walking_girl3, true),
        DirectionalLottie(R.raw.walking_guy1, false),
        DirectionalLottie(R.raw.walking_guy2, false),
        DirectionalLottie(R.raw.walking_guy3, true),
        DirectionalLottie(R.raw.walking_guy4, false),
        DirectionalLottie(R.raw.walking_duck1, false),
        DirectionalLottie(R.raw.walking_orange1, false),
        DirectionalLottie(R.raw.walking_peach1, false),
    )
    val lotties = generateLotties(lottiePool, numLotties,  maxWidth)
    MoveXLotties(lotties, modifier = modifier)
}


@Composable
fun MoveXLotties(lotties: List<MovingLottie>, modifier: Modifier) {
    lotties.forEach { lottie ->
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(lottie.lottieId))
        val startX by remember { mutableStateOf(lottie.startX) }
        val infiniteTransition = rememberInfiniteTransition()

        val offsetX by infiniteTransition.animateFloat(
            initialValue = startX,
            targetValue = lottie.endX,

            animationSpec = InfiniteRepeatableSpec(
                animation = tween(
                    durationMillis=lottie.walkingDuration,
                    delayMillis = 0,
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            )
        )
        LottieAnimation(
            composition,
            iterations = LottieConstants.IterateForever,
            modifier = modifier
                .size(100.dp)
                .offset(x = offsetX.toInt().dp)
                .scale(scaleX = if (lottie.shouldMirror(offsetX)) -1f else 1f, scaleY = 1f)
        )
        lottie.prevOffsetX = offsetX
    }
}

