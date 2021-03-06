package taxi.kassa.view.auth.auth_phone

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import taxi.kassa.MyApplication
import taxi.kassa.R
import taxi.kassa.repository.Repository
import taxi.kassa.util.Constants.ERROR_504

class AuthPhoneViewModel(
    app: Application,
    private val repository: Repository
) : AndroidViewModel(app) {

    private val context = getApplication<MyApplication>()

    val isProgressVisible = MutableLiveData<Boolean>().apply { value = false }
    val isLoggedIn = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        when (throwable.message) {
            ERROR_504 -> error.postValue(context.getString(R.string.internet_unavailable))
            else -> error.postValue(throwable.message)
        }
        isProgressVisible.postValue(false)
    }

    fun login(phone: String) {
        isProgressVisible.value = true

        viewModelScope.launch(exceptionHandler) {
            val response = repository.login(phone)
            isLoggedIn.postValue(response?.success)
            response?.errorMsg?.let { error.postValue(it) }
            isProgressVisible.postValue(false)
        }
    }
}