package taxi.kassa.view.profile

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telephony.PhoneNumberUtils.formatNumber
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.findNavController
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import taxi.kassa.R
import taxi.kassa.util.*
import taxi.kassa.util.Constants.MESSAGES_COUNTER
import taxi.kassa.util.Constants.PHONE
import taxi.kassa.util.Constants.PUSH_COUNTER
import taxi.kassa.util.Constants.SUPPORT_PHONE_NUMBER
import taxi.kassa.util.Constants.TOKEN
import taxi.kassa.util.Constants.accessToken
import taxi.kassa.view.MainActivity
import java.text.NumberFormat
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var prefManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel { parametersOf() }
        prefManager = PreferenceManager(requireContext())
        accessToken = prefManager.getString(TOKEN) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_profile, container, false)

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLogoutButtonConstraint()

        viewModel.isNetworkAvailable.observe(viewLifecycleOwner, Observer { available ->
            if (!available) activity?.longToast(getString(R.string.internet_unavailable))
        })

        viewModel.isProgressVisible.observe(viewLifecycleOwner, Observer { visible ->
            progress_bar.visibility = if (visible) VISIBLE else GONE
        })

        viewModel.error.observe(viewLifecycleOwner, Observer {
            activity?.shortToast(it)
        })

        viewModel.responseOwner.observe(viewLifecycleOwner, Observer { response ->
            response?.let {
                name_tv.text = it.fullName
                number_tv.text = getString(
                    R.string.profile_format,
                    formatNumber(it.phone, Locale.getDefault().country)
                ).replaceFirst(" ", "(").replace(" ", ")")

                val format = NumberFormat.getInstance(Locale("ru", "RU"))
                balance_tv.text = getString(
                    R.string.balance_format,
                    format.format(it.balanceTotal.toDouble())
                ).replace(',', '.')
            }
        })

        viewModel.notifications.observe(viewLifecycleOwner, Observer {
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
        })

        viewModel.incomingMessages.observe(viewLifecycleOwner, Observer {
            val readMessages = PreferenceManager(requireContext()).getInt(MESSAGES_COUNTER)
            val unreadMessages = it.size - (readMessages ?: 0)

            if (unreadMessages > 0) {
                message_counter.visible()
                message_counter.text = getString(R.string.profile_format, unreadMessages.toString())
            } else {
                message_counter.gone()
            }
        })

        balance_view.setOnClickListener {
            findNavController(this).navigate(R.id.action_profileFragment_to_balanceFragment)
        }

        orders_view.setOnClickListener {
            findNavController(this).navigate(R.id.action_profileFragment_to_ordersFragment)
        }

        withdrawal_applications_view.setOnClickListener {
            findNavController(this).navigate(R.id.action_profileFragment_to_withdrawsFragment)
        }

        accounts_and_cards_view.setOnClickListener {
            findNavController(this).navigate(R.id.action_profileFragment_to_accountsFragment)
        }

        support_service_view.setOnClickListener {
            findNavController(this).navigate(R.id.action_profileFragment_to_supportFragment)
        }

        phone_image.setOnClickListener {
            context?.showTwoButtonsDialog(
                getString(R.string.support_service),
                getString(R.string.support_service_message),
                getString(R.string.cancel),
                getString(R.string.call)
            ) {
                makeCall()
            }
        }

        notification_image.setOnClickListener {
            findNavController(this).navigate(R.id.action_profileFragment_to_notificationsFragment)
        }

        notification_count.setOnClickListener {
            findNavController(this).navigate(R.id.action_profileFragment_to_notificationsFragment)
        }

        exit_tv.setOnClickListener {
            context?.showTwoButtonsDialog(
                getString(R.string.exit),
                getString(R.string.exit_message),
                getString(R.string.no),
                getString(R.string.yes)
            ) {
                logout()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            makeCall()
        }
    }

    private fun makeCall() {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$SUPPORT_PHONE_NUMBER")

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 10)
            return
        } else {
            try {
                startActivity(callIntent)
            } catch (ex: ActivityNotFoundException) {
                requireActivity().shortToast(getString(R.string.not_find_call_app))
            }
        }
    }

    private fun logout() {
        prefManager.saveString(PHONE, "")
        prefManager.saveString(TOKEN, "")

        activity?.finish()
        startActivity(Intent(requireActivity(), MainActivity::class.java))
    }

    private fun setLogoutButtonConstraint() {
        if (requireContext().getScreenSize() < 5.5) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(parent_layout)
            constraintSet.connect(
                R.id.exit_tv,
                ConstraintSet.TOP,
                R.id.bottom_line,
                ConstraintSet.BOTTOM
            )
            constraintSet.applyTo(parent_layout)
        }
    }
}