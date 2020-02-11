package at.fh.swengb.ambrosch

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
class AuthResponse(val token: String) {
}