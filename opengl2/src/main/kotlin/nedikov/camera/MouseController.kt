package nedikov.camera

import glm_.vec2.Vec2d
import org.lwjgl.glfw.GLFW
import uno.glfw.glfw

open class MouseController {
    var press: MouseEvent? = null
    var pressTime = Float.POSITIVE_INFINITY
    var hasDrag = false

    private var firstMove = true
    private var lastPos: Vec2d = Vec2d()
    private var lastOffset: Vec2d = Vec2d()

    fun buttonAction(event: MouseEvent) {
        if (hasDrag) {
            if (event.pressed) {
                return
            }
            if (event.key == press?.key && !event.pressed) {
                hasDrag = false
                dragStopped()
            }
        }
        if (press == null && event.pressed) {
            press = event
            pressTime = glfw.time
        }
        if (press != null && !event.pressed && event.key == press?.key) {
            press = null
        }
    }

    open fun dragStopped() {
    }

    open fun dragStarted() {
    }

    fun mouseMove(pos: Vec2d) {
        if (firstMove) {
            lastPos.put(pos)
            firstMove = false
        }

        if (press != null && !hasDrag && (glfw.time - pressTime) >= dragDelay) {
            hasDrag = true
            dragStarted()
        }

        lastOffset.put(pos.x - lastPos.x, lastPos.y - pos.y)
        lastPos.put(pos)
        processOffset(lastOffset)
    }

    open fun processOffset(offset: Vec2d) {
    }

    companion object {
        const val dragDelay = 0.1f
    }
}

class MouseEvent(val key: MouseKey, val mod: MouseMod, val pressed: Boolean) {
    constructor(key: Int, action: Int, mod: Int) : this(MouseKey.create(key), MouseMod(mod), action == 1)
}

enum class MouseKey {
    Left, Right, Middle;

    companion object {
        fun create(num: Int) : MouseKey {
            return when (num) {
                0 -> Left
                1 -> Right
                else -> Middle
            }
        }
    }
}

class MouseMod(mask: Int) {
    val shift   = (mask and GLFW.GLFW_MOD_SHIFT)   == 1
    val control = (mask and GLFW.GLFW_MOD_CONTROL) == 1
    val alt     = (mask and GLFW.GLFW_MOD_ALT)     == 1
    val sup     = (mask and GLFW.GLFW_MOD_SUPER)   == 1
}