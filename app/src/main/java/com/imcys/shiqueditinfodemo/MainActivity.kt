package com.imcys.shiqueditinfodemo

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.imcys.shiqueditinfodemo.base.BaseActivity
import com.imcys.shiqueditinfodemo.databinding.ActivityMainBinding
import com.imcys.shiqueditinfodemo.extend.collectState
import com.imcys.shiqueditinfodemo.ui.editInfo.EditInfoSaveState
import com.imcys.shiqueditinfodemo.ui.editInfo.EditInfoViewModel
import com.imcys.shiqueditinfodemo.ui.editInfo.UserEditInfo
import com.kongzue.dialogx.datepicker.DatePickerDialog
import com.kongzue.dialogx.datepicker.interfaces.OnDateSelected
import com.kongzue.dialogx.dialogs.BottomMenu
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.dialogs.WaitDialog
import com.kongzue.dialogx.interfaces.OnBindView
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

    private val regTakePicturePreviewResult =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
            it?.let { bitmap ->
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
            // 数据绑定
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

            sexRadioGroup.setOnCheckedChangeListener { radioGroup, i ->
                // 数据绑定
                when (i) {
                    R.id.sex_boy_radio -> {
                        userEditInfo.sex = 0
                    }

                    R.id.sex_girl_radio -> {
                        userEditInfo.sex = 1
                    }
                }

            }

            faceImage.setOnClickListener {
                // 获取头像
//                regSelectFaceImageResult.launch("image/*")

                BottomMenu.show(listOf("使用默认头像", "拍照选择", "从相册里选择"))
                    .setMessage("请选择头像")
                    .setOnMenuItemClickListener { dialog, text, index ->
                        when (index) {
                            0 -> {
                                // 使用默认头像
                                binding.faceImage.setImageResource(R.drawable.ic_launcher_background)
                                userEditInfo.face = binding.faceImage.drawable.toBitmap()
                            }

                            1 -> {
                                // 拍照选择
                                regTakePicturePreviewResult.launch(null)
                            }

                            2 -> {
                                // 从相册里选择
                                regSelectFaceImageResult.launch("image/*")
                            }
                        }
                        false
                    }
            }

            // 提交按钮
            submitButton.setOnClickListener {
                userEditInfo = userEditInfo.copy(
                    nickname = nicknameEdit.text.toString(),
                    birthday = birthdayEdit.text.toString(),
                )
                viewModel.updateEditInfo(userEditInfo)
            }

            // 返回按钮
            backImage.setOnClickListener {
                finish()
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

        viewModel.editInfoSaveState.collectState(this) {
            when (it) {
                is EditInfoSaveState.DEFAULT -> {
                }

                is EditInfoSaveState.ERROR -> {
                    PopTip.show(it.msg)
                    WaitDialog.dismiss()
                }


                is EditInfoSaveState.LOADING -> {
                    WaitDialog.show("正在加载").setCustomView(object :
                        OnBindView<WaitDialog?>(R.layout.dialog_load) {
                        override fun onBind(dialog: WaitDialog?, v: View) {
                        }
                    })
                }

                is EditInfoSaveState.SUCCESS -> {
                    PopTip.show("成功🏅")
                    WaitDialog.dismiss()
                }
            }
        }

//        lifecycleScope.launch {
//            // 有消息时消费，无消息时挂起，销毁时解除，不会阻塞线程
//            repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.editInfoSaveState.collect {
//                    when (it) {
//                        is EditInfoSaveState.DEFAULT -> {
//                        }
//
//                        is EditInfoSaveState.ERROR -> {
//                            PopTip.show(it.msg)
//                            WaitDialog.dismiss()
//                        }
//
//                        is EditInfoSaveState.LOADING -> {
//                            WaitDialog.show("正在加载").setCustomView(object :
//                                OnBindView<WaitDialog?>(R.layout.dialog_load) {
//                                override fun onBind(dialog: WaitDialog?, v: View) {
//                                }
//                            })
//                        }
//
//                        is EditInfoSaveState.SUCCESS -> {
//                            PopTip.show("成功🏅")
//                            WaitDialog.dismiss()
//                        }
//                    }
//                }
//            }
//
//
//        }


    }


    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

}