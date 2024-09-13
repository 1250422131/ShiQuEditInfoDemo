package com.imcys.shiqueditinfodemo

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.imcys.shiqueditinfodemo.base.BaseActivity
import com.imcys.shiqueditinfodemo.databinding.ActivityMainBinding
import com.imcys.shiqueditinfodemo.ui.editInfo.EditInfoSaveState
import com.imcys.shiqueditinfodemo.ui.editInfo.EditInfoViewModel
import com.imcys.shiqueditinfodemo.ui.editInfo.UserEditInfo
import com.kongzue.dialogx.datepicker.DatePickerDialog
import com.kongzue.dialogx.datepicker.interfaces.OnDateSelected
import com.kongzue.dialogx.dialogs.PopTip
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


class MainActivity : BaseActivity<ActivityMainBinding>() {

    private val viewModel: EditInfoViewModel by viewModels()

    private var userEditInfo = UserEditInfo()

    private val regSelectFaceImageResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) {
            it?.let { imageUri ->
                val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                binding.faceImage.setImageBitmap(bitmap)
                userEditInfo.face = bitmap
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initDataBind()
        initView()
        bindEnv()
    }

    private fun bindEnv() {
        binding.apply {
            birthdayEdit.setOnClickListener {
                // 获取当前年月日
                val dateStr: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                    val localDate = LocalDate.now()
                    localDate.format(formatter)
                } else {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    sdf.format(Date())
                }

                // 高危风险行为
                val dateSplitResult = dateStr.split("-").map { it.toInt() }
                val currentYear = dateSplitResult[0]
                val currentMonth = dateSplitResult[1]
                val currentDay = dateSplitResult[2]

                DatePickerDialog
                    .build()
                    .setMaxTime(currentYear, currentMonth, currentDay)
                    .setDefaultSelect(currentYear, currentMonth, currentDay)    //设置默认选中日期
                    .show(object : OnDateSelected() {
                        override fun onSelect(text: String, year: Int, month: Int, day: Int) {
                            birthdayEdit.setText(text)
                        }
                    })
            }

            faceImage.setOnClickListener {
                // 获取头像
                regSelectFaceImageResult.launch("image/*")
            }

            submitButton.setOnClickListener {
                userEditInfo = userEditInfo.copy(
                    nickname = nicknameEdit.text.toString(),
                    birthday = birthdayEdit.text.toString(),
                )
                viewModel.updateEditInfo(userEditInfo)
            }


        }
    }

    private fun initView() {
        // 初始化任务
        binding.apply {
            sexBoyRadio.isChecked = true
        }


    }


    private fun initDataBind() {

        lifecycleScope.launch {
            // 有消息时消费，无消息时挂起，销毁时解除，不会阻塞线程
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.editInfoSaveState.collect {
                    when (it) {
                        is EditInfoSaveState.DEFAULT -> {}

                        is EditInfoSaveState.ERROR -> {
                            PopTip.show(it.msg)
                        }

                        is EditInfoSaveState.LOADING -> {

                        }

                        is EditInfoSaveState.SUCCESS -> {

                        }
                    }
                }
            }


        }

    }


    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

}