package fi.orkas.rvtest

import android.app.Application
import android.view.View
import androidx.core.os.bundleOf
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import dagger.hilt.components.SingletonComponent
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondBadRequest
import io.ktor.http.Headers
import junit.framework.AssertionFailedError
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@UninstallModules(HttpClientEngineModule::class)
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DetailsTest {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val mainDispatcherRule = MainDispatcherRule()

    @Module
    @InstallIn(SingletonComponent::class)
    inner class HttpClientEngineModule {
        @Provides
        fun httpClientEngine(application: Application): HttpClientEngine = MockEngine.create {
            dispatcher = mainDispatcherRule.testDispatcher
            addHandler { request ->
                if (request.url.encodedPath.contains("configuration")) {
                    application.assets.open("configuration.json").use { stream ->
                        respond(
                            stream.readAllBytes(),
                            headers = Headers.build {
                                set("content-type", "application/json")
                            }
                        )
                    }
                } else if (request.url.encodedPath.contains("movie")) {
                    application.assets.open("movie.json").use { stream ->
                        respond(
                            stream.readAllBytes(),
                            headers = Headers.build {
                                set("content-type", "application/json")
                            }
                        )
                    }
                } else {
                    respondBadRequest()
                }
            }
        }
    }

    @Test
    fun testEventFragment() = runTest(mainDispatcherRule.testDispatcher) {
        launchFragmentInHiltContainer<DetailsFragment>(
            bundleOf("id" to 552524, "type" to DetailsType.MOVIE)
        )
        waitForView(withId(R.id.title)) {
            check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            check(matches(withText("Lilo & Stitch")))
        }
    }
}

suspend fun waitForView(
    viewMatcher: Matcher<View>,
    timeout: Duration = 5.seconds,
    delay: Duration = 100.milliseconds,
    block: ViewInteraction.() -> Unit
): ViewInteraction = withTimeout(timeout) {
    while (true) {
        try {
            return@withTimeout onView(viewMatcher).apply(block)
        } catch (_: NoMatchingViewException) {
            delay(delay)
        } catch (_: AssertionFailedError) {
            delay(delay)
        }
    }
    return@withTimeout onView(isRoot())
}
