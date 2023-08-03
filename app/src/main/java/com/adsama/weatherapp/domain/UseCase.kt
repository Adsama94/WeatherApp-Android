package com.adsama.weatherapp.domain

abstract class UseCase<in Q : UseCase.RequestValues, out R : UseCase.ResponseValue> {

    private var requestValues: Q? = null

    internal suspend fun run() {
        executeUseCase(requestValues)
    }

    protected abstract suspend fun executeUseCase(requestValues: Q?) : R

    interface RequestValues

    interface ResponseValue

}