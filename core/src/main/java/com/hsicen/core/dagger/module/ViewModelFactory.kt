package com.hsicen.core.dagger.module

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import javax.inject.Inject

/**
 * 作者：hsicen  2020/9/5 23:13
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：ViewModel工厂，创建ViewModel
 */
open class ViewModelFactory @Inject constructor(
  private val viewModelMap: MutableMap<Class<out ViewModel>, ViewModelAssistedFactory<out ViewModel>>,
  owner: SavedStateRegistryOwner,
  defArgs: Bundle?
) : AbstractSavedStateViewModelFactory(owner, defArgs) {

  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T =
    viewModelMap[modelClass]?.create(handle) as? T ?: throw IllegalStateException("Unknown ViewModel class")
}