package com.hsicen.core.ui.dialog

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.*
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.annotation.MenuRes
import androidx.core.internal.view.SupportMenu
import androidx.core.internal.view.SupportMenuItem
import androidx.core.view.ActionProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hsicen.core.R
import com.hsicen.core.arch.adapter.CoreRvAdapter
import com.hsicen.core.ui.decoration.KItemDecoration
import com.hsicen.core.utils.glide.loadImage
import com.hsicen.extensions.extensions.color
import com.hsicen.extensions.extensions.dimen
import com.hsicen.extensions.extensions.dp2px
import com.hsicen.extensions.extensions.drawableRes

/**
 * 作者：hsicen  2020/9/6 16:31
 * 邮箱：codinghuang@163.com
 * 功能：
 * 描述：弹出式菜单
 *
 * 使用:
 * </p>
 * KMenu.with(ctx)
 *      .applyMenu(menuResId)
 *      .onMenuItemClick{
 *      }
 *      .showAsDropDown()
 * <p>
 */
class KMenu private constructor(internal val ctx: Context) : Menu {

  /** 所有菜单选项. */
  private val menuItems = mutableListOf<MenuItemImpl>()

  /** 菜单. */
  private val rvMenu by lazy { RecyclerView(ctx) }

  /** PopupWindow. */
  private val popupWindow by lazy {
    PopupWindow(rvMenu, ctx.dp2px(165), ViewGroup.LayoutParams.WRAP_CONTENT, true)
  }

  /** 菜单适配器. */
  private val adapter by lazy {
    CoreRvAdapter<MenuItemImpl>(R.layout.item_menu).apply {
      render = { model, helper ->
        helper.setText(R.id.tv_menu_title, model._title)
        helper.setGone(R.id.iv_menu_icon, model.icon != null || model.iconUrl != null)
        if (model.icon != null) {
          helper.setImageDrawable(R.id.iv_menu_icon, model.icon)
        } else {
          helper.getView<ImageView>(R.id.iv_menu_icon).loadImage(model.iconUrl)
        }
      }

      onItemClick = { _, position, item ->
        onMenuItemClick?.invoke(position, item)
        popupWindow.dismiss()
      }
    }
  }

  /** 菜单点击回调. */
  private var onMenuItemClick: ((Int, MenuItem) -> Unit)? = null

  /** 当前是否显示. */
  var isShowing = popupWindow.isShowing

  init {
    popupWindow.isFocusable = true
    popupWindow.setBackgroundDrawable(ColorDrawable())
    popupWindow.setOnDismissListener {
      (ctx as? Activity)?.window?.let {
        val lp = it.attributes
        lp.alpha = 1f
        it.attributes = lp
      }
    }
    (ctx as? Activity)?.window?.let {
      val lp = it.attributes
      lp.alpha = 0.5f
      it.attributes = lp
    }

    rvMenu.layoutManager = LinearLayoutManager(ctx)
    rvMenu.setBackgroundResource(R.drawable.bg_k_menu)
    rvMenu.adapter = adapter
    rvMenu.addItemDecoration(
      KItemDecoration(
        ctx.dp2px(1),
        ctx.color(R.color.divider_line5),
        ctx.dimen(R.dimen.padding_z2),
        ctx.dimen(R.dimen.padding_z2),
        endOffsetCount = 1
      )
    )
  }

  /**
   * 应用菜单.
   * @param menuResId Int
   * @return KMenu
   */
  fun applyMenu(@MenuRes menuResId: Int): KMenu {
    // 解析menu.
    MenuInflater(ctx).inflate(menuResId, this)
    return this
  }

  /**
   * 应用菜单.
   * @param title Array<out String>
   * @return KMenu
   */
  fun applyMenu(vararg title: String): KMenu {
    title.forEach { add(it) }
    return this
  }

  /**
   * 菜单点击回调.
   * @param block (Int, MenuItem) -> Unit
   * @return KMenu
   */
  fun onMenuItemClick(block: (position: Int, item: MenuItem) -> Unit): KMenu {
    this.onMenuItemClick = block
    return this
  }

  /**
   * 显示,
   * @param anchor View
   */
  fun showAsDropDown(anchor: View) {
    popupWindow.showAsDropDown(anchor)
  }

  /**
   * 显示.
   * @param anchor View
   * @param xoff Int
   * @param yoff Int
   */
  fun showAsDropDown(anchor: View, xoff: Int, yoff: Int) {
    popupWindow.showAsDropDown(anchor, xoff, yoff)
  }

  /**
   * 显示.
   * @param anchor View
   * @param xoff Int
   * @param yoff Int
   * @param gravity Int
   */
  fun showAsDropDown(anchor: View, xoff: Int, yoff: Int, gravity: Int) {
    popupWindow.showAsDropDown(anchor, xoff, yoff, gravity)
  }

  /**
   * 显示.
   * @param parent View
   * @param gravity Int
   * @param x Int
   * @param y Int
   */
  fun showAtLocation(parent: View, gravity: Int, x: Int, y: Int) {
    popupWindow.showAtLocation(parent, gravity, x, y)
  }

  /**
   * 创建菜单.
   * @param group Int
   * @param id Int
   * @param categoryOrder Int
   * @param ordering Int
   * @param title CharSequence
   * @param iconUrl String?
   * @param defaultShowAsAction Int
   * @return MenuItemImpl
   */
  private fun createNewMenuItem(
    group: Int,
    id: Int,
    categoryOrder: Int,
    ordering: Int,
    title: CharSequence,
    iconUrl: String? = null,
    defaultShowAsAction: Int
  ): MenuItemImpl {
    return MenuItemImpl(
      this,
      group,
      id,
      categoryOrder,
      ordering,
      title,
      iconUrl,
      defaultShowAsAction
    )
  }

  /**
   * 添加.
   * @param group Int
   * @param id Int
   * @param categoryOrder Int
   * @param title CharSequence
   * @param iconUrl String?
   * @return MenuItem
   */
  private fun addInternal(group: Int, id: Int, categoryOrder: Int, title: CharSequence, iconUrl: String? = null): MenuItem {
    val ordering = getOrdering(categoryOrder)
    val item = createNewMenuItem(group, id, categoryOrder, ordering, title, iconUrl, SupportMenuItem.SHOW_AS_ACTION_NEVER)
    menuItems.add(findInsertIndex(menuItems, ordering), item)
    adapter.addData(item)
    return item
  }

  override fun clear() {
  }

  override fun removeItem(id: Int) {
  }

  override fun setGroupCheckable(group: Int, checkable: Boolean, exclusive: Boolean) {
  }

  override fun performIdentifierAction(id: Int, flags: Int): Boolean = false

  override fun setGroupEnabled(group: Int, enabled: Boolean) {
  }

  override fun getItem(index: Int): MenuItem = menuItems[index]

  override fun performShortcut(keyCode: Int, event: KeyEvent?, flags: Int): Boolean = false

  override fun removeGroup(groupId: Int) {
  }

  override fun setGroupVisible(group: Int, visible: Boolean) {
  }

  override fun add(title: CharSequence?): MenuItem =
    addInternal(0, 0, 0, title.toString())

  /**
   * 支持icon url格式.
   * @param title CharSequence?
   * @param iconUrl String
   * @return MenuItem
   */
  fun add(title: CharSequence?, iconUrl: String): MenuItem =
    addInternal(0, 0, 0, title.toString(), iconUrl)

  override fun add(titleRes: Int): MenuItem =
    addInternal(0, 0, 0, ctx.resources.getString(titleRes))

  override fun add(groupId: Int, itemId: Int, order: Int, title: CharSequence?): MenuItem =
    addInternal(groupId, itemId, order, title.toString())

  override fun add(groupId: Int, itemId: Int, order: Int, titleRes: Int): MenuItem =
    addInternal(groupId, itemId, order, ctx.resources.getString(titleRes))

  override fun isShortcutKey(keyCode: Int, event: KeyEvent?): Boolean = false

  override fun setQwertyMode(isQwerty: Boolean) {
  }

  override fun hasVisibleItems(): Boolean = true

  override fun addSubMenu(title: CharSequence?): SubMenu {
    TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
  }

  override fun addSubMenu(titleRes: Int): SubMenu {
    TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
  }

  override fun addSubMenu(groupId: Int, itemId: Int, order: Int, title: CharSequence?): SubMenu {
    TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
  }

  override fun addSubMenu(groupId: Int, itemId: Int, order: Int, titleRes: Int): SubMenu {
    TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
  }

  override fun addIntentOptions(
    groupId: Int,
    itemId: Int,
    order: Int,
    caller: ComponentName?,
    specifics: Array<out Intent>?,
    intent: Intent?,
    flags: Int,
    outSpecificItems: Array<out MenuItem>?
  ): Int = 0

  override fun findItem(id: Int): MenuItem? = menuItems.find { it.itemId == id }

  override fun size(): Int = menuItems.size

  override fun close() {
  }

  @SuppressLint("RestrictedApi")
  class MenuItemImpl(
    val mennu: KMenu,
    val group: Int,
    val id: Int,
    val categoryOrder: Int,
    val ordering: Int,
    var _title: CharSequence,
    var iconUrl: String?,
    val showAsAction: Int
  ) : SupportMenuItem {

    private var contentDescription: CharSequence = ""
    private var tooltipText: CharSequence = ""
    private var iconTintList: ColorStateList? = null
    private var iconTintMode: PorterDuff.Mode? = null
    private var icon: Drawable? = null

    override fun expandActionView(): Boolean = false

    override fun requiresActionButton(): Boolean = true

    override fun hasSubMenu(): Boolean = false

    override fun getMenuInfo(): ContextMenu.ContextMenuInfo {
      TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getContentDescription(): CharSequence = contentDescription

    override fun getTooltipText(): CharSequence = tooltipText

    override fun getItemId(): Int = id

    override fun getAlphabeticShortcut(): Char {
      TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun setEnabled(enabled: Boolean): MenuItem {
      return this
    }

    override fun setTitle(title: CharSequence?): MenuItem {
      _title = title.toString()
      return this
    }

    override fun setTitle(title: Int): MenuItem {
      _title = mennu.ctx.getString(title)
      return this
    }

    override fun setChecked(checked: Boolean): MenuItem {
      return this
    }

    override fun getActionView(): View {
      TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getTitle(): CharSequence = _title

    override fun getOrder(): Int = order

    override fun getSupportActionProvider(): ActionProvider {
      TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getIconTintList(): ColorStateList? = iconTintList

    override fun requiresOverflow(): Boolean = false

    override fun getNumericModifiers(): Int {
      TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun setOnActionExpandListener(listener: MenuItem.OnActionExpandListener?): MenuItem {
      return this
    }

    override fun getIntent(): Intent = Intent()

    override fun setVisible(visible: Boolean): MenuItem {
      return this
    }

    override fun isEnabled(): Boolean = true

    override fun isCheckable(): Boolean = true

    override fun setShowAsAction(actionEnum: Int) {
    }

    override fun getGroupId(): Int = groupId

    override fun setActionProvider(actionProvider: android.view.ActionProvider?): MenuItem {
      return this
    }

    override fun setTitleCondensed(title: CharSequence?): MenuItem {
      return this
    }

    override fun getNumericShortcut(): Char {
      TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun isActionViewExpanded(): Boolean = false

    override fun setIconTintMode(tintMode: PorterDuff.Mode?): MenuItem {
      this.iconTintMode = tintMode ?: return this
      return this
    }

    override fun collapseActionView(): Boolean = false

    override fun isVisible(): Boolean = true

    override fun setNumericShortcut(numericChar: Char, numericModifiers: Int): MenuItem {
      TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun setNumericShortcut(numericChar: Char): MenuItem {
      return this
    }

    override fun setActionView(view: View?): MenuItem {
      return this
    }

    override fun setActionView(resId: Int): MenuItem {
      return this
    }

    override fun setIconTintList(tint: ColorStateList?): MenuItem {
      this.iconTintList = tint
      return this
    }

    override fun setAlphabeticShortcut(alphaChar: Char, alphaModifiers: Int): MenuItem {
      return this
    }

    override fun setAlphabeticShortcut(alphaChar: Char): MenuItem {
      return this
    }

    override fun setIcon(icon: Drawable?): MenuItem {
      this.icon = icon
      return this
    }

    override fun setIcon(iconRes: Int): MenuItem {
      if (iconRes == 0) return this
      this.icon = mennu.ctx.drawableRes(iconRes)
      return this
    }

    override fun setTooltipText(tooltipText: CharSequence?): SupportMenuItem {
      this.tooltipText = tooltipText.toString()
      return this
    }

    override fun isChecked(): Boolean = false

    override fun getAlphabeticModifiers(): Int {
      TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun setIntent(intent: Intent?): MenuItem {
      return this
    }

    override fun setShortcut(
      numericChar: Char,
      alphaChar: Char,
      numericModifiers: Int,
      alphaModifiers: Int
    ): MenuItem {
      return this
    }

    override fun setShortcut(numericChar: Char, alphaChar: Char): MenuItem {
      return this
    }

    override fun getIcon(): Drawable? = icon

    override fun setShowAsActionFlags(actionEnum: Int): MenuItem {
      return this
    }

    override fun setContentDescription(contentDescription: CharSequence?): SupportMenuItem {
      return this
    }

    override fun setOnMenuItemClickListener(menuItemClickListener: MenuItem.OnMenuItemClickListener?): MenuItem {
      return this
    }

    override fun setSupportActionProvider(actionProvider: ActionProvider?): SupportMenuItem {
      return this
    }

    override fun getActionProvider(): android.view.ActionProvider {
      TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun setCheckable(checkable: Boolean): MenuItem = this

    override fun getSubMenu(): SubMenu {
      TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getTitleCondensed(): CharSequence {
      TODO("not implemented") // To change body of created functions use File | Settings | File Templates.
    }

    override fun getIconTintMode(): PorterDuff.Mode? = iconTintMode
  }

  companion object {
    private val sCategoryToOrder = intArrayOf(
      1, /* No category */
      4, /* CONTAINER */
      5, /* SYSTEM */
      3, /* SECONDARY */
      2, /* ALTERNATIVE */
      0
    )/* SELECTED_ALTERNATIVE */

    private fun getOrdering(categoryOrder: Int): Int {
      val index = categoryOrder and SupportMenu.CATEGORY_MASK shr SupportMenu.CATEGORY_SHIFT

      if (index < 0 || index >= sCategoryToOrder.size) {
        throw IllegalArgumentException("order does not contain a valid category.")
      }

      return sCategoryToOrder[index] shl SupportMenu.CATEGORY_SHIFT or (categoryOrder and SupportMenu.USER_MASK)
    }

    private fun findInsertIndex(items: List<MenuItemImpl>, ordering: Int): Int {
      for (i in items.indices.reversed()) {
        val item = items[i]
        if (item.ordering <= ordering) {
          return i + 1
        }
      }

      return 0
    }

    /**
     * 新建菜单实例.
     * @param ctx Context
     * @return KMenu
     */
    fun with(ctx: Context): KMenu = KMenu(ctx)
  }
}