package taxi.kassa.view.withdraws.withdraw_create

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import taxi.kassa.MyApplication
import taxi.kassa.R
import taxi.kassa.model.Card
import taxi.kassa.model.Notification
import taxi.kassa.model.responses.AccountsList
import taxi.kassa.model.responses.ResponseOwner
import taxi.kassa.repository.ApiRepository

class WithdrawCreateViewModel(
    app: Application,
    private val repository: ApiRepository
) : AndroidViewModel(app) {

    private val context = getApplication<MyApplication>()
    private val accountId = MutableLiveData<Int>()

    val isProgressVisible = MutableLiveData<Boolean>().apply { this.value = true }
    val accounts = MutableLiveData<AccountsList>()
    val creatingStatus = MutableLiveData<String>()
    val deletionStatus = MutableLiveData<String>()
    val responseOwner = MutableLiveData<ResponseOwner>()
    val error = MutableLiveData<String>()
    val notifications = MutableLiveData<MutableList<Notification>>()
    val cards = MutableLiveData<MutableList<Card>>()

    init {
        viewModelScope.launch {
            val response = repository.getOwner()
            responseOwner.postValue(response?.response)
            error.postValue(response?.errorMsg)
            isProgressVisible.postValue(false)
        }

        viewModelScope.launch {
            val response = repository.getAccounts()

            val cardList = mutableListOf<Card>()
            response?.response?.info?.map {
                cardList.add(Card(it.cardNumber, it.cardDate))
            }
            cards.postValue(cardList)

            accountId.postValue(response?.response?.info?.firstOrNull()?.id)
            accounts.postValue(response?.response)
            error.postValue(response?.errorMsg)
            isProgressVisible.postValue(false)
        }

        notifications.value = repository.getNotifications()
    }

    fun deleteAccount() {
        isProgressVisible.value = true

        viewModelScope.launch {
            accounts.value?.info?.firstOrNull()?.id?.let {
                val response = repository.deleteAccount(it)
                deletionStatus.postValue(response?.response?.status)
                error.postValue(response?.errorMsg)
            }
            isProgressVisible.postValue(false)
        }
    }

    fun createWithdraw(sourceId: Int, amount: String?) {
        isProgressVisible.value = true

        viewModelScope.launch {
            accountId.value?.let { id ->
                try {
                    val response = repository.createWithdraw(sourceId, amount, id)
                    creatingStatus.postValue(response?.response?.status)
                    error.postValue(response?.errorMsg)
                    isProgressVisible.postValue(false)
                } catch (e: HttpException) {
                    error.postValue(context.getString(R.string.internet_unavailable))
                    isProgressVisible.postValue(false)
                }
            }
        }
    }
}