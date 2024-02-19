package com.yourssu.logging.system

import com.yourssu.logging.system.HashUtil._hashedId
import com.yourssu.logging.system.HashUtil._id
import java.security.MessageDigest

/**
 * @property _id for caching
 * @property _hashedId for caching
 */
object HashUtil {
    private var _id: String? = null
    private var _hashedId: String? = null

    fun hashId(id: String): String {
        if (_id == id && _hashedId != null) {
            return _hashedId!!
        }

        _hashedId = hashString(id)
        _id = id
        return _hashedId!!
    }

    internal fun hashString(origin: String, algorithm: String = "SHA-256"): String {
        return MessageDigest.getInstance(algorithm)
            .digest(origin.toByteArray())
            .let { bytes ->
                bytes.fold(StringBuilder(bytes.size * 2)) { str, it ->
                    str.append("%02x".format(it))
                }
            }.toString()
    }
}
