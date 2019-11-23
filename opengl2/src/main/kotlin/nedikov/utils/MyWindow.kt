package nedikov.utils

import glm_.f
import nedikov.program.camera
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import uno.glfw.GlfwWindow
import uno.glfw.glfw

class MyWindow(title: String, private val cursorController: CursorController) {
    private val window: GlfwWindow = initWindow(title, cursorController)

    var deltaTime = 0f    // time between current frame and last frame
    var lastFrame = 0f

    val open: Boolean
        get() = window.open

    val aspect: Float
        get() = window.aspect

    private fun initWindow(title: String, cursorController: CursorController): GlfwWindow {
        with(glfw) {
            init()
            windowHint {
                context.version = "3.3"
                profile = "core"
            }
        }

        return GlfwWindow(windowSize, title).apply {
            makeContextCurrent()
            show()
            framebufferSizeCallback = { size -> gln.glViewport(size) }

            cursorPosCallback = cursorController::mouseCallback
            scrollCallback = { offset -> cursorController.camera.processMouseScroll(offset.y.f) }

            cursor = GlfwWindow.Cursor.Disabled

        }.also {
            GL.createCapabilities()
        }
    }


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

            if (pressed(GLFW.GLFW_KEY_W)) camera.processKeyboard(Camera.Movement.Forward, deltaTime)
            if (pressed(GLFW.GLFW_KEY_S)) camera.processKeyboard(Camera.Movement.Backward, deltaTime)
            if (pressed(GLFW.GLFW_KEY_A)) camera.processKeyboard(Camera.Movement.Left, deltaTime)
            if (pressed(GLFW.GLFW_KEY_D)) camera.processKeyboard(Camera.Movement.Right, deltaTime)
        }
    }

}