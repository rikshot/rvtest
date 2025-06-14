package fi.orkas.rvtest

import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(internal val testDispatcher: TestDispatcher = UnconfinedTestDispatcher()) : TestWatcher() {
    private val enabled = Build.DEVICE == "robolectric"

    override fun starting(description: Description) {
        if (enabled) {
            Dispatchers.setMain(testDispatcher)
        }
    }

    override fun finished(description: Description) {
        if (enabled) {
            Dispatchers.resetMain()
        }
    }
}
