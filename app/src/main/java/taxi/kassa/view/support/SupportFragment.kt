package taxi.kassa.view.support

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment.findNavController
import kotlinx.android.synthetic.main.fragment_support.*
import kotlinx.android.synthetic.main.fragment_support.back_arrow
import kotlinx.android.synthetic.main.fragment_support.notification_image
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import taxi.kassa.R
import taxi.kassa.util.Constants
import taxi.kassa.util.shortToast

class SupportFragment : Fragment() {

    private lateinit var viewModel: SupportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel { parametersOf() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_support, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getNotifications()

        viewModel.notifications.observe(viewLifecycleOwner, Observer {
            when (it.size) {
                0 -> {
                    notification_count.visibility = INVISIBLE
                    notification_image.visibility = VISIBLE
                }
                else -> {
                    notification_count.text = it.size.toString()
                    notification_count.visibility = VISIBLE
                    notification_image.visibility = INVISIBLE
                }
            }
        })

        back_arrow.setOnClickListener { activity?.onBackPressed() }

        write_to_us_view.setOnClickListener {
            findNavController(this).navigate(R.id.action_supportFragment_to_writeMessageFragment)
        }

        chat_history_view.setOnClickListener {
            findNavController(this).navigate(R.id.action_supportFragment_to_chatHistoryFragment)
        }

        notification_image.setOnClickListener {
            findNavController(this).navigate(R.id.action_supportFragment_to_notificationsFragment)
        }

        notification_count.setOnClickListener {
            findNavController(this).navigate(R.id.action_supportFragment_to_notificationsFragment)
        }

        call_button.setOnClickListener { makeCall() }
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
        callIntent.data = Uri.parse("tel:${Constants.SUPPORT_PHONE_NUMBER}")

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
}