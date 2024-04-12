package com.connectsdk.sampler.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.Button
import android.widget.ListView

import com.connectsdk.core.AppInfo
import com.connectsdk.sampler.R
import com.connectsdk.sampler.util.TestResponseObject
import com.connectsdk.sampler.widget.AppAdapter
import com.connectsdk.service.capability.Launcher
import com.connectsdk.service.capability.Launcher.AppInfoListener
import com.connectsdk.service.capability.Launcher.AppLaunchListener
import com.connectsdk.service.command.ServiceCommandError
import com.connectsdk.service.command.ServiceSubscription
import com.connectsdk.service.sessions.LaunchSession

class CustomFragment(context: Context?) : BaseFragment(context) {
    var watchaButton: Button? = null
    var wavveButton: Button? = null
    var coupangButton: Button? = null
    var netflixButton: Button? = null
    var appleTVButton: Button? = null
    var youtubeButton: Button? = null
    var tvingButton: Button? = null
    var disneyButton: Button? = null
    var amazonButton: Button? = null

    var appListView: ListView? = null
    var adapter: AppAdapter? = null
    var runningAppSession: LaunchSession? = null
    var testResponse: TestResponseObject? = null

    var runningAppSubs: ServiceSubscription<AppInfoListener>? = null

    init {
        testResponse = TestResponseObject()
    }

    fun buttonAction(button: Button, appId: String) {
        var capability: String = ""
        var params: String = ""
        if (appId == "netflix") {
            capability = Launcher.Netflix
            params = Launcher.Netflix_Params
        } else if (appId == "youtube") {
            capability = Launcher.YouTube
            params = Launcher.YouTube_Params
        } else {
            capability = Launcher.Application
            params = Launcher.Application_Params
        }
        
        if (tv.hasCapability(capability)
            || tv.hasCapability(params)
        ) {
            button.setOnClickListener {
                if (button.isSelected) {
                    button.isSelected = false
                    if (runningAppSession != null) {
                        runningAppSession!!.close(null)
                    }
                } else {
                    button.isSelected = true
                    if (capability == Launcher.Netflix) {
                        launcher.launchNetflix(
                            "http://connectsdk.com/",
                            object : AppLaunchListener {
                                override fun onSuccess(session: LaunchSession) {
                                    setRunningAppInfo(session)
                                    testResponse = TestResponseObject(
                                        true,
                                        TestResponseObject.SuccessCode,
                                        TestResponseObject.Launched_Browser
                                    )
                                }

                                override fun onError(error: ServiceCommandError) {}
                            })
                    } else if (capability == Launcher.YouTube) {
                        launcher.launchYouTube(
                            "http://connectsdk.com/",
                            object : AppLaunchListener {
                                override fun onSuccess(session: LaunchSession) {
                                    setRunningAppInfo(session)
                                    testResponse = TestResponseObject(
                                        true,
                                        TestResponseObject.SuccessCode,
                                        TestResponseObject.Launched_Browser
                                    )
                                }

                                override fun onError(error: ServiceCommandError) {}
                            })
                    } else {
                        launcher.launchAppWithInfo(
                            AppInfo(appId),
                            null,
                            object : AppLaunchListener {
                                override fun onSuccess(session: LaunchSession) {
                                    setRunningAppInfo(session)
                                    testResponse = TestResponseObject(
                                        true,
                                        TestResponseObject.SuccessCode,
                                        "Launched $appId"
                                    )
                                }

                                override fun onError(error: ServiceCommandError) {}
                            })
                    }
                }
            }
        } else {
            disableButton(button)
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(
            R.layout.fragment_custom, container, false
        )
        watchaButton = rootView.findViewById<View>(R.id.watchaButton) as Button
        wavveButton = rootView.findViewById<View>(R.id.wavveButton) as Button
        coupangButton = rootView.findViewById<View>(R.id.coupangButton) as Button
        netflixButton = rootView.findViewById<View>(R.id.netflixButton) as Button
        appleTVButton = rootView.findViewById<View>(R.id.appleTVButton) as Button
        youtubeButton = rootView.findViewById<View>(R.id.youtubeButton) as Button
        tvingButton = rootView.findViewById<View>(R.id.tvingButton) as Button
        disneyButton = rootView.findViewById<View>(R.id.disneyButton) as Button
        amazonButton = rootView.findViewById<View>(R.id.amazonButton) as Button

        appListView = rootView.findViewById<View>(R.id.appListView) as ListView
        adapter = AppAdapter(context, R.layout.app_item)
        appListView!!.adapter = adapter
        buttons = arrayOf(
            watchaButton,
            wavveButton,
            coupangButton,
            netflixButton,
            appleTVButton,
            youtubeButton,
            tvingButton,
            disneyButton,
            amazonButton
        )
        return rootView
    }

    override fun enableButtons() {
        super.enableButtons()
        buttonAction(watchaButton!!, "com.frograms.watchaplay.webos")
        buttonAction(wavveButton!!, "pooq")
        buttonAction(coupangButton!!, "coupangplay")
        buttonAction(netflixButton!!, "netflix")
        buttonAction(appleTVButton!!, "com.apple.appletv")
        buttonAction(youtubeButton!!, "youtube")
        buttonAction(tvingButton!!, "cj.eandm")
        buttonAction(disneyButton!!, "com.disney.disneyplus-prod")
        buttonAction(amazonButton!!, "amazon")

        if (tv.hasCapability(Launcher.RunningApp_Subscribe)) {
            runningAppSubs = launcher.subscribeRunningApp(object : AppInfoListener {
                override fun onSuccess(appInfo: AppInfo) {
                    adapter!!.setRunningAppId(appInfo.id)
                    adapter!!.notifyDataSetChanged()
                    val position = adapter!!.getPosition(appInfo)
                    appListView!!.setSelection(position)
                }

                override fun onError(error: ServiceCommandError) {}
            })
        }
        if (tv.hasCapability(Launcher.Application_List)) {
            launcher.getAppList(object : Launcher.AppListListener {
                override fun onSuccess(appList: MutableList<AppInfo>) {
                    adapter?.clear()
                    for (app in appList) {
                        adapter?.add(app)
                    }
                    adapter?.sort()
                }

                override fun onError(error: ServiceCommandError) {}
            })
        }
        appListView!!.onItemClickListener =
            OnItemClickListener { arg0, arg1, arg2, arg3 ->
                if (runningAppSession != null) {
                    runningAppSession!!.close(null)
                }
                val appInfo = arg0.getItemAtPosition(arg2) as AppInfo
                launcher.launchAppWithInfo(appInfo, null, object : AppLaunchListener {
                    override fun onSuccess(session: LaunchSession) {
                        setRunningAppInfo(session)
                    }

                    override fun onError(error: ServiceCommandError) {}
                })
            }
    }

    override fun disableButtons() {
        if (runningAppSubs != null) runningAppSubs!!.unsubscribe()
        adapter!!.clear()
        super.disableButtons()
    }

    fun setRunningAppInfo(session: LaunchSession?) {
        runningAppSession = session
    }

    protected fun getToastIconData(): String {
        return mContext.getString(R.string.toast_icon_data)
    }
}