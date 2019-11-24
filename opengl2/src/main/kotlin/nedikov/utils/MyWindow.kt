package nedikov.utils

import glm_.f
import glm_.vec2.Vec2d
import nedikov.camera.FreeCamera
import nedikov.camera.FreeCamera.Movement.*
import nedikov.camera.MouseController
import nedikov.camera.MouseEvent
import nedikov.camera.MouseKey
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import uno.glfw.GlfwWindow
import uno.glfw.glfw

class MyWindow(title: String, private val camera: FreeCamera) {
    init {
        with(glfw) {
            init()
            windowHint {
                context.version = "3.3"
                profile = "core"
            }
        }
    }

    private val controller = object : MouseController() {
        override fun processOffset(offset: Vec2d) {
            if (hasDrag && press?.key == MouseKey.Left) {
                camera.processMouseMovement(offset)
            }
        }
    }

    private val window: GlfwWindow = GlfwWindow(windowSize, title).apply {
        makeContextCurrent()
        show()
        framebufferSizeCallback = { size -> gln.glViewport(size) }

        mouseButtonCallback = { button, action, mod -> controller.buttonAction(MouseEvent(button,action, mod))}
        cursorPosCallback = controller::mouseMove
        scrollCallback = { offset -> camera.processMouseScroll(offset.y.f) }

        cursor = GlfwWindow.Cursor.Disabled

    }.also {
        GL.createCapabilities()
    }

    var deltaTime = 0f    // time between current frame and last frame
    var lastFrame = 0f

    val open: Boolean
        get() = window.open

    val aspect: Float
        get() = window.aspect

    fun end() {
        window.destroy()
        glfw.terminate()
    }

    fun swapAndPoll() {
        window.swapBuffers()
        glfw.pollEvents()
    }

    fun processFrame() {
        val currentFrame = glfw.time
        deltaTime = currentFrame - lastFrame
        lastFrame = currentFrame

        with(window) {
            if (pressed(GLFW.GLFW_KEY_ESCAPE)) close = true

            if (pressed(GLFW.GLFW_KEY_W)) camera.processKeyboard(Forward, deltaTime)
            if (pressed(GLFW.GLFW_KEY_S)) camera.processKeyboard(Backward, deltaTime)
            if (pressed(GLFW.GLFW_KEY_A)) camera.processKeyboard(Left, deltaTime)
            if (pressed(GLFW.GLFW_KEY_D)) camera.processKeyboard(Right, deltaTime)
        }
    }

}