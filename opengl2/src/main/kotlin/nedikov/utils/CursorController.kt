package nedikov.utils

import glm_.vec2.Vec2d

class CursorController(val camera: Camera) {
    // camera
    val last = Vec2d(windowSize) / 2

    var firstMouse = true

    fun mouseCallback(pos: Vec2d) {

        if (firstMouse) {
            last put pos
            firstMouse = false
        }

        val offset = Vec2d(pos.x - last.x, last.y - pos.y)
        last put pos

        offset *= 0.1

        camera processMouseMovement offset
    }
}