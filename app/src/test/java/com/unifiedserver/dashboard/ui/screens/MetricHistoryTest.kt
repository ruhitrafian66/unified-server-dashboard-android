package com.unifiedserver.dashboard.ui.screens

import androidx.lifecycle.viewModelScope
import com.unifiedserver.dashboard.MainDispatcherRule
import com.unifiedserver.dashboard.MemoryStore
import com.unifiedserver.dashboard.data.PreferencesManager
import com.unifiedserver.dashboard.data.models.HistorySample
import com.unifiedserver.dashboard.data.models.SystemOverview
import com.unifiedserver.dashboard.data.repository.HistoryMetric
import com.unifiedserver.dashboard.data.repository.SystemRepository
import com.unifiedserver.dashboard.ui.components.HistoryRange
import com.unifiedserver.dashboard.ui.components.downsample
import com.unifiedserver.dashboard.ui.components.historyStats
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.*
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MetricHistoryTest {
    @get:Rule val main = MainDispatcherRule()

    private class Server(var temperature: Float? = 50f, var tempFails: Boolean = false) :
        SystemRepository(PreferencesManager(MemoryStore())) {
        val calls = mutableListOf<Pair<HistoryMetric, Int>>()
        val gates = mutableMapOf<Int, CompletableDeferred<Unit>>()
        override suspend fun getOverview() = Result.success(SystemOverview(temperature = temperature))
        override suspend fun getHistory(metric: HistoryMetric, hours: Int): Result<List<HistorySample>> {
            calls += metric to hours
            gates[hours]?.await()
            if (metric == HistoryMetric.TEMPERATURE && tempFails) return Result.failure(RuntimeException("404"))
            return Result.success(listOf(HistorySample(1, hours.toFloat())))
        }
    }

    @Test fun loadsBothHistoriesAtTheSelectedRange() = runTest {
        val server = Server()
        val vm = SystemViewModel(server)
        vm.loadAll()
        advanceUntilIdle()
        assertEquals(setOf(HistoryMetric.CPU to 6, HistoryMetric.TEMPERATURE to 6), server.calls.toSet())
        assertEquals(6f, vm.uiState.value.histories.getValue(HistoryMetric.TEMPERATURE).samples!!.single().v)

        vm.selectRange(HistoryMetric.CPU, HistoryRange.D4)
        advanceUntilIdle()
        assertEquals(HistoryMetric.CPU to 96, server.calls.last())
        assertEquals(96f, vm.uiState.value.histories.getValue(HistoryMetric.CPU).samples!!.single().v)
        vm.viewModelScope.cancel()
    }

    @Test fun silentRefreshesSkipHistoriesWithinAMinute() = runTest {
        val server = Server()
        val vm = SystemViewModel(server)
        vm.loadAll()
        advanceUntilIdle()
        server.calls.clear()
        vm.loadAll(silent = true)
        advanceUntilIdle()
        assertTrue(server.calls.isEmpty())
        vm.viewModelScope.cancel()
    }

    @Test fun aSlowOldRangeResponseDoesNotOverwriteTheNewRange() = runTest {
        val server = Server().apply { gates[6] = CompletableDeferred() }
        val vm = SystemViewModel(server)
        vm.loadAll()
        runCurrent()
        vm.selectRange(HistoryMetric.CPU, HistoryRange.H24)
        advanceUntilIdle()
        server.gates.getValue(6).complete(Unit)
        advanceUntilIdle()
        val cpu = vm.uiState.value.histories.getValue(HistoryMetric.CPU)
        assertEquals(HistoryRange.H24, cpu.range)
        assertEquals(24f, cpu.samples!!.single().v)
        vm.viewModelScope.cancel()
    }

    @Test fun temperatureFailureIsOnlyAnErrorWhenTheBoardReportsTemperature() = runTest {
        val noSensor = SystemViewModel(Server(temperature = null, tempFails = true))
        noSensor.loadAll()
        advanceUntilIdle()
        assertNull(noSensor.uiState.value.error)
        noSensor.viewModelScope.cancel()

        val withSensor = SystemViewModel(Server(tempFails = true))
        withSensor.loadAll()
        advanceUntilIdle()
        assertEquals("Temperature history unavailable. Previous data retained.", withSensor.uiState.value.error)
        withSensor.viewModelScope.cancel()
    }

    @Test fun statsAndDownsampling() {
        val samples = (0 until 5800).map { HistorySample(it * 60L, (it % 100).toFloat()) }
        val stats = historyStats(samples)!!
        assertEquals(99f, stats.now)
        assertEquals(99f, stats.peak)
        assertEquals(49.5f, stats.average, 0.1f)
        assertNull(historyStats(emptyList()))

        val small = downsample(samples, 360)
        assertTrue(small.size <= 360)
        assertEquals(samples.last(), small.last())
        assertTrue(small.first().t < 60L * 16)
        assertEquals(10, downsample(samples.take(10), 360).size)
    }
}
