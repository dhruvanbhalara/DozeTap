package com.dhruvanbhalara.dozetap.domain.usecase

import com.dhruvanbhalara.dozetap.domain.model.TimeoutOption
import com.dhruvanbhalara.dozetap.fakes.FakeTimeoutRepository
import org.junit.Assert.assertEquals
import org.junit.Test

/** Unit tests for [GetScreenTimeoutUseCase]. */
class GetScreenTimeoutUseCaseTest {

    /** Verifies that invoking use case returns current screen timeout from repository. */
    @Test
    fun `invoke returns current screen timeout from repository`() {
        val fakeRepo = FakeTimeoutRepository(initialTimeout = TimeoutOption.ONE_MIN)
        val useCase = GetScreenTimeoutUseCase(fakeRepo)

        val result = useCase()

        assertEquals(TimeoutOption.ONE_MIN, result)
    }
}
