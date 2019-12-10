package nedikov.utils

import glm_.f
import glm_.func.rad
import glm_.glm
import glm_.i
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2d
import glm_.vec2.Vec2i
import imgui.impl.LwjglGL3
import nedikov.camera.*
import nedikov.camera.Camera.Movement.*
import org.lwjgl.glfw.GLFW
import org.lwjgl.opengl.GL
import uno.glfw.GlfwWindow
import uno.glfw.glfw

class MyWindow(title: String, private val camera: Camera) {
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

        override fun dragStopped() {
            if (press?.key == MouseKey.Left) {
                window.cursor = GlfwWindow.Cursor.Normal
            }
        }

        override fun dragStarted() {
            if (press?.key == MouseKey.Left) {
                window.cursor = GlfwWindow.Cursor.Disabled
            }
        }
    }

    private val window: GlfwWindow = GlfwWindow(windowSize, title).apply {
        makeContextCurrent()
        show()
        framebufferSizeCallback = { size ->
            gln.glViewport(size)
            updateProjectionMatrix()
        }

        mouseButtonCallback = { button, action, mod -> controller.buttonAction(MouseEvent(button,action, mod))}
        cursorPosCallback = controller::mouseMove
        scrollCallback = { offset -> camera.processMouseScroll(offset.y.f) }

        cursor = GlfwWindow.Cursor.Normal

    }.also {
        GL.createCapabilities()
    }

    enum class Projection { Perspective, Orthographic }

    private var projection = Projection.Perspective
    val projectionMatrix = Mat4()

    private var deltaTime = 0f    // time between current frame and last frame
    private var lastFrame = 0f

    var fps = 0
    private var fps10 = 0
    private var frameCounter = 0

    init {
        updateProjectionMatrix()
        LwjglGL3.init(window, false)
    }

    val open: Boolean
        get() = window.open

    val aspect: Float
        get() = window.aspect

    val size: Vec2i
        get() = window.size

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

        fps10 += (1 / deltaTime).i
        if (++frameCounter == 10) {
            fps = fps10 / 10
            frameCounter = 0
            fps10 = 0
        }

        with(window) {
            if (pressed(GLFW.GLFW_KEY_ESCAPE)) close = true

            if (pressed(GLFW.GLFW_KEY_W)) camera.processKeyboard(Forward, deltaTime)
            if (pressed(GLFW.GLFW_KEY_S)) camera.processKeyboard(Backward, deltaTime)
            if (pressed(GLFW.GLFW_KEY_A)) camera.processKeyboard(Left, deltaTime)
            if (pressed(GLFW.GLFW_KEY_D)) camera.processKeyboard(Right, deltaTime)

            if (pressed(GLFW.GLFW_KEY_Z)) {
                camera.zoom(0.3f)
                updateProjectionMatrix()
            }
            if (pressed(GLFW.GLFW_KEY_X)) {
                camera.zoom(-0.3f)
                updateProjectionMatrix()
            }

            if (pressed(GLFW.GLFW_KEY_P)) setProjection(Projection.Perspective)
            if (pressed(GLFW.GLFW_KEY_O)) setProjection(Projection.Orthographic)
        }
    }

    private fun setProjection(projection: Projection) {
        if (projection != this.projection) {
            this.projection = projection
            updateProjectionMatrix()
        }
    }

    private fun updateProjectionMatrix() {
        when (projection) {
            Projection.Perspective ->
                glm.perspective(projectionMatrix, camera.zoom.rad, aspect, 0.1f, 100f)
            Projection.Orthographic -> {
                val x = camera.zoom / 15f * aspect
                val y = camera.zoom / 15f
                glm.ortho(projectionMatrix, -x, x, -y, y, 0.1f, 100f)
            }
        }
    }
}