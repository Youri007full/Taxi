package taxi.kassa.view.auth.auth_sign_up

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import taxi.kassa.repository.ApiRepository
import taxi.kassa.util.Constants.KEY

class AuthSignUpViewModel(private val repository: ApiRepository) : ViewModel() {

    private val disposable = CompositeDisposable()

    val isSignUp = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()

    fun signUp(phone: String) {
        disposable.add(
            Observable.fromCallable {
                repository.signUp("", phone, 11, KEY)
                    ?.subscribe({
                        isSignUp.postValue(it?.success)
                        it?.errorMsg?.let { error.postValue(it) }
                    }, {
                    })
            }
                .subscribeOn(Schedulers.io())
                .subscribe()
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}