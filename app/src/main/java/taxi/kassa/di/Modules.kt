package taxi.kassa.di

import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import taxi.kassa.model.remote.ApiClient
import taxi.kassa.repository.Repository
import taxi.kassa.util.PreferenceManager
import taxi.kassa.view.MainViewModel
import taxi.kassa.view.accounts_cards.AccountsCardsViewModel
import taxi.kassa.view.accounts_cards.accounts.AccountsViewModel
import taxi.kassa.view.accounts_cards.cards.CardsViewModel
import taxi.kassa.view.auth.auth_code.AuthCodeViewModel
import taxi.kassa.view.auth.auth_phone.AuthPhoneViewModel
import taxi.kassa.view.auth.auth_sign_up.AuthSignUpViewModel
import taxi.kassa.view.balance.BalanceViewModel
import taxi.kassa.view.fuel.FuelReplenishViewModel
import taxi.kassa.view.notifications.NotificationsViewModel
import taxi.kassa.view.orders.OrdersViewModel
import taxi.kassa.view.orders.list.OrdersListViewModel
import taxi.kassa.view.profile.ProfileViewModel
import taxi.kassa.view.registration.connection.ConnectionViewModel
import taxi.kassa.view.support.SupportViewModel
import taxi.kassa.view.support.chat.ChatHistoryViewModel
import taxi.kassa.view.support.message.WriteMessageViewModel
import taxi.kassa.view.withdraws.WithdrawsViewModel
import taxi.kassa.view.withdraws.withdraw.WithdrawViewModel
import taxi.kassa.view.withdraws.withdraw_create.WithdrawCreateViewModel
import taxi.kassa.view.withdraws.withdraw_create.daily.DailyWithdrawViewModel
import taxi.kassa.view.withdraws.withdraw_create.instant.InstantWithdrawViewModel

val apiModule = module {
    single { ApiClient.create(get()) }
}

val repositoryModule = module {
    single { Repository(get(), get()) }
}

val preferenceModule = module {
    single { PreferenceManager(androidApplication().applicationContext) }
}

val viewModelModule = module(override = true) {
    viewModel {
        MainViewModel(get(), get())
    }
    viewModel {
        AuthPhoneViewModel(androidApplication(), get())
    }
    viewModel {
        AuthSignUpViewModel(androidApplication(), get())
    }
    viewModel {
        AuthCodeViewModel(androidApplication(), get(), get())
    }
    viewModel {
        ProfileViewModel(androidApplication(), get(), get())
    }
    viewModel {
        BalanceViewModel(androidApplication(), get())
    }
    viewModel {
        WithdrawsViewModel(androidApplication(), get())
    }
    viewModel {
        WithdrawViewModel(androidApplication(), get())
    }
    viewModel {
        AccountsCardsViewModel(androidApplication(), get())
    }
    viewModel {
        AccountsViewModel(androidApplication(), get())
    }
    viewModel {
        CardsViewModel(androidApplication(), get())
    }
    viewModel {
        WithdrawCreateViewModel(androidApplication(), get())
    }
    viewModel {
        DailyWithdrawViewModel(androidApplication(), get())
    }
    viewModel {
        InstantWithdrawViewModel(androidApplication(), get())
    }
    viewModel {
        OrdersViewModel(androidApplication(), get())
    }
    viewModel {
        FuelReplenishViewModel(androidApplication(), get())
    }
    viewModel {
        OrdersListViewModel(androidApplication(), get())
    }
    viewModel {
        ChatHistoryViewModel(androidApplication(), get())
    }
    viewModel {
        WriteMessageViewModel(androidApplication(), get())
    }
    viewModel {
        NotificationsViewModel(get())
    }
    viewModel {
        SupportViewModel(get())
    }
    viewModel {
        SupportViewModel(get())
    }
    viewModel {
        ConnectionViewModel(androidApplication(), get())
    }
}