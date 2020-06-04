package com.kitlink.activity

import com.kitlink.R
import com.kitlink.fragment.*
import com.kitlink.popup.CommonPopupWindow
import com.mvp.IPresenter
import com.mvp.presenter.GetBindDeviceTokenPresenter
import com.mvp.view.GetBindDeviceTokenView
import com.util.L
import kotlinx.android.synthetic.main.activity_soft_ap.*

class SoftApActivity : PActivity(), GetBindDeviceTokenView {

    private lateinit var presenter: GetBindDeviceTokenPresenter
    private var ssid = ""
    private var bssid = ""
    private var password = ""

    private lateinit var softAppStepFragment: SoftAppStepFragment
    private lateinit var wifiFragment: WifiFragment
    private lateinit var softHotspotFragment: SoftHotspotFragment
    private lateinit var connectProgressFragment: ConnectProgressFragment

    private var closePopup: CommonPopupWindow? = null

    override fun getContentView(): Int {
        return R.layout.activity_soft_ap
    }

    override fun initView() {
        presenter = GetBindDeviceTokenPresenter(this)
        softAppStepFragment = SoftAppStepFragment()
        wifiFragment = WifiFragment(WifiFragment.soft_ap)
        softHotspotFragment = SoftHotspotFragment()
        connectProgressFragment = ConnectProgressFragment(WifiFragment.soft_ap)

        this.supportFragmentManager.beginTransaction()
            .add(R.id.container_soft_ap, softAppStepFragment)
            .show(softAppStepFragment)
            .commit()
    }

    override fun setListener() {
        tv_soft_ap_cancel.setOnClickListener {
            if (connectProgressFragment.isVisible) {
                showPopup()
            } else {
                finish()
            }
        }
        softAppStepFragment.onNextListener = object : SoftAppStepFragment.OnNextListener {
            override fun onNext() {
                showTitle(
                    getString(R.string.smart_config_second_title),
                    getString(R.string.cancel)
                )
                showFragment(wifiFragment, softAppStepFragment)
            }
        }
        wifiFragment.onCommitWifiListener = object : WifiFragment.OnCommitWifiListener {
            override fun commitWifi(ssid: String, bssid: String?, password: String) {
                this@SoftApActivity.let {
                    it.ssid = ssid
                    it.bssid = if (bssid == null) "" else bssid
                    it.password = password
                }
                presenter.getBindDeviceToken()
            }
        }
        softHotspotFragment.onNextListener = object : SoftHotspotFragment.OnNextListener {
            override fun onNext() {
                connectProgressFragment.setWifiInfo(ssid, bssid, password)
                showFragment(connectProgressFragment, softHotspotFragment)
                showTitle(
                    getString(R.string.smart_config_third_connect_progress)
                    , getString(R.string.close)
                )
            }
        }
        connectProgressFragment.onRestartListener =
            object : ConnectProgressFragment.OnRestartListener {
                override fun restart() {
                    showFragment(softAppStepFragment, connectProgressFragment)
                    showTitle(
                        getString(R.string.soft_ap),
                        getString(R.string.close)
                    )

                }
            }
    }

    private fun showTitle(title: String, cancel: String) {
        tv_soft_ap_title.text = title
        tv_soft_ap_cancel.text = cancel
    }

    private fun showFragment(showFragment: BaseFragment, hideFragment: BaseFragment) {
        if (showFragment.isAdded) {
            this.supportFragmentManager.beginTransaction()
                .show(showFragment)
                .hide(hideFragment)
                .commit()
        } else {
            this.supportFragmentManager.beginTransaction()
                .add(R.id.container_soft_ap, showFragment)
                .show(showFragment)
                .hide(hideFragment)
                .commit()
        }
    }

    private fun showPopup() {
        if (closePopup == null) {
            closePopup = CommonPopupWindow(this)
            closePopup?.setCommonParams(
                getString(R.string.exit_toast_title),
                getString(R.string.exit_toast_content)
            )
            closePopup?.setMenuText(getString(R.string.cancel), getString(R.string.confirm))
            closePopup?.setBg(soft_ap_bg)
            closePopup?.onKeyListener = object : CommonPopupWindow.OnKeyListener {
                override fun confirm(popupWindow: CommonPopupWindow) {
                    finish()
                }

                override fun cancel(popupWindow: CommonPopupWindow) {
                    popupWindow.dismiss()
                }
            }
        }
        closePopup?.show(soft_ap)
    }

    override fun onBackPressed() {
        if (connectProgressFragment.isVisible) {
            showPopup()
        } else {
            super.onBackPressed()
        }
    }

    override fun getPresenter(): IPresenter? {
        return presenter
    }

    override fun onDestroy() {
        closePopup?.dismiss()
        super.onDestroy()
    }

    override fun onSuccess(token: String) {
        showFragment(softHotspotFragment, wifiFragment)
        showTitle(
            getString(R.string.soft_ap),
            getString(R.string.close)
        )
        L.e("getToken onSuccess token:" + token)
    }

    override fun onFail(msg: String) {
        L.e("getToken onFail msg:" + msg)
    }

}