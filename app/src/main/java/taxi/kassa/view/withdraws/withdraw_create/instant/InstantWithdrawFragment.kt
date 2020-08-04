package taxi.kassa.view.withdraws.withdraw_create.instant

import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import kotlinx.android.synthetic.main.fragment_instant_withdraw.*
import kotlinx.android.synthetic.main.item_card.view.*
import kotlinx.android.synthetic.main.keyboard.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import taxi.kassa.R
import taxi.kassa.util.*
import taxi.kassa.util.Constants.CITYMOBIL
import taxi.kassa.util.Constants.GETT
import taxi.kassa.util.Constants.NOT_FROM_PUSH
import taxi.kassa.util.Constants.PUSH_COUNTER
import taxi.kassa.util.Constants.TAXI
import taxi.kassa.util.Constants.YANDEX

class InstantWithdrawFragment : Fragment() {

    private lateinit var viewModel: InstantWithdrawViewModel

    private val taxiType: String by lazy { arguments?.get(TAXI) as String }
    private var sourceId = 1
    private var cardId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = container?.inflate(R.layout.fragment_instant_withdraw)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        back_arrow.setOnClickListener { findNavController(this).popBackStack() }

        notification_image.setOnClickListener {
            findNavController(this).navigate(
                R.id.action_instantWithdrawFragment_to_notificationsFragment,
                Bundle().apply { putString(NOT_FROM_PUSH, NOT_FROM_PUSH) }
            )
        }

        notification_count.setOnClickListener {
            findNavController(this).navigate(
                R.id.action_instantWithdrawFragment_to_notificationsFragment,
                Bundle().apply { putString(NOT_FROM_PUSH, NOT_FROM_PUSH) }
            )
        }

        call_background.setOnClickListener { activity?.makeCall(this) }

        next_button.setOnClickListener {
            cards_block.gone()
            withdraw_block.visible()
        }

        transfer_button.setOnClickListener { sendRequest() }

        sum_edit_text.showSoftInputOnFocus = false

        sum_edit_text.setOnFocusChangeListener { _, hasFocus ->
            when (hasFocus) {
                true -> {
                    sum_input_view.error = getString(R.string.max_sum_hint)
                    keyboard.visible()
                }
                false -> keyboard.gone()
            }
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

        with(viewModel) {
            getOwnerData()

            isProgressVisible.observe(viewLifecycleOwner) { visible ->
                progress_bar.visibility = if (visible) VISIBLE else GONE
            }

            error.observe(viewLifecycleOwner) { context?.longToast(it) }

            creatingStatus.observe(viewLifecycleOwner) { status ->
                status?.let { context?.longToast(it) }
            }

            cards.observe(viewLifecycleOwner) {
                when (it.isNullOrEmpty()) {
                    true -> call_dispatcher_block.visible()
                    false -> {
                        cards_block.visible()
                        cards_recycler.setHasFixedSize(true)
                        cards_recycler.adapter = CardsAdapter(it) { card, view ->
                            cardId = card.id?.toInt() ?: 1

                            val items = mutableListOf<View>()
                            (0 until (cards_recycler.adapter?.itemCount ?: 0)).map {
                                items.add(cards_recycler[it])
                            }

                            items.map {
                                if (it == view) {
                                    it.card_background_outline.visible()
                                    it.card_background.invisible()
                                } else {
                                    it.card_background_outline.invisible()
                                    it.card_background.visible()
                                }
                            }
                        }
                    }
                }
            }

            responseOwner.observe(viewLifecycleOwner) { response ->
                response?.let {
                    when (taxiType) {
                        YANDEX -> {
                            sourceId = 1
                            taxi_icon.setImageResource(R.drawable.ic_yandex_mini)
                            taxi_name.text = getString(R.string.yandex_title)
                            balance_tv.text = getString(R.string.account_balance_format, it.balanceYandex)
                        }
                        GETT -> {
                            sourceId = 4
                            taxi_icon.setImageResource(R.drawable.ic_gett_mini)
                            taxi_name.text = getString(R.string.gett_title)
                            balance_tv.text = getString(R.string.account_balance_format, it.balanceGett)
                        }
                        CITYMOBIL -> {
                            sourceId = 3
                            taxi_icon.setImageResource(R.drawable.ic_citymobil_mini)
                            taxi_name.text = getString(R.string.citymobil_title)
                            balance_tv.text = getString(R.string.account_balance_format, it.balanceCity)
                        }
                    }
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

        runDelayed(500) { cards_recycler?.let { if (it.isNotEmpty()) it[0].performClick() } }

        val keyboardPairs = mutableListOf<Pair<Button, Int>>(
            Pair(num_0, R.string.num0),
            Pair(num_1, R.string.num1),
            Pair(num_2, R.string.num2),
            Pair(num_3, R.string.num3),
            Pair(num_4, R.string.num4),
            Pair(num_5, R.string.num5),
            Pair(num_6, R.string.num6),
            Pair(num_7, R.string.num7),
            Pair(num_8, R.string.num8),
            Pair(num_9, R.string.num9)
        )

        keyboardPairs.map {
            sum_edit_text.setNumberClickListener(it.first, it.second)
        }

        erase_btn.setOnClickListener {
            val cursorPosition = sum_edit_text.selectionStart
            if (cursorPosition > 0) {
                sum_edit_text.text = sum_edit_text.text?.delete(cursorPosition - 1, cursorPosition)
                sum_edit_text.setSelection(cursorPosition - 1)
            }
        }

        apply_btn.setOnClickListener { sendRequest() }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
            activity?.makeCall(this)
        }
    }

    private fun sendRequest() {
        val sum = sum_edit_text.value
        if (sum.isEmpty()) {
            sum_input_view.setErrorTextColor(ColorStateList.valueOf(getColor(requireContext(), R.color.error_red_color)))
            sum_input_view.error = getString(R.string.enter_sum_error)
            return
        }

        viewModel.createWithdraw(sourceId, sum, cardId)
        sum_edit_text.setText("")
    }
}