package com.pguillen.readingtracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PhoneDemoFrame(
	modifier: Modifier = Modifier,
	mobileMaxWidth: Dp = 420.dp,
	wideModeBreakpoint: Dp = 700.dp,
	outerPadding: Dp = 24.dp,
	showDeviceChrome: Boolean = true,
	content: @Composable () -> Unit
) {
	BoxWithConstraints(
		modifier = modifier
			.fillMaxSize()
			.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
	) {
		val isWideMode = maxWidth >= wideModeBreakpoint

		if (!isWideMode) {
			Box(modifier = Modifier.fillMaxSize()) {
				content()
			}
			return@BoxWithConstraints
		}

		val availableWidth = maxWidth - (outerPadding * 2)
		val frameWidth = if (availableWidth < mobileMaxWidth) availableWidth else mobileMaxWidth

		Box(
			modifier = Modifier.fillMaxSize(),
			contentAlignment = Alignment.Center
		) {
			Surface(
				modifier = Modifier.width(frameWidth),
				shape = RoundedCornerShape(32.dp),
				shadowElevation = 18.dp,
				tonalElevation = 6.dp,
				color = MaterialTheme.colorScheme.surface
			) {
				if (showDeviceChrome) {
					Column(
						modifier = Modifier
							.fillMaxWidth()
							.clip(RoundedCornerShape(32.dp))
					) {
						PhoneTopBar()
						Box(
							modifier = Modifier
								.fillMaxWidth()
								.background(MaterialTheme.colorScheme.background)
						) {
							content()
						}
						PhoneBottomBar()
					}
				}
				else {
					Box(
						modifier = Modifier
							.fillMaxWidth()
							.clip(RoundedCornerShape(32.dp))
							.background(MaterialTheme.colorScheme.background)
					) {
						content()
					}
				}
			}
		}
	}
}

@Composable
private fun PhoneTopBar() {
	Box(
		modifier = Modifier
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.surface)
			.padding(top = 12.dp, bottom = 10.dp),
		contentAlignment = Alignment.Center
	) {
		Box(
			modifier = Modifier
				.width(110.dp)
				.height(26.dp)
				.clip(RoundedCornerShape(16.dp))
				.background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
		)
	}

	HorizontalDivider(
		color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
	)
}

@Composable
private fun PhoneBottomBar() {
	HorizontalDivider(
		color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
	)

	Box(
		modifier = Modifier
			.fillMaxWidth()
			.background(MaterialTheme.colorScheme.surface)
			.padding(vertical = 12.dp),
		contentAlignment = Alignment.Center
	) {
		Box(
			modifier = Modifier
				.width(120.dp)
				.height(5.dp)
				.clip(RoundedCornerShape(100.dp))
				.background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.18f))
		)
	}
}