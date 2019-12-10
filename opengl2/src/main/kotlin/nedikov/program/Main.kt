package nedikov.program

import glm_.vec2.Vec2
import glm_.vec3.Vec3
import gln.glClearColor
import imgui.ImGui
import imgui.WindowFlags
import imgui.functionalProgramming
import imgui.impl.LwjglGL3
import imgui.or
import nedikov.camera.OrbitCamera
import nedikov.program.ShaderLibrary.lamp
import nedikov.program.ShaderLibrary.phong
import nedikov.utils.*
import org.lwjgl.opengl.GL11.*
import uno.glsl.glDeletePrograms

fun main() {
    with(BasicLightingDiffuse()) {
        run()
        end()
    }
}


private class BasicLightingDiffuse {

    val camera = OrbitCamera();

    val window = MyWindow("Phong shader", camera)

    val dirLight = DirectionalLight(Vec3(-2f, -1.5f, -1f).normalizeAssign(), Vec3(1f))

    val cube = Mesh(verticesCube, indicesCube, dirLight)
    val floor = Mesh(verticesCube, indicesCube, dirLight)

    val meshes = arrayOf(cube, floor)

    init {
        glEnable(GL_DEPTH_TEST)
        cube.color.put(1f, 0.5f, 0.31f)
        cube.model.translate_(0f, 0f, 0.5f)

        floor.color.put(0.4f, 0.4f, 0.4f)
        floor.model.translate_(0f, 0f, -0.1f).scale_(10f, 10f, 0.2f)

        meshes.forEach { it.init() }
    }

    var showOverlay = true
    val flags = WindowFlags.NoTitleBar or WindowFlags.NoResize or
            WindowFlags.AlwaysAutoResize or WindowFlags.NoMove or
            WindowFlags.NoSavedSettings or WindowFlags.NoInputs or
            WindowFlags.NoFocusOnAppearing or WindowFlags.NoBringToFrontOnFocus

    fun run() {
        while (window.open) {

            window.processFrame()
            LwjglGL3.newFrame()
            with(ImGui) {
                setNextWindowPos(Vec2(10))
                functionalProgramming.withWindow(
                    "Controls",
                    ::showOverlay,
                    flags
                ) {
                    text("Moving: WASD or LMC drag")
                    text("Projection: O/P")
                    text("Zoom: Z/X")
                }
            }

            glEnable(GL_CULL_FACE)
            glCullFace(GL_BACK)

            // render
            glClearColor(clearColor0)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            meshes.forEach { it.draw(camera, window.projectionMatrix) }

            ImGui.render()
            window.swapAndPoll()
        }
    }

    fun end() {
        LwjglGL3.shutdown()

        glDeletePrograms(phong, lamp)

        meshes.forEach { it.dispose() }
        window.end()
    }
}