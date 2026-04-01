package com.example.myapplication.presentation

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*
import com.google.accompanist.pager.*

// ─── Color Palette ────────────────────────────────────────────────────────────
private val BgDark        = Color(0xFF0A0A0F)
private val AccentRed     = Color(0xFFFF3D5A)
private val AccentRedDim  = Color(0x33FF3D5A)
private val AccentCyan    = Color(0xFF00E5FF)
private val TextPrimary   = Color(0xFFF0F0F5)
private val TextMuted     = Color(0xFF6B7280)

@OptIn(ExperimentalPagerApi::class)
@Composable
fun SensorScreen(
    hr: Float,
    ax: Float,
    ay: Float,
    az: Float,
    steps: Float,           // kept for API compatibility – not displayed
    isSending: Boolean = true
) {
    val pagerState = rememberPagerState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark),
        contentAlignment = Alignment.Center
    ) {
        // ── Full-screen horizontal pager ──────────────────────────────────
        HorizontalPager(
            count = 2,
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> HeartRatePage(hr = hr)
                1 -> AccelerometerPage(ax = ax, ay = ay, az = az)
            }
        }

        // ── Page indicator dots ───────────────────────────────────────────
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(2) { idx ->
                val selected = pagerState.currentPage == idx
                val color    = if (idx == 0) AccentRed else AccentCyan
                Box(
                    modifier = Modifier
                        .size(if (selected) 6.dp else 4.dp)
                        .clip(CircleShape)
                        .background(if (selected) color else TextMuted)
                )
            }
        }

        // ── Live / Offline status ─────────────────────────────────────────
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val pulseAnim = rememberInfiniteTransition()
            val pulseAlpha by pulseAnim.animateFloat(
                initialValue = 1f, targetValue = 0.3f,
                animationSpec = infiniteRepeatable(
                    animation = tween(900, easing = EaseInOut),
                    repeatMode = RepeatMode.Reverse
                )
            )
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSending) AccentRed.copy(alpha = pulseAlpha) else TextMuted
                    )
            )
            Text(
                text = if (isSending) "LIVE" else "OFFLINE",
                color = if (isSending) AccentRed else TextMuted,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }
    }
}

// ─── Page 1 : Heart Rate ──────────────────────────────────────────────────────
@Composable
private fun HeartRatePage(hr: Float) {
    val pulse = rememberInfiniteTransition()
    val ringScale by pulse.animateFloat(
        initialValue = 0.88f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Outer glow ring (pulses)
        Box(
            modifier = Modifier
                .size((110 * ringScale).dp)
                .clip(CircleShape)
                .background(AccentRedDim)
        )

        // Circular border
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(AccentRed.copy(alpha = 0.20f), Color.Transparent)
                    )
                )
                .drawBehind {
                    drawCircle(
                        color = AccentRed,
                        radius = size.minDimension / 2,
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
        )

        // HR value
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "♥", color = AccentRed, fontSize = 14.sp)
            Text(
                text = "${hr.toInt()}",
                color = TextPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 30.sp
            )
            Text(
                text = "BPM",
                color = TextMuted,
                fontSize = 9.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = "HEART RATE",
            color = AccentRed,
            fontSize = 9.sp,
            letterSpacing = 2.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 28.dp)
        )

        Text(
            text = "swipe →",
            color = TextMuted,
            fontSize = 7.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 26.dp)
        )
    }
}

// ─── Page 2 : Accelerometer ───────────────────────────────────────────────────
@Composable
private fun AccelerometerPage(ax: Float, ay: Float, az: Float) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "ACCELEROMETER",
                color = AccentCyan,
                fontSize = 9.sp,
                letterSpacing = 2.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            AxisBar(label = "X", value = ax, color = Color(0xFFFF6B6B))
            AxisBar(label = "Y", value = ay, color = Color(0xFFFFD93D))
            AxisBar(label = "Z", value = az, color = AccentCyan)
        }

        Text(
            text = "← swipe",
            color = TextMuted,
            fontSize = 7.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 26.dp)
        )
    }
}

// ─── Animated axis bar ────────────────────────────────────────────────────────
@Composable
private fun AxisBar(label: String, value: Float, color: Color) {
    // Normalise from roughly [-20, 20] m/s² → [0, 1]
    val normalized   = ((value + 20f) / 40f).coerceIn(0f, 1f)
    val animProgress by animateFloatAsState(
        targetValue    = normalized,
        animationSpec  = tween(300, easing = EaseOut),
        label          = "axis_$label"
    )

    Row(
        verticalAlignment    = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier             = Modifier.fillMaxWidth()
    ) {
        Text(
            text       = label,
            color      = color,
            fontSize   = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier   = Modifier.width(12.dp),
            textAlign  = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color.copy(alpha = 0.15f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animProgress)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(color.copy(alpha = 0.5f), color)
                        )
                    )
            )
        }

        Text(
            text      = "${"%.1f".format(value)}",
            color     = TextPrimary,
            fontSize  = 9.sp,
            fontWeight = FontWeight.Medium,
            modifier  = Modifier.width(32.dp),
            textAlign = TextAlign.End
        )
    }
}