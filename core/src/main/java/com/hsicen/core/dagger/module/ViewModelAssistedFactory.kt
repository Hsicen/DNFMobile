package com.hsicen.core.dagger.module

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

/**
 * 作者：hsicen  2020/9/5 23:13
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：ViewModel辅助工厂接口
 */
interface ViewModelAssistedFactory<VM : ViewModel> {

  /**
   * 创建ViewModel.
   * @param handle SavedStateHandle ui状态处理实例.
   * @return T
   */
  fun create(handle: SavedStateHandle): VM
}