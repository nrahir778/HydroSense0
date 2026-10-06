package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TankState
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PumpActiveGreen

@Composable
fun MotorControlCard(
  tankState: TankState,
  isConnected: Boolean,
  onToggleMotor: () -> Unit,
  onSetTargetLevel: (Float) -> Unit,
  modifier: Modifier = Modifier
) {
  val isRunning = tankState.isMotorOn && isConnected

  val infiniteTransition = rememberInfiniteTransition(label = "rotorRotation")
  val rotationAngle by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 360f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 800, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "rotorAngle"
  )

  val cardBgColor by animateColorAsState(
    targetValue = if (isRunning) {
      Color(0xFF0F3025)
    } else {
      MaterialTheme.colorScheme.surface
    },
    label = "cardBgColor"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("motor_control_card"),
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = cardBgColor),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      // Header: Motor Title & Pin D7 info
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(
                if (isRunning) PumpActiveGreen.copy(alpha = 0.2f)
                else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isRunning) Icons.Default.Autorenew else Icons.Default.Speed,
              contentDescription = null,
              tint = if (isRunning) PumpActiveGreen else MaterialTheme.colorScheme.primary,
              modifier = Modifier
                .size(26.dp)
                .then(if (isRunning) Modifier.rotate(rotationAngle) else Modifier)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Text(
              text = "Pump Motor (Relay D7)",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = if (isRunning) Color.White else MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = when {
                !isConnected -> "Awaiting HC-05 Connection"
                isRunning -> "Relay D7 Active LOW (Motor Running)"
                else -> "Relay D7 Standby HIGH (Motor Stopped)"
              },
              style = MaterialTheme.typography.bodySmall,
              color = if (isRunning) Color(0xFFA7F3D0) else MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Live Relay state chip
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = when {
            !isConnected -> MaterialTheme.colorScheme.surfaceVariant
            isRunning -> PumpActiveGreen.copy(alpha = 0.2f)
            else -> MaterialTheme.colorScheme.surfaceVariant
          }
        ) {
          Text(
            text = when {
              !isConnected -> "OFFLINE"
              isRunning -> "ACTIVE LOW"
              else -> "IDLE HIGH"
            },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = when {
              !isConnected -> MaterialTheme.colorScheme.onSurfaceVariant
              isRunning -> PumpActiveGreen
              else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Runtime Duration & Last Alert info
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(
            if (isRunning) Color.Black.copy(alpha = 0.25f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
          )
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Timer,
            contentDescription = null,
            tint = if (isRunning) Color(0xFFA7F3D0) else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Run Duration",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isRunning) Color(0xFFD1FAE5) else MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Text(
          text = if (isConnected && isRunning) formatDuration(tankState.motorRunSeconds) else "--:--",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = if (isRunning) PumpActiveGreen else MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Target Water Level Setter (Arduino TARGET:<val>)
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(16.dp))
          .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
          .padding(14.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Flag,
              contentDescription = null,
              tint = Color(0xFFFFB300),
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Arduino Target Level",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.SemiBold
            )
          }

          Text(
            text = "${tankState.targetLevel.toInt()}%",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFFFFB300)
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Quick Preset Chips for Target
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf(60f, 70f, 80f, 90f).forEach { preset ->
            val isSelected = tankState.targetLevel.toInt() == preset.toInt()
            FilterChip(
              selected = isSelected,
              onClick = { onSetTargetLevel(preset) },
              label = { Text("${preset.toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
              modifier = Modifier.weight(1f)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Primary Large Action Button
      Button(
        onClick = onToggleMotor,
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .testTag("motor_power_toggle_button"),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = when {
            !isConnected -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            isRunning -> DangerRed
            else -> MaterialTheme.colorScheme.primary
          },
          contentColor = when {
            !isConnected -> MaterialTheme.colorScheme.onSurfaceVariant
            else -> Color.White
          }
        )
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = if (!isConnected) Icons.Default.BluetoothDisabled else Icons.Default.PowerSettingsNew,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = when {
              !isConnected -> "CONNECT HC-05 TO OPERATE"
              isRunning -> "STOP MOTOR (MOTOR_OFF)"
              else -> "START MOTOR (MOTOR_ON)"
            },
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold
          )
        }
      }
    }
  }
}

private fun formatDuration(seconds: Long): String {
  val hrs = seconds / 3600
  val mins = (seconds % 3600) / 60
  val secs = seconds % 60
  return if (hrs > 0) {
    String.format("%02d:%02d:%02d", hrs, mins, secs)
  } else {
    String.format("%02d:%02d", mins, secs)
  }
}
