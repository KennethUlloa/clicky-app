package com.lunnaris.clicky

class Global {
    companion object {
        var dragSensibility: Float = 1f
        var scrollSensibility: Float = 1f
        var inverseScroll: Boolean = false
        var serverAddress: String = ""
        var deviceName: String? = null
        var allowQR: Boolean = true
        var showClickButtons: Boolean = true
    }
}

enum class Click {
    RIGHT, LEFT
}