package com.hsicen.core.dagger.android

import android.util.Log
import androidx.fragment.app.Fragment
import dagger.internal.Preconditions

/**
 * 作者：hsicen  2020/8/27 22:48
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：注入器
 */
object AndroidInjection {

  /**
   * 注入到Fragment.
   * @param fragment Fragment
   */
  fun inject(fragment: Fragment) {
    Preconditions.checkNotNull(fragment, "fragment")
    val hasFragmentInjector = findHasFragmentInjector(fragment)
    if (Log.isLoggable("dagger.android.support", Log.DEBUG)) {
      com.orhanobut.logger.Logger.d("dagger.android.support", String.format("An injector for %s was found in %s", fragment.javaClass.canonicalName, hasFragmentInjector.javaClass.canonicalName))
    }

    val fragmentInjector = hasFragmentInjector.fragmentInjector()
    Preconditions.checkNotNull(fragmentInjector, "%s.supportFragmentInjector() returned null", hasFragmentInjector.javaClass)
    fragmentInjector.inject(fragment)
  }

  /**
   * 找到Fragment注入器.
   * @param fragment Fragment
   * @return HasFragmentInjector
   */
  private fun findHasFragmentInjector(fragment: Fragment): HasFragmentInjector {
    var parentFragment: Fragment? = fragment

    do {
      parentFragment = parentFragment?.parentFragment
      if (parentFragment == null) {
        val activity = fragment.activity
        if (activity is HasFragmentInjector) {
          return activity
        }

        if (activity?.application is HasFragmentInjector) {
          return activity.application as HasFragmentInjector
        }

        throw IllegalArgumentException(String.format("No injector was found for %s", fragment.javaClass.canonicalName))
      }
    } while (parentFragment !is HasFragmentInjector)

    return parentFragment as? HasFragmentInjector ?: throw IllegalArgumentException(String.format("No injector was found for %s", fragment.javaClass.canonicalName))
  }
}
