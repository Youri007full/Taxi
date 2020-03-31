package taxi.kassa.view.support.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import kotlinx.android.synthetic.main.fragment_write_message.*
import taxi.kassa.R
import taxi.kassa.util.hideKeyboard

class WriteMessageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_write_message, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        message_edit_text.requestFocus()

        back_arrow.setOnClickListener { activity?.onBackPressed() }

        send_button.setOnClickListener {
            if (message_edit_text.text.isBlank()) {
                message_edit_text.error = getString(R.string.input_message_error)
                return@setOnClickListener
            }
            findNavController(this).navigate(R.id.action_writeMessageFragment_to_successFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireView().hideKeyboard()
    }
}