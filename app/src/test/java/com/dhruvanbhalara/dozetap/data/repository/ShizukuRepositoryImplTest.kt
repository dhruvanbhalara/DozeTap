package com.dhruvanbhalara.dozetap.data.repository

import com.dhruvanbhalara.dozetap.fakes.FakeShizukuRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Contract tests for [FakeShizukuRepository] verifying it correctly satisfies the
 * [com.dhruvanbhalara.dozetap.domain.repository.IShizukuRepository] interface contract.
 *
 * Note: [com.dhruvanbhalara.dozetap.data.repository.ShizukuRepositoryImpl] depends on the
 * Shizuku binder API and Android [android.content.Context], which require instrumented tests
 * in `androidTest/`. This file guards against fake drift by verifying the contract the fake
 * must satisfy.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FakeShizukuRepositoryContractTest {

    @Test
    fun `isShizukuRunning returns true when service is running`() {
        val fakeRepo = FakeShizukuRepository(isRunning = true, isGranted = true)
        assertTrue(fakeRepo.isShizukuRunning())
    }

    @Test
    fun `isShizukuRunning returns false when service is stopped`() {
        val fakeRepo = FakeShizukuRepository(isRunning = false, isGranted = true)
        assertFalse(fakeRepo.isShizukuRunning())
    }

    @Test
    fun `isShizukuPermissionGranted returns true when authorized`() {
        val fakeRepo = FakeShizukuRepository(isRunning = true, isGranted = true)
        assertTrue(fakeRepo.isShizukuPermissionGranted())
    }

    @Test
    fun `isShizukuPermissionGranted returns false when service is stopped`() {
        val fakeRepo = FakeShizukuRepository(isRunning = false, isGranted = true)
        assertFalse(fakeRepo.isShizukuPermissionGranted())
    }

    @Test
    fun `isShizukuPermissionGranted returns false when permission is denied`() {
        val fakeRepo = FakeShizukuRepository(isRunning = true, isGranted = false)
        assertFalse(fakeRepo.isShizukuPermissionGranted())
    }

    @Test
    fun `grantWriteSettingsPermission returns true on successful grant`() = runTest {
        val fakeRepo = FakeShizukuRepository(isRunning = true, isGranted = true, shouldGrantSucceed = true)
        val result = fakeRepo.grantWriteSettingsPermission()
        assertTrue(result)
    }

    @Test
    fun `grantWriteSettingsPermission returns false when Shizuku service is not running`() = runTest {
        val fakeRepo = FakeShizukuRepository(isRunning = false, isGranted = true, shouldGrantSucceed = true)
        val result = fakeRepo.grantWriteSettingsPermission()
        assertFalse(result)
    }

    @Test
    fun `grantWriteSettingsPermission returns false when permission is not granted`() = runTest {
        val fakeRepo = FakeShizukuRepository(isRunning = true, isGranted = false, shouldGrantSucceed = true)
        val result = fakeRepo.grantWriteSettingsPermission()
        assertFalse(result)
    }

    @Test
    fun `grantWriteSettingsPermission returns false when shell execution fails`() = runTest {
        val fakeRepo = FakeShizukuRepository(isRunning = true, isGranted = true, shouldGrantSucceed = false)
        val result = fakeRepo.grantWriteSettingsPermission()
        assertFalse(result)
    }

    /** Verifies that requestShizukuPermission records the correct request code for later assertion. */
    @Test
    fun `requestShizukuPermission stores the provided request code`() {
        val fakeRepo = FakeShizukuRepository(isRunning = true, isGranted = false)
        fakeRepo.requestShizukuPermission(requestCode = 42)
        assertEquals(42, fakeRepo.lastRequestedCode)
    }

    /** Verifies that updateState correctly updates isShizukuAvailableFlow to reflect combined running+granted state. */
    @Test
    fun `updateState emits correct availability when both conditions are met`() {
        val fakeRepo = FakeShizukuRepository(isRunning = false, isGranted = false)
        assertFalse(fakeRepo.isShizukuAvailableFlow.value)

        fakeRepo.updateState(running = true, granted = true)
        assertTrue(fakeRepo.isShizukuAvailableFlow.value)

        fakeRepo.updateState(running = true, granted = false)
        assertFalse(fakeRepo.isShizukuAvailableFlow.value)

        fakeRepo.updateState(running = false, granted = true)
        assertFalse(fakeRepo.isShizukuAvailableFlow.value)
    }
}
