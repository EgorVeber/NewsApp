import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.utils.DURATION_ERROR_INPUT
import ru.gb.veber.newsapi.databinding.AddKeysDialogFragmentBinding
import ru.gb.veber.newsapi.presentation.keymanagement.KeysManagementFragment.Companion.RESULT_KEY_DATA
import ru.gb.veber.newsapi.presentation.keymanagement.KeysManagementFragment.Companion.RESULT_KEY_LISTENER

class KeysDialogFragment : BottomSheetDialogFragment() {

    private var _binding: AddKeysDialogFragmentBinding? = null
    private val binding: AddKeysDialogFragmentBinding
        get() {
            return _binding!!
        }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = AddKeysDialogFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        init()
    }

    private fun init() {
        binding.saveKeyB.setOnClickListener {
            val key = binding.KeyET.text.toString()
            if (key.isNotEmpty()) {
                setFragmentResult(RESULT_KEY_LISTENER, bundleOf(RESULT_KEY_DATA to key))
                dismiss()
            } else {
                binding.keyTL.error = getString(R.string.error_input_email)
                Handler(Looper.getMainLooper()).postDelayed({
                    if (isAdded) {
                        binding.keyTL.error = null
                    }
                }, DURATION_ERROR_INPUT)
            }
        }
    }
}
