package com.yourssu.logging.system

/**
 * YLS에서 사용되는 기본 이벤트 데이터
 *
 * @param hashedID 암호화 된 ID. 이벤트 식별을 위해 필요
 * @param timestamp 이벤트 데이터가 생성된 시간. ISO 8601 포맷
 * @param event key-value 쌍의 상세한 이벤트 정보
 */
data class YLSEventData(
    val hashedID: String,
    val timestamp: String, // ISO 8601
    val version: Int,
    val event: Map<String, Any>,
)
