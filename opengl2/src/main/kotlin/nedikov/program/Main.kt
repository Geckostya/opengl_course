package nedikov.program

import glm_.func.rad
import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import gln.draw.glDrawElements
import gln.get
import gln.glClearColor
import gln.glf.glf
import gln.glf.semantic
import gln.program.usingProgram
import gln.texture.glBindTexture
import gln.uniform.glUniform
import gln.uniform.glUniform3f
import gln.vertexArray.glEnableVertexAttribArray
import gln.vertexArray.glVertexAttribPointer
import nedikov.camera.OrbitCamera
import nedikov.program.ShaderLibrary.lamp
import nedikov.program.ShaderLibrary.phong
import nedikov.utils.*
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL12
import org.lwjgl.opengl.GL13.*
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL20
import org.lwjgl.opengl.GL20.glGetUniformLocation
import org.lwjgl.opengl.GL30.*
import uno.buffer.destroyBuf
import uno.buffer.intBufferBig
import uno.buffer.use
import uno.glfw.glfw
import uno.glsl.glDeletePrograms
import uno.glsl.glUseProgram

fun main() {
    with(BasicLightingDiffuse()) {
        run()
        end()
    }
}

val camera = OrbitCamera();

private class BasicLightingDiffuse {

    val window = MyWindow("Phong shader", camera)

    val dirLight = DirectionalLight(Vec3(-2f, -1.5f, -1f).normalizeAssign(), Vec3(1f))

    val cube = Mesh(verticesCube, indicesCube, dirLight)

    init {
        cube.color.put(1f, 0.5f, 0.31f)
        glEnable(GL_DEPTH_TEST)
        cube.init()
    }

    fun run() {
        while (window.open) {

            val projection = glm.perspective(camera.zoom.rad, window.aspect, 0.1f, 100f)

            window.processFrame()

            glEnable(GL_CULL_FACE)
            glCullFace(GL_BACK)

            // render
            glClearColor(clearColor0)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            cube.draw(camera, projection)
            window.swapAndPoll()
        }
    }

    fun end() {
        glDeletePrograms(phong, lamp)

        cube.dispose()
        window.end()
    }
}