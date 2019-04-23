package ffc.airsync

import ffc.airsync.api.otp.OtpApi
import ffc.airsync.api.otp.RetofitOtpApi

val otpApi: OtpApi by lazy { RetofitOtpApi() }
