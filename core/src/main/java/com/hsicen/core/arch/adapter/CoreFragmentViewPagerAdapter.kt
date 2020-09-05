package com.hsicen.core.arch.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter

/**
 * 作者：hsicen  2020/8/2 23:29
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：ViewPager以fragment作为item的adapter
 */
open class CoreFragmentViewPagerAdapter(
  manager: FragmentManager,
  private val fragments: List<Fragment>,
  private val titles: Array<String>
) : FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

  override fun getItem(position: Int): Fragment = fragments[position]

  override fun getCount(): Int = fragments.size

  override fun getPageTitle(position: Int): CharSequence = titles[position]

  override fun getItemPosition(`object`: Any): Int = PagerAdapter.POSITION_NONE
}
