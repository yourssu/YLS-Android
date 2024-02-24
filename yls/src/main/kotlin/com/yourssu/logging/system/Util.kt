package com.yourssu.logging.system

fun logEvent(
    eventName: String,
    version: Int = YLS.VERSION,
    vararg extra: Pair<String, Any>,
) {
    YLS.log(version, "event" to eventName, *extra)
}

fun logAppInitEvent(
    version: Int = YLS.VERSION,
    vararg extra: Pair<String, Any>,
) {
    logEvent("AppInitialEntry", version, *extra)
}

fun logDeepLinkEntry(
    screenName: String,
    version: Int = YLS.VERSION,
    vararg extra: Pair<String, Any>,
) {
    logEvent("DeepLinkEntry", version, "screen" to screenName, *extra)
}

fun logScreenEntry(
    screenName: String,
    version: Int = YLS.VERSION,
    vararg extra: Pair<String, Any>,
) {
    logEvent("ScreenEntry", version, "screen" to screenName, *extra)
}

fun logScreenExit(
    screenName: String,
    version: Int = YLS.VERSION,
    vararg extra: Pair<String, Any>,
) {
    logEvent("ScreenExit", version, "screen" to screenName, *extra)
}

/**
 * @param componentName 클릭한 요소의 이름
 * @param screenName 클릭한 시점의 화면의 이름
 */
fun logClick(
    componentName: String,
    screenName: String,
    version: Int = YLS.VERSION,
    vararg extra: Pair<String, Any>,
) {
    logEvent("${componentName}Clicked", version, "screen" to screenName, *extra)
}
