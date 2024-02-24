package com.yourssu.logging.system

fun logEvent(eventName: String, vararg extra: Pair<String, Any>) {
    YLS.log("event" to eventName, *extra)
}

fun logAppInitEvent(vararg extra: Pair<String, Any>) {
    logEvent("AppInitialEntry", *extra)
}

fun logDeepLinkEntry(screenName: String, vararg extra: Pair<String, Any>) {
    logEvent("DeepLinkEntry", "screen" to screenName, *extra)
}

fun logScreenEntry(screenName: String, vararg extra: Pair<String, Any>) {
    logEvent("ScreenEntry", "screen" to screenName, *extra)
}

fun logScreenExit(screenName: String, vararg extra: Pair<String, Any>) {
    logEvent("ScreenExit", "screen" to screenName, *extra)
}

/**
 * @param componentName 클릭한 요소의 이름
 * @param screenName 클릭한 시점의 화면의 이름
 */
fun logClick(componentName: String, screenName: String, vararg extra: Pair<String, Any>) {
    logEvent("${componentName}Clicked", "screen" to screenName, *extra)
}
