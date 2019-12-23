package ffc.airsync.utils

val FFC_HOME: String?
    get() {
        val property = System.getProperty("FFC_HOME")
        return if (property != null)
            return property
        else
            try {
                System.getenv("FFC_HOME")
            } catch (ignore: Exception) {
                null
            }
    }
