package com.pguillen.readingtracker.appium

import io.appium.java_client.AppiumBy
import io.appium.java_client.android.AndroidDriver
import io.appium.java_client.android.options.UiAutomator2Options
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.openqa.selenium.WebElement
import org.openqa.selenium.support.ui.ExpectedConditions
import org.openqa.selenium.support.ui.WebDriverWait
import java.net.URI
import java.time.Duration

abstract class BaseAppiumTest {

	protected lateinit var driver: AndroidDriver
	protected lateinit var wait: WebDriverWait

	@BeforeEach
	fun setUp() {
		val deviceUdid =
			System.getenv("APPIUM_DEVICE_UDID")
				?: "emulator-5554"

		val options = UiAutomator2Options()
			.setPlatformName("Android")
			.setAutomationName("UiAutomator2")
			.setUdid(deviceUdid)
			.setAppPackage("com.pguillen.readingtracker")
			.setAppActivity(".MainActivity")

		driver = AndroidDriver(
			URI("http://127.0.0.1:4723").toURL(),
			options
		)

		driver.setSetting(
			"disableIdLocatorAutocompletion",
			true
		)

		wait = WebDriverWait(
			driver,
			Duration.ofSeconds(30)
		)
	}

	@AfterEach
	fun tearDown() {
		driver.quit()
	}

	protected fun waitUntilVisible(id: String): WebElement {
		return wait.until(
			ExpectedConditions.visibilityOfElementLocated(
				AppiumBy.id(id)
			)
		)
	}

	protected fun waitUntilTextVisible(text: String): WebElement {
		return wait.until(
			ExpectedConditions.visibilityOfElementLocated(
				AppiumBy.xpath("//*[contains(@text, '$text')]")
			)
		)
	}

	protected fun openDeepLink(url: String) {
		driver.executeScript(
			"mobile: deepLink",
			mapOf(
				"url" to url,
				"package" to "com.pguillen.readingtracker"
			)
		)
	}
}
