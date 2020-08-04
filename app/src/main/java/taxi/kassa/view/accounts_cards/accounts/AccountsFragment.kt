package taxi.kassa.view.accounts_cards.accounts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import kotlinx.android.synthetic.main.fragment_accounts.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import taxi.kassa.R
import taxi.kassa.util.*
import taxi.kassa.util.Constants.NOT_FROM_PUSH
import taxi.kassa.util.Constants.PUSH_COUNTER
import taxi.kassa.view.withdraws.withdraw_create.daily.AccountsAdapter

class AccountsFragment : Fragment() {

    private lateinit var viewModel: AccountsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = container?.inflate(R.layout.fragment_accounts)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_arrow.setOnClickListener { findNavController(this).popBackStack() }

        notification_image.setOnClickListener {
            findNavController(this).navigate(
                R.id.action_accountsFragment_to_notificationsFragment,
                Bundle().apply { putString(NOT_FROM_PUSH, NOT_FROM_PUSH) }
            )
        }

        notification_count.setOnClickListener {
            findNavController(this).navigate(
                R.id.action_accountsFragment_to_notificationsFragment,
                Bundle().apply { putString(NOT_FROM_PUSH, NOT_FROM_PUSH) }
            )
        }

        add_account.setOnClickListener {
            accounts_block.gone()
            add_account_block.visible()
        }

        close_image.setOnClickListener {
            add_account_block.gone()
            accounts_block.visible()
        }

        with(viewModel) {
            getAccounts()

            isProgressVisible.observe(viewLifecycleOwner) { visible ->
                progress_bar.visibility = if (visible) VISIBLE else GONE
            }

            error.observe(viewLifecycleOwner) { context?.longToast(it) }

            creatingStatus.observe(viewLifecycleOwner) { status ->
                status?.let { context?.longToast(it) }
            }

            accounts.observe(viewLifecycleOwner) {
                accounts_recycler.setHasFixedSize(true)
                it.info?.let {
                    accounts_recycler.adapter = AccountsAdapter(it, true, { _, _ ->
                    }, { account ->
                        context?.showTwoButtonsDialog(
                            getString(R.string.delete_account),
                            getString(R.string.delete_account_message),
                            getString(R.string.no),
                            getString(R.string.yes)
                        ) { deleteAccount(account.id) }
                    })
                }
            }

            notifications.observe(viewLifecycleOwner) {
                val oldPushesSize = PreferenceManager(requireContext()).getInt(PUSH_COUNTER)
                oldPushesSize?.let { oldSize ->
                    if (it.size > oldSize) {
                        notification_count.text = (it.size - oldSize).toString()
                        notification_count.visible()
                        notification_image.invisible()
                    } else {
                        notification_count.invisible()
                        notification_image.visible()
                    }
                }
            }
        }

        runDelayed(500) { accounts_recycler?.let { if (it.isNotEmpty()) it[0].performClick() } }
    }
}