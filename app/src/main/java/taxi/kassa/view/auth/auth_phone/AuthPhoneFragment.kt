package taxi.kassa.view.auth.auth_phone

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import kotlinx.android.synthetic.main.fragment_auth_phone.*
import kotlinx.android.synthetic.main.keyboard.*
import org.koin.android.ext.android.inject
import taxi.kassa.R
import taxi.kassa.util.*
import taxi.kassa.util.Constants.PHONE

class AuthPhoneFragment : Fragment(R.layout.fragment_auth_phone) {

    private val viewModel by inject<AuthPhoneViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        input_title.oneClick()

        with(viewModel) {
            isProgressVisible.observe(viewLifecycleOwner) { visible ->
                progress_bar.visibility = if (visible) VISIBLE else GONE
            }

            error.observe(viewLifecycleOwner) { phone_input_view.error = it }

            isLoggedIn.observe(viewLifecycleOwner) { loggedIn ->
                if (loggedIn) findNavController(view).navigate(R.id.action_authPhoneFragment_to_authCodeFragment)
            }
        }

        login_checkbox.setOnCheckedChangeListener { _, _ ->
            phone_input_view.error = null
        }

        with(phone_edit_text) {
            setMaskListener(phone_input_view)
            setKeyboard(
                arrayOf(num_0, num_1, num_2, num_3, num_4, num_5, num_6, num_7, num_8, num_9, erase_btn, apply_btn)
            ) { apply() }
        }

        login_button.setOnClickListener { apply() }

        back_arrow.setOnClickListener { activity?.onBackPressed() }
    }

    private fun apply() {
        if (!login_checkbox.isChecked) {
            phone_input_view.error = getString(R.string.accept_conditions_error)
            return
        }

        val phone: String = phone_edit_text.value.replace("[^\\d]", "")
        PreferenceManager(requireContext()).saveString(PHONE, phone)

        viewModel.login(phone)
    }
}