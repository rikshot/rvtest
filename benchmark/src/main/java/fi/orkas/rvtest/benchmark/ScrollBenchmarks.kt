package fi.orkas.rvtest.benchmark

import androidx.benchmark.macro.ExperimentalMetricApi
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.TraceSectionMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val PACKAGE_NAME = "fi.orkas.rvtest"

@RunWith(AndroidJUnit4::class)
class ScrollBenchmarks {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @OptIn(ExperimentalMetricApi::class)
    private val metrics = listOf(
        FrameTimingMetric(),
        TraceSectionMetric("RV Scroll"),
        TraceSectionMetric("RV OnLayout"),
        TraceSectionMetric("RV FullInvalidate"),
        TraceSectionMetric("RV PartialInvalidate"),
        TraceSectionMetric("RV Prefetch"),
        TraceSectionMetric("RV onCreateViewHolder %"),
        TraceSectionMetric("RV onBindViewHolder %")
    )

    @Test
    fun vertical() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = metrics,
        iterations = 5,
        setupBlock = {
            startActivityAndWait()
        }
    ) {
        val recyclerView = device.findObject(By.res(packageName, "categories"))
        recyclerView.fling(Direction.DOWN)
        recyclerView.fling(Direction.DOWN)
        recyclerView.fling(Direction.UP)
        recyclerView.fling(Direction.UP)
    }

    @Test
    fun horizontal() = benchmarkRule.measureRepeated(
        packageName = PACKAGE_NAME,
        metrics = metrics,
        iterations = 5,
        setupBlock = {
            startActivityAndWait()
        }
    ) {
        val recyclerView = device.findObject(By.res(packageName, "category"))
        recyclerView.fling(Direction.RIGHT)
        recyclerView.fling(Direction.RIGHT)
        recyclerView.fling(Direction.LEFT)
        recyclerView.fling(Direction.LEFT)
    }
}
