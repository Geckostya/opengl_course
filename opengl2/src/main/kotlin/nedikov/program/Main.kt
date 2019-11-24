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


// camera
val camera = OrbitCamera();//position = Vec3(0f, 0f, 3f))

private class BasicLightingDiffuse {

    val window = MyWindow("Phong shader", camera)

    val phong = Phong()
    val lamp = Lamp()

    val texture = intBufferBig(1)

    enum class VA { Cube, Light }
    enum class Buffer { Vertex, Element }


    val buffers = intBufferBig<Buffer>()
    val vao = intBufferBig<VA>()

    // lighting
    val lightPos = Vec3(1.2f, 1f, 2f)

    init {

        glEnable(GL_DEPTH_TEST)

        glGenVertexArrays(vao)

        // first, configure the cube's VAO (and VBO)
        glGenBuffers(buffers)

        glBindVertexArray(vao[VA.Cube])

        glBindBuffer(GL_ARRAY_BUFFER, buffers[Buffer.Vertex])
        glBufferData(GL_ARRAY_BUFFER, verticesCube, GL_STATIC_DRAW)

        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers[Buffer.Element])
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesCube, GL_STATIC_DRAW)

        glVertexAttribPointer(glf.pos3_nor3_tc2)
        glEnableVertexAttribArray(glf.pos3_nor3_tc2)


        // second, configure the light's VAO (VBO stays the same; the vertices are the same for the light object which is also a 3D cube)
        glBindVertexArray(vao[VA.Light])

        glBindBuffer(GL_ARRAY_BUFFER, buffers[Buffer.Vertex])
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers[Buffer.Element])

        // note that we update the lamp's position attribute's stride to reflect the updated buffer data
        glVertexAttribPointer(glf.pos3_nor3_tc2[0])
        glEnableVertexAttribArray(glf.pos3_nor3_tc2[0])
        glVertexAttribPointer(glf.pos3_nor3_tc2[2])
        glEnableVertexAttribArray(glf.pos3_nor3_tc2[2])



        // load and create a texture
        glGenTextures(texture)
        //  all upcoming GL_TEXTURE_2D operations now have effect on this texture object
        glBindTexture(GL_TEXTURE_2D, texture)

        val image = readImage(
            "textures/perlin_noise.png"
//            "textures/perlin_noise2.png"
//            "textures/worley-noise.jpg"
        )
        image.toBuffer().use {
            // ByteBuffered images used BRG instead RGB
            gln.texture.glTexImage2D(GL_RGB, image.width, image.height, GL12.GL_BGR, GL_UNSIGNED_BYTE, it)
            glGenerateMipmap(GL_TEXTURE_2D)
        } // byteBuffer automatically dispose with `use{ .. }`

    }

    inner class Phong : Shader("phong") {
        val objCol  = glGetUniformLocation(name, "u_objectColor")
        val lgtCol  = glGetUniformLocation(name, "u_lightColor")
        val lgtPos  = glGetUniformLocation(name, "u_lightPos")
        val viewPos = glGetUniformLocation(name, "u_viewPos")
        val time = glGetUniformLocation(name, "u_time")
        init {
            usingProgram(name) {
                GL20.glUniform1i(
                    glGetUniformLocation(name, "u_noise"),
                    semantic.sampler.DIFFUSE
                )
            }
        }
    }

    inner class Lamp : Shader("lamp")

    fun run() {

        val startTime = glfw.time

        while (window.open) {

            window.processFrame()

            glEnable(GL_CULL_FACE)
            glCullFace(GL_BACK)

            // render
            glClearColor(clearColor0)
            glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

            // bind Texture
            glActiveTexture(GL_TEXTURE0 + semantic.sampler.DIFFUSE)
            glBindTexture(GL_TEXTURE_2D, texture)

            // be sure to activate shader when setting uniforms/drawing objects
            glUseProgram(phong)

            glUniform(phong.time, (glfw.time - startTime) * 0.3f)
            glUniform3f(phong.objCol, 1f, 0.5f, 0.31f)
            glUniform3f(phong.lgtCol, 1f)
            glUniform3f(phong.lgtPos, lightPos)
            glUniform3f(phong.viewPos, camera.position)

            // view/projection transformations
            val projection = glm.perspective(camera.zoom.rad, window.aspect, 0.1f, 100f)
            val view = camera.viewMatrix
            glUniform(phong.proj, projection)
            glUniform(phong.view, view)

            // world transformation
            var model = Mat4()
            glUniform(phong.model, model)

            // render the cube
            glBindVertexArray(vao[VA.Cube])
            glDrawElements(GL_TRIANGLES, indicesCube.size, GL_UNSIGNED_INT)

            // also draw the lamp object
            glUseProgram(lamp)

            glUniform(lamp.proj, projection)
            glUniform(lamp.view, view)
            model = model
                .translate(lightPos)
                .scale(0.2f) // a smaller cube
            glUniform(lamp.model, model)

            glBindVertexArray(vao[VA.Light])
            glDrawElements(GL_TRIANGLES, indicesCube.size / 2, GL_UNSIGNED_INT)

            window.swapAndPoll()
        }
    }

    fun end() {
        glDeletePrograms(phong, lamp)
        glDeleteVertexArrays(vao)
        glDeleteBuffers(buffers)
        glDeleteTextures(texture)

        destroyBuf(vao, buffers, texture)

        window.end()
    }
}