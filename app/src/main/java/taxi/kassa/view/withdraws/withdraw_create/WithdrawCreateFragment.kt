package taxi.kassa.view.withdraws.withdraw_create

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.findNavController
import kotlinx.android.synthetic.main.fragment_withdraw_create.*
import kotlinx.android.synthetic.main.item_card.view.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import taxi.kassa.R
import taxi.kassa.util.Constants.CITYMOBIL
import taxi.kassa.util.Constants.GETT
import taxi.kassa.util.Constants.PUSH_COUNTER
import taxi.kassa.util.Constants.TAXI
import taxi.kassa.util.Constants.YANDEX
import taxi.kassa.util.PreferenceManager
import taxi.kassa.util.shortToast
import taxi.kassa.util.showOneButtonDialog
import taxi.kassa.util.showTwoButtonsDialog

class WithdrawCreateFragment : Fragment() {

    private lateinit var viewModel: WithdrawCreateViewModel

    private val taxiType: String by lazy { arguments?.get(TAXI) as String }
    private var sourceId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel { parametersOf() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_withdraw_create, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserInfo()
        viewModel.getAccounts()

        val constraintSet = ConstraintSet()

        viewModel.error.observe(viewLifecycleOwner, Observer {
            activity?.shortToast(it)
        })

        viewModel.creatingStatus.observe(viewLifecycleOwner, Observer { status ->
            status?.let {
                activity?.shortToast(it)
            }
        })

        viewModel.deletionStatus.observe(viewLifecycleOwner, Observer { status ->
            status?.let {
                activity?.shortToast(it)
            }
        })

        viewModel.accounts.observe(viewLifecycleOwner, Observer {
            if (it.info?.isNotEmpty() == true) {
                val account = it.info.first()
                bank_name_tv.text = account.bankName
                order_tv.text = getString(R.string.order_format, account.accountNumber)
                name_tv.text = account.driverName
            }
        })

        viewModel.responseOwner.observe(viewLifecycleOwner, Observer { response ->
            response?.let {
                when (taxiType) {
                    YANDEX -> {
                        taxi_icon.setImageResource(R.drawable.ic_yandex_mini)
                        taxi_name.text = getString(R.string.yandex_title)
                        balance_tv.text = getString(R.string.withdraw_create_format, response.balanceYandex)
                    }
                    GETT -> {
                        taxi_icon.setImageResource(R.drawable.ic_gett_mini)
                        taxi_name.text = getString(R.string.gett_title)
                        balance_tv.text = getString(R.string.withdraw_create_format, response.balanceGett)
                    }
                    CITYMOBIL -> {
                        taxi_icon.setImageResource(R.drawable.ic_citymobil_mini)
                        taxi_name.text = getString(R.string.citymobil_title)
                        balance_tv.text = getString(R.string.withdraw_create_format, response.balanceCity)
                    }
                }
            }
        })

        viewModel.notifications.observe(viewLifecycleOwner, Observer {
            val oldPushesSize = PreferenceManager(requireContext()).getInt(PUSH_COUNTER)
            oldPushesSize?.let { oldSize ->
                if (it.size > oldSize) {
                    notification_count.text = (it.size - oldSize).toString()
                    notification_count.visibility = VISIBLE
                    notification_image.visibility = INVISIBLE
                } else {
                    notification_count.visibility = INVISIBLE
                    notification_image.visibility = VISIBLE
                }
            }
        })

        viewModel.cards.observe(viewLifecycleOwner, Observer { cards ->
            cards_recycler.setHasFixedSize(true)
            cards_recycler.adapter = WithdrawCardsAdapter(cards) { card ->
                card.check_icon.visibility = VISIBLE
                card.green_background.visibility = VISIBLE

                cards_recycler.adapter?.itemCount?.let {
                    if (it > 1) {
                        for (i in 0 until it) {
                            val item = cards_recycler[i]
                            if (item != card) {
                                item.check_icon.visibility = GONE
                                item.green_background.visibility = GONE
                            }
                        }
                    }
                }
            }
        })

        val runnable = Runnable { cards_recycler[0].performClick() }
        Handler().postDelayed(runnable, 500)

        sum_edit_text.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                constraintSet.clone(parent_layout)
                constraintSet.connect(
                    R.id.sum_input_view,
                    ConstraintSet.BOTTOM,
                    R.id.send_request_button,
                    ConstraintSet.TOP
                )
                constraintSet.applyTo(parent_layout)
            }
        }

        sum_edit_text.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                constraintSet.clear(R.id.sum_input_view, ConstraintSet.BOTTOM)
                constraintSet.applyTo(parent_layout)
            }
            false
        }

        minus_image.setOnClickListener {
            account_block.visibility = GONE
            minus_image.visibility = GONE
            plus_image.visibility = VISIBLE
        }

        plus_image.setOnClickListener {
            account_block.visibility = VISIBLE
            minus_image.visibility = VISIBLE
            plus_image.visibility = GONE
        }

        minus_card_image.setOnClickListener {
            cards_recycler.visibility = GONE
            add_card_tv.visibility = GONE
            minus_card_image.visibility = GONE
            plus_card_image.visibility = VISIBLE
        }

        plus_card_image.setOnClickListener {
            cards_recycler.visibility = VISIBLE
            add_card_tv.visibility = VISIBLE
            minus_card_image.visibility = VISIBLE
            plus_card_image.visibility = GONE
        }

        daily_withdrawal_tv.setOnClickListener {
            context?.showOneButtonDialog(
                getString(R.string.daily_withdrawal),
                getString(R.string.daily_withdrawal_dialog_message)
            )
        }

        instant_withdrawal_tv.setOnClickListener {
            context?.showOneButtonDialog(
                getString(R.string.instant_withdrawal),
                getString(R.string.instant_withdrawal_dialog_message)
            )
        }

        delete_icon.setOnClickListener {
            context?.showTwoButtonsDialog(
                getString(R.string.delete_account),
                getString(R.string.delete_account_message),
                getString(R.string.no),
                getString(R.string.yes)
            ) {
                viewModel.deleteAccount()
            }
        }

        add_card_tv.setOnClickListener {
            findNavController(this).navigate(R.id.action_withdrawCreateFragment_to_accountsFragment)
        }

        send_request_button.setOnClickListener {
            val sum = sum_edit_text.text.toString()
            if (sum.isEmpty()) {
                sum_input_view.error = getString(R.string.input_error)
                return@setOnClickListener
            }

            viewModel.createWithdraw(sourceId, sum)
        }

        sum_edit_text.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                sum_input_view.error = null
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })

        notification_image.setOnClickListener {
            findNavController(this).navigate(R.id.action_withdrawCreateFragment_to_notificationsFragment)
        }

        notification_count.setOnClickListener {
            findNavController(this).navigate(R.id.action_withdrawCreateFragment_to_notificationsFragment)
        }

        back_arrow.setOnClickListener { activity?.onBackPressed() }
    }
}