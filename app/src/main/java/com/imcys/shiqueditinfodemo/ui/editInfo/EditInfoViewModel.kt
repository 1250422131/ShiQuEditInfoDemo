package com.imcys.shiqueditinfodemo.ui.editInfo

import android.graphics.Bitmap
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
    var face: Bitmap? = null
)


class EditInfoViewModel : ViewModel() {
    private val _editInfoSaveState = MutableStateFlow<EditInfoSaveState>(EditInfoSaveState.DEFAULT)
    val editInfoSaveState = _editInfoSaveState.asStateFlow()

    // 如果有持久化需求
    //    private val _userEditInfo = MutableStateFlow(UserEditInfo())
    //    val userEditInfo = _userEditInfo.asStateFlow()

    fun updateEditInfo(userEditInfo: UserEditInfo) {
        viewModelScope.launch {
            if (userEditInfo.nickname.isBlank()) {
                _editInfoSaveState.emit(EditInfoSaveState.ERROR("请填写昵称") )
            } else if (userEditInfo.birthday.isBlank()) {
                _editInfoSaveState.emit(EditInfoSaveState.ERROR("请填写生日"))
            } else {
                _editInfoSaveState.emit(EditInfoSaveState.LOADING)
                delay(3000)
                _editInfoSaveState.emit( EditInfoSaveState.SUCCESS("请填写生日") )
            }
            delay(300)
            _editInfoSaveState.emit( EditInfoSaveState.DEFAULT )

        }
    }


}