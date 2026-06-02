package com.adsama.domain.model

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DomainModelTest {

    @Test
    fun `DomainError from should map DomainError to itself`() {
        val error = DomainError.NetworkError("No connection")
        val result = DomainError.from(error)
        assertEquals(error, result)
    }

    @Test
    fun `DomainError from should map general Exception to UnknownError`() {
        val exception = Exception("Random error")
        val result = DomainError.from(exception)
        assertTrue(result is DomainError.UnknownError)
        assertEquals("Random error", result.message)
    }

    @Test
    fun `ResultFlowUseCase should catch exceptions and emit DomainError`() = runTest {
        val exceptionUseCase = object : ResultFlowUseCase<Unit, String>() {
            override suspend fun execute(parameters: Unit): Result<String> {
                throw Exception("Unexpected crash")
            }
        }

        val results = exceptionUseCase(Unit).toList()

        assertEquals(2, results.size)
        assertTrue(results[0] is Result.Loading)
        assertTrue(results[1] is Result.Error)
        val error = (results[1] as Result.Error).error
        assertTrue(error is DomainError.UnknownError)
        assertEquals("Unexpected crash", error.message)
    }

    @Test
    fun `ResultFlowUseCase should handle Result Loading emission`() = runTest {
        val loadingUseCase = object : ResultFlowUseCase<Unit, String>() {
            override suspend fun execute(parameters: Unit): Result<String> {
                return Result.Loading("Partial data")
            }
        }

        val results = loadingUseCase(Unit).toList()

        assertEquals(2, results.size)
        assertTrue(results[1] is Result.Loading)
        assertEquals("Partial data", (results[1] as Result.Loading).data)
    }
}
