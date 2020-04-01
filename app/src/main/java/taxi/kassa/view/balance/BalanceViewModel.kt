package taxi.kassa.view.balance

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import taxi.kassa.model.Notification
import taxi.kassa.model.responses.ResponseOwner
import taxi.kassa.repository.ApiRepository

class BalanceViewModel(private val repository: ApiRepository) : ViewModel() {

    private val disposable: Disposable

    val isProgressVisible = MutableLiveData<Boolean>().apply { this.value = true }
    val responseOwner = MutableLiveData<ResponseOwner>()
    val error = MutableLiveData<String>()
    val notifications = MutableLiveData<MutableList<Notification>>()

    init {
        disposable = Observable.fromCallable {
                repository.getOwner()
                    ?.doFinally { isProgressVisible.postValue(false) }
                    ?.subscribe({
                        responseOwner.postValue(it?.response)
                        error.postValue(it?.errorMsg)
                    }, {
                    })
            }
            .subscribeOn(Schedulers.io())
            .subscribe()

        notifications.value = repository.getNotifications()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}