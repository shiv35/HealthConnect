//package com.example.health_connect.ChatSection
//
//import android.app.Application
//import im.zego.zegoexpress.ZegoExpressEngine
//import im.zego.zegoexpress.constants.ZegoScenario
//import im.zego.zegoexpress.callback.IZegoEventHandler
//
//class MyApp : Application() {
//    companion object {
//        const val ZEGO_APP_ID = 2051507613L // Replace with your Zego AppID
//        const val ZEGO_APP_SIGN = "a9c3e9c7aaa46cb24d35b41856f61994f5fbdc6f4c47e23348604d2188be8e3e" // Replace with your AppSign
//        lateinit var engine: ZegoExpressEngine
//    }
//
//    override fun onCreate() {
//        super.onCreate()
//
//        // Initialize Zego Express SDK
//        engine = ZegoExpressEngine.createEngine(
//            ZEGO_APP_ID,
//            ZEGO_APP_SIGN,
//            true, // Set to true for test environment, false for production
//            ZegoScenario.GENERAL,
//            applicationContext, // Use application context
//            object : IZegoEventHandler() {
//                // Implement required event handler methods if needed
//            }
//        )
//    }
//
//    override fun onTerminate() {
//        super.onTerminate()
//        // Release the Zego Express Engine when the application is terminated
//        ZegoExpressEngine.destroyEngine()
//    }
//}