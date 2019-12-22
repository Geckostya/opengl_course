package nedikov.program

import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import gln.glClearColor
import imgui.*
import imgui.impl.LwjglGL3
import nedikov.camera.OrbitCamera
import nedikov.program.ShaderLibrary.unlit
import nedikov.program.ShaderLibrary.phong
import nedikov.program.ShaderLibrary.simpleDepth
import nedikov.utils.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.*
import uno.glsl.glDeletePrograms
import java.nio.IntBuffer
import org.lwjgl.opengl.GL11.glBindTexture
import org.lwjgl.opengl.GL11.glClear
import org.lwjgl.opengl.GL11.glViewport
import org.lwjgl.opengl.GL13.*
import uno.glfw.glfw
import kotlin.math.cos
import kotlin.math.sin


fun main() {
    with(BasicLightingDiffuse()) {
        run()
        end()
    }
}

private class BasicLightingDiffuse {

    val camera = OrbitCamera()

    val window = MyWindow("Phong shader", camera)

    val dirLight = DirectionalLight(Vec3(1f), Vec3(1f, 1f, 0.7f))

    val floor = Mesh(verticesCube, indicesCube, Vec3(0.1f, 0.8f, 0f))
    val sun = Mesh(verticesCube, indicesCube, Vec3(1f, 1f, 0), isUnlit = true)

    val meshes = mutableListOf<Mesh>()

    val projectionViewMatrix = Mat4()

    val f = 8f
    val lightProjectionViewMatrix = Mat4()

    val depthMapFBO: Int
    val depthMap: Int

    val sceneAABB = AABB(Vec3(Float.MAX_VALUE), Vec3(-Float.MAX_VALUE))

    fun loadMeshes() {
        meshes.add(floor)
        floor.model.translate_(0f, 0f, -0.1f).scale_(10f, 10f, 0.2f)
        meshes.addAll(loadModel("meshes/grass.obj"))
        meshes.addAll(loadModel("meshes/tree.obj").also {
            meshes -> meshes.forEach { it.model.translate_(3f, 2f, 0f) }
        })
        meshes.addAll(loadModel("meshes/tree2.obj").also {
                meshes -> meshes.forEach { it.model.translate_(0f, -1.5f, 0f) }
        })
        meshes.addAll(loadModel("meshes/tree3.obj").also {
            meshes -> meshes.forEach { it.model.translate_(-3f, 1f, 0f) }
        })
        meshes.add(sun)
    }

    init {
        glEnable(GL_DEPTH_TEST)

        loadMeshes()

        meshes.forEach {
            it.init()
            if (it !== sun) sceneAABB.put(it.model, it.vertices, 8)
        }

        depthMapFBO = glGenFramebuffers()

        depthMap = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, depthMap)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT,
            SHADOW_MAP_RES, SHADOW_MAP_RES, 0, GL_DEPTH_COMPONENT, GL_FLOAT, null as IntBuffer?
        )
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT)

        // attach depth texture as FBO's depth buffer
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthMap, 0)
        glDrawBuffer(GL_NONE)
        glReadBuffer(GL_NONE)
        glBindFramebuffer(GL_FRAMEBUFFER, 0)

        phong.bindTexture()
    }

    var showOverlay = true
    val flags = WindowFlags.NoTitleBar or WindowFlags.NoResize or
            WindowFlags.AlwaysAutoResize or WindowFlags.NoMove or
            WindowFlags.NoSavedSettings or WindowFlags.NoInputs or
            WindowFlags.NoFocusOnAppearing or WindowFlags.NoBringToFrontOnFocus

    fun run() {
        LwjglGL3.newFrame()
//        updateLigth()
        while (window.open) {

            window.processFrame()
            LwjglGL3.newFrameWithCursor()

            with(ImGui) {
                setNextWindowPos(Vec2(10))
                functionalProgramming.withWindow(
                    "Controls",
                    ::showOverlay,
                    flags
                ) {
                    text("fps: ${window.fps}")
                    text("Moving: WASD or LMB drag")
                    text("Projection: O/P")
                    text("Zoom: Z/X")
                }
            }


            glEnable(GL_CULL_FACE)
            glCullFace(GL_BACK)

            updateLigth()
            renderShadowPass()
            renderFinalPass()

            ImGui.render()
            window.swapAndPoll()
        }
    }

    fun renderShadowPass() {
        glViewport(0, 0, SHADOW_MAP_RES, SHADOW_MAP_RES)
        glBindFramebuffer(GL_FRAMEBUFFER, depthMapFBO)
        glClear(GL_DEPTH_BUFFER_BIT)

        glEnable(GL_POLYGON_OFFSET_FILL)

        glPolygonOffset(1f, 1f)

        simpleDepth.use(lightProjectionViewMatrix)

        meshes.forEach { it.drawShadows() }

        glDisable(GL_POLYGON_OFFSET_FILL)

        glBindFramebuffer(GL_FRAMEBUFFER, 0)
    }

    fun renderFinalPass() {
        Mat4.times(projectionViewMatrix, window.projectionMatrix, camera.viewMatrix)

        gln.glViewport(window.size)
        glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)
        glClearColor(clearColor0)

        phong.use(camera.position, projectionViewMatrix, dirLight, lightProjectionViewMatrix)

        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, depthMap)

        meshes.forEach {
            if (it.isUnlit) unlit.use(projectionViewMatrix) // unlit at the end of the list
            it.draw()
        }
    }

    private fun updateLigth() {
        val angle = glfw.time / 12
        dirLight.direction.x = cos(angle)
        dirLight.direction.y = sin(angle)
        dirLight.direction.z = -0.65f
        dirLight.update()

        val b = sceneAABB.bounds(dirLight.viewMatrix)
        glm.ortho(lightProjectionViewMatrix, b.x, b.y, b.z, b.w, 0f, 100f).times_(dirLight.viewMatrix)

        sun.model.put(1f).translate_(dirLight.direction * -15).rotate_(angle, worldUp)
    }

    fun end() {
        LwjglGL3.shutdown()

        glDeletePrograms(phong, simpleDepth, unlit)

        meshes.forEach { it.dispose() }

        window.end()
    }

    companion object {
        const val SHADOW_MAP_RES = 2048
    }
}