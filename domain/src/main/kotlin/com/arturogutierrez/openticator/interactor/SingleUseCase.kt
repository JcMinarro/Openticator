package com.arturogutierrez.openticator.interactor

import com.arturogutierrez.openticator.executor.PostExecutionThread
import com.arturogutierrez.openticator.executor.ThreadExecutor
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

abstract class SingleUseCase<T, in Params> constructor(
    private val threadExecutor: ThreadExecutor,
    private val postExecutionThread: PostExecutionThread) {

  private val disposables = CompositeDisposable()

  protected abstract fun buildObservable(params: Params): Single<T>

  open fun execute(params: Params, observer: DisposableSingleObserver<T>) {
    val observable = buildObservable(params)
        .subscribeOn(Schedulers.from(threadExecutor))
        .observeOn(postExecutionThread.scheduler)
    addDisposable(observable.subscribeWith(observer))
  }

  fun dispose() {
    if (!disposables.isDisposed) {
      disposables.dispose()
    }
  }

  private fun addDisposable(disposable: Disposable) {
    disposables.add(disposable)
  }
}