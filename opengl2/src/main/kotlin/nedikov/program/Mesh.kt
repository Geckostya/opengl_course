package nedikov.program

import gli_.buffer.intBufferBig
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import gln.draw.glDrawElements
import gln.get
import gln.glf.glf
import gln.uniform.glUniform
import gln.uniform.glUniform3f
import gln.vertexArray.glEnableVertexAttribArray
import gln.vertexArray.glVertexAttribPointer
import nedikov.camera.Camera
import nedikov.program.ShaderLibrary.phong
import org.lwjgl.opengl.GL11.GL_TRIANGLES
import org.lwjgl.opengl.GL11.GL_UNSIGNED_INT
import org.lwjgl.opengl.GL15.*
import org.lwjgl.opengl.GL30.*
import uno.buffer.destroyBuf
import uno.glsl.glUseProgram

class Mesh(private val vertices: FloatArray, private val indices: IntArray,
           private val dirLight: DirectionalLight) {

    val color: Vec3 = Vec3()

    var model = Mat4()

    enum class Buffer { Vertex, Element }

    private var vertexArrayObject: Int = -1

    private val buffers = intBufferBig<Buffer>()

    fun init() {
        vertexArrayObject = glGenVertexArrays()

        // first, configure the cube's VAO (and VBO)
        glGenBuffers(buffers)

        glBindVertexArray(vertexArrayObject)

        glBindBuffer(GL_ARRAY_BUFFER, buffers[Buffer.Vertex])
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers[Buffer.Element])
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)

        glVertexAttribPointer(glf.pos3_nor3_tc2)
        glEnableVertexAttribArray(glf.pos3_nor3_tc2)
    }

    fun draw(camera: Camera, projection: Mat4) {
        glUseProgram(phong)

        glUniform3f(phong.objCol, color)
        glUniform3f(phong.lgtCol, dirLight.color)
        glUniform3f(phong.lgtDir, dirLight.direction)
        glUniform3f(phong.viewPos, camera.position)

        glUniform(phong.proj, projection)
        glUniform(phong.view, camera.viewMatrix)
        glUniform(phong.model, model)

        glBindVertexArray(vertexArrayObject)
        glDrawElements(GL_TRIANGLES, indices.size, GL_UNSIGNED_INT)
    }

    fun dispose() {
        glDeleteVertexArrays(vertexArrayObject)
        glDeleteBuffers(buffers)
        destroyBuf(buffers)
    }
}