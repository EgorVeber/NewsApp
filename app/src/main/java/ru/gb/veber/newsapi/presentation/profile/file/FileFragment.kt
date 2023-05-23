package ru.gb.veber.newsapi.presentation.profile.file

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.github.terrakok.cicerone.Router
import ru.gb.veber.newsapi.R
import ru.gb.veber.newsapi.common.extentions.showSnackBar
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID
import ru.gb.veber.newsapi.common.utils.ACCOUNT_ID_DEFAULT
import ru.gb.veber.newsapi.common.utils.BundleInt
import ru.gb.veber.newsapi.databinding.FileFragmentBinding
import ru.gb.veber.newsapi.presentation.activity.BackPressedListener
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import javax.inject.Inject

class FileFragment : Fragment(R.layout.file_fragment), BackPressedListener {

    @Inject
    lateinit var router: Router

    private var accountID by BundleInt(ACCOUNT_ID, ACCOUNT_ID_DEFAULT)

    private val openLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            try {
                uri?.let {
                    openFileStorage(it)
                }
            } catch (e: Exception) {
                this.showSnackBar("Error Open File")
            }
        }

    private val saveLauncher =
        registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
            try {
                uri?.let {
                    saveFileStorage(it)
                }
            } catch (e: Exception) {
                this.showSnackBar("Error Open File")
            }
        }

    private fun saveFileStorage(it: Uri) {
        context?.contentResolver?.openOutputStream(it).use {
            val bytes = binding.editTextFile.text.toString().toByteArray()
            it?.write(bytes)
        }
    }


    private fun openFileStorage(it: Uri) {
        val data = context?.contentResolver?.openInputStream(it)?.use {
            String(it.readBytes())
        }
        binding.editTextFile.setText(data)
    }


    private lateinit var binding: FileFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val pathFiles: File? = context?.filesDir
        binding.pathText.text = pathFiles?.path

        binding.open.setOnClickListener {
            openFile2()
        }
        binding.save.setOnClickListener {
            saveFile()
        }

        binding.openStorage.setOnClickListener {
            openLauncher.launch(arrayOf("text/plain"))
        }

        binding.saveStorage.setOnClickListener {
            saveLauncher.launch("file-storage.txt")
        }
    }

    companion object {
        const val FILE_NAME = "my-file.txt"

        fun getInstance(accountID: Int) = FileFragment().apply { this.accountID = accountID }
    }

    private fun openFile1() {
        val file = File(context?.filesDir, FILE_NAME)
        val inputStream = FileInputStream(file)
        val reader = InputStreamReader(inputStream)
        val bufferReader = BufferedReader(reader)
        val data = bufferReader.use {
            it.readLines().joinToString(separator = "\n")
        }
        binding.editTextFile.setText(data)
    }

    //use закрывает файл и освобождает ресурсы
    private fun openFile2() {
        val file = File(context?.filesDir, FILE_NAME)
        val data = FileInputStream(file).use {
            String(it.readBytes())
        }
        binding.editTextFile.setText(data)
    }

    private fun saveFile() {
        val file = File(context?.filesDir, FILE_NAME)
        FileOutputStream(file).use {
            val bytes = binding.editTextFile.text.toString().toByteArray()
            it.write(bytes)
        }
    }

    override fun onBackPressedRouter(): Boolean {
        router.exit()
        return false
    }
}