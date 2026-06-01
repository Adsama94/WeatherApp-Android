package com.adsama.domain

import io.mockk.mockk
import org.junit.Before


class FetchCurrentLocationUseCaseTest {

    private lateinit var locationRepository: LocationRepository
    private lateinit var useCase: FetchCurrentLocationUseCase

    @Before
    fun setUp() {
        locationRepository = mockk()
        useCase = FetchCurrentLocationUseCase(locationRepository)
    }


}