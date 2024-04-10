package com.connectsdk.sampler.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
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
import com.connectsdk.service.capability.ToastControl
import com.connectsdk.service.capability.listeners.ResponseListener
import com.connectsdk.service.command.ServiceCommandError
import com.connectsdk.service.command.ServiceSubscription
import com.connectsdk.service.sessions.LaunchSession

class CustomFragment(context: Context?) : BaseFragment(context) {
    var netflixButton: Button? = null
    var youtubeButton: Button? = null
    var browserButton: Button? = null
    
    var appListView: ListView? = null
    var adapter: AppAdapter? = null
    var runningAppSession: LaunchSession? = null
    var appStoreSession: LaunchSession? = null
    var myAppSession: LaunchSession? = null
    var testResponse: TestResponseObject? = null

    var runningAppSubs: ServiceSubscription<AppInfoListener>? = null

    init {
        testResponse = TestResponseObject()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(
            R.layout.fragment_custom, container, false
        )
        netflixButton = rootView.findViewById<View>(R.id.netflixButton) as Button
        youtubeButton = rootView.findViewById<View>(R.id.youtubeButton) as Button
        appListView = rootView.findViewById<View>(R.id.appListView) as ListView
        adapter = AppAdapter(context, R.layout.app_item)
        appListView!!.adapter = adapter
        buttons = arrayOf(

            netflixButton,

            youtubeButton
        )
        return rootView
    }

    fun check(){
         if (tv.hasCapability(Launcher.Browser)
            || tv.hasCapability(Launcher.Browser_Params)
        ) {
            browserButton!!.setOnClickListener {
                if (browserButton!!.isSelected) {
                    browserButton!!.isSelected = false
                    if (runningAppSession != null) {
                        runningAppSession!!.close(null)
                    }
                } else {
                    browserButton!!.isSelected = true
                    launcher.launchBrowser("http://connectsdk.com/", object : AppLaunchListener {
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
                }
            }
        } else {
            disableButton(browserButton)
        }
    }

    override fun enableButtons() {
        super.enableButtons()
        if (tv.hasCapability(Launcher.Browser)
            || tv.hasCapability(Launcher.Browser_Params)
        ) {
            browserButton!!.setOnClickListener {
                if (browserButton!!.isSelected) {
                    browserButton!!.isSelected = false
                    if (runningAppSession != null) {
                        runningAppSession!!.close(null)
                    }
                } else {
                    browserButton!!.isSelected = true
                    launcher.launchBrowser("http://connectsdk.com/", object : AppLaunchListener {
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
                }
            }
        } else {
            disableButton(browserButton)
        }
//        if (tv.hasCapability(ToastControl.Show_Toast)) {
//            toastButton!!.setOnClickListener(object : OnClickListener {
//                override fun onClick(v: View?) {
//                    toastControl.showToast("Yeah, toast!", getToastIconData(), "png", null)
//                    testResponse = TestResponseObject(
//                        true,
//                        TestResponseObject.SuccessCode,
//                        TestResponseObject.Show_Toast
//                    )
//                }
//            })
//        } else {
//            disableButton(toastButton)
//        }
        browserButton!!.isSelected = false
        if (tv.hasCapability(Launcher.Netflix)
            || tv.hasCapability(Launcher.Netflix_Params)
        ) {
            netflixButton!!.setOnClickListener {
                if (netflixButton!!.isSelected) {
                    netflixButton!!.isSelected = false
                    if (runningAppSession != null) {
                        runningAppSession!!.close(null)
                    }
                } else {
                    netflixButton!!.isSelected = true
                    launcher.launchNetflix("http://connectsdk.com/", object : AppLaunchListener {
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
                }
            }
        } else {
            disableButton(netflixButton)
        }
        if (tv.hasCapability(Launcher.YouTube)
            || tv.hasCapability(Launcher.YouTube_Params)
        ) {
            youtubeButton!!.setOnClickListener {
                if (youtubeButton!!.isSelected) {
                    youtubeButton!!.isSelected = false
                    if (runningAppSession != null) {
                        runningAppSession!!.close(null)
                    }
                } else {
                    youtubeButton!!.isSelected = true
                    launcher.launchYouTube("http://connectsdk.com/", object : AppLaunchListener {
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
                }
            }
        } else {
            disableButton(netflixButton)
        }
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
        if (tv.hasCapability(Launcher.Browser)) {
            if (tv.hasCapability(Launcher.Browser_Params)) {
                browserButton!!.text = "Open Google"
            } else {
                browserButton!!.text = "Open Browser"
            }
        }
//        myAppButton!!.isEnabled = tv.hasCapability("Launcher.Levak")
//        myAppButton!!.setOnClickListener(myAppLaunch)
//        appStoreButton!!.isEnabled = tv.hasCapability(Launcher.AppStore_Params)
//        appStoreButton!!.setOnClickListener(launchAppStore)
    }

//    var myAppLaunch = View.OnClickListener {
//        if (myAppSession != null) {
//            myAppSession!!.close(null)
//            myAppSession = null
//            myAppButton!!.isSelected = false
//        } else {
//            launcher.launchApp("Levak", object : AppLaunchListener {
//                override fun onError(error: ServiceCommandError) {
//                    Log.d("LG", "My app failed: $error")
//                }
//
//                override fun onSuccess(`object`: LaunchSession) {
//                    myAppSession = `object`
//                    myAppButton!!.isSelected = true
//                }
//            })
//        }
//    }

//    var launchAppStore = View.OnClickListener {
//        if (appStoreSession != null) {
//            appStoreSession!!.close(object : ResponseListener<Any?> {
//                override fun onError(error: ServiceCommandError) {
//                    Log.d("LG", "App Store close error: $error")
//                }
//
//                override fun onSuccess(`object`: Any?) {
//                    Log.d("LG", "AppStore close success")
//                }
//            })
//            appStoreSession = null
//            appStoreButton!!.isSelected = false
//        } else {
//            var appId: String? = null
//            if (tv.getServiceByName("Netcast TV") != null) appId =
//                "125071" else if (tv.getServiceByName("webOS TV") != null) appId =
//                "redbox" else if (tv.getServiceByName("Roku") != null) appId = "13535"
//            launcher.launchAppStore(appId, object : AppLaunchListener {
//                override fun onError(error: ServiceCommandError) {
//                    Log.d("LG", "App Store failed: $error")
//                }
//
//                override fun onSuccess(`object`: LaunchSession) {
//                    Log.d("LG", "App Store launched!")
//                    appStoreSession = `object`
//                    appStoreButton!!.isSelected = true
//                }
//            })
//        }
//    }

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