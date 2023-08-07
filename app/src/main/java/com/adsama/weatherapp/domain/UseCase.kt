package com.adsama.weatherapp.domain

abstract class UseCase<Q : UseCase.RequestValues, P : UseCase.ResponseValue> {

    var requestValues: Q? = null
    var useCaseCallback: UseCaseCallback<P>? = null

    internal abstract suspend fun executeUseCase(requestValues: Q?)

    interface RequestValues
    interface ResponseValue

    interface UseCaseCallback<R> {
        fun onSuccess(response: R)
        fun onError(t: Throwable)
    }

}