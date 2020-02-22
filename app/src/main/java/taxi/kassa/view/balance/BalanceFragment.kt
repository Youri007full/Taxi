package taxi.kassa.view.balance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_balance.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import taxi.kassa.R
import taxi.kassa.util.shortToast

class BalanceFragment : Fragment() {

    private lateinit var viewModel: BalanceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel { parametersOf() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_balance, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getUserInfo()

        viewModel.error.observe(viewLifecycleOwner, Observer {
            activity?.shortToast(it)
        })

        viewModel.responseOwner.observe(viewLifecycleOwner, Observer { response ->
            response?.let {
                balance_tv.text = getString(R.string.balance_format, it.balanceTotal)
                yandex_amount.text = getString(R.string.balance_format, it.balanceYandex)
                citymobil_amount.text = getString(R.string.balance_format, it.balanceCity)
                gett_amount.text = getString(R.string.balance_format, it.balanceGett)

                yandex_amount.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (it.balanceYandex.toFloat() > 0.0F) R.color.balance_green else R.color.balance_red
                    )
                )
                withdraw_yandex_tv.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (it.balanceYandex.toFloat() > 0.0F) R.color.gray_intro_text else R.color.colorAccent
                    )
                )

                citymobil_amount.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (it.balanceYandex.toFloat() > 0.0F) R.color.balance_green else R.color.balance_red
                    )
                )
                withdraw_citymobil_tv.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (it.balanceYandex.toFloat() > 0.0F) R.color.gray_intro_text else R.color.colorAccent
                    )
                )

                gett_amount.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (it.balanceYandex.toFloat() > 0.0F) R.color.balance_green else R.color.balance_red
                    )
                )
                withdraw_gett_tv.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        if (it.balanceYandex.toFloat() > 0.0F) R.color.gray_intro_text else R.color.colorAccent
                    )
                )
            }
        })

        back_arrow.setOnClickListener {
            activity?.onBackPressed()
        }
    }
}