package nedikov.program

import glm_.vec3.Vec3
import gln.glClearColor
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

    fun run() {
        while (window.open) {
            window.processFrame()

            glEnable(GL_CULL_FACE)
            glCullFace(GL_BACK)

            // render
            glClearColor(clearColor0)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            meshes.forEach { it.draw(camera, window.projectionMatrix) }

            window.swapAndPoll()
        }
    }

    fun end() {
        glDeletePrograms(phong, lamp)

        meshes.forEach { it.dispose() }
        window.end()
    }
}