package com.imcys.shiqueditinfodemo.ui.editInfo

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


sealed class EditInfoSaveState {
    data class SUCCESS(val msg: String) : EditInfoSaveState()
    data object LOADING : EditInfoSaveState()
    data class ERROR(val msg: String) : EditInfoSaveState()
    data object DEFAULT : EditInfoSaveState()
}


data class UserEditInfo(
    var nickname: String = "",
    var birthday: String = "",
    var sex: Int = 0, // 暂时不用枚举了，0为男 1为女
    var face: Bitmap? = null
)


class EditInfoViewModel : ViewModel() {
    private val _editInfoSaveState = MutableStateFlow<EditInfoSaveState>(EditInfoSaveState.DEFAULT)
    val editInfoSaveState = _editInfoSaveState.asStateFlow()

//    // 如果有持久化需求
//    private val _userEditInfo = MutableStateFlow(UserEditInfo())
//    val userEditInfo = _userEditInfo.asStateFlow()


    fun updateEditInfo(userEditInfo: UserEditInfo) {
        viewModelScope.launch {
            if (userEditInfo.nickname.isBlank()) {
                _editInfoSaveState.update { EditInfoSaveState.ERROR("请填写昵称") }
            } else if (userEditInfo.birthday.isBlank()) {
                _editInfoSaveState.update { EditInfoSaveState.ERROR("请填写生日") }
            } else if (userEditInfo.face == null) {
                _editInfoSaveState.update { EditInfoSaveState.ERROR("请选择头像") }
            } else {
                _editInfoSaveState.update { EditInfoSaveState.LOADING }
                delay(2000)
                _editInfoSaveState.update { EditInfoSaveState.SUCCESS("登录成功") }
            }
            delay(300)
            _editInfoSaveState.update { EditInfoSaveState.DEFAULT }
        }
    }


}