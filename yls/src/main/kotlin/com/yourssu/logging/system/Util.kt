package com.yourssu.logging.system

fun YLS.Facade.logEvent(
    eventName: String,
    vararg extra: Pair<String, Any>,
) {
    YLS.log("event" to eventName, *extra)
}

fun YLS.Facade.logAppInit(vararg extra: Pair<String, Any>) {
    logEvent("AppInitialEntry", *extra)
}

fun YLS.Facade.logDeepLinkEntry(
    screenName: String,
    vararg extra: Pair<String, Any>,
) {
    logEvent("DeepLinkEntry", "screen" to screenName, *extra)
}

fun YLS.Facade.logScreenEntry(
    screenName: String,
    vararg extra: Pair<String, Any>,
) {
    logEvent("ScreenEntry", "screen" to screenName, *extra)
}

fun YLS.Facade.logScreenExit(
    screenName: String,
    vararg extra: Pair<String, Any>,
) {
    logEvent("ScreenExit", "screen" to screenName, *extra)
    flush()
}

/**
 * @param componentName 클릭한 요소의 이름
 * @param screenName 클릭한 시점의 화면의 이름
 */
fun YLS.Facade.logClick(
    componentName: String,
    screenName: String,
    vararg extra: Pair<String, Any>,
) {
    logEvent("${componentName}Clicked", "screen" to screenName, *extra)
}
