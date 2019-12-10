package nedikov.program

import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import gln.glf.semantic
import gln.program.usingProgram
import gln.uniform.glUniform
import gln.uniform.glUniform3f
import org.lwjgl.opengl.GL20
import uno.glsl.Program
import uno.glsl.glUseProgram

object ShaderLibrary {
    val phong = Phong()
    val unlit = Unlit()
    val simpleDepth = SimpleDepth()

    class Phong : Shader("phong") {
        val lgtCol  = GL20.glGetUniformLocation(name, "u_lightColor")
        val lgtDir  = GL20.glGetUniformLocation(name, "u_lightDir")
        val lgtSpace  = GL20.glGetUniformLocation(name, "u_lightSpaceMatrix")
        val viewPos = GL20.glGetUniformLocation(name, "u_viewPos")
        val objCol  = GL20.glGetUniformLocation(name, "u_objectColor")

        fun bindTexture() {
            glUseProgram(this)
            GL20.glUniform1i(GL20.glGetUniformLocation(name, "u_shadowMap"), 0)
        }

        fun use(cameraPos: Vec3, projectionView: Mat4, dirLight: DirectionalLight, lightSpace: Mat4) {
            useProgram(projectionView)

            glUniform3f(lgtCol, dirLight.color)
            glUniform3f(lgtDir, dirLight.direction)
            glUniform3f(viewPos, cameraPos)
            glUniform(lgtSpace, lightSpace)
        }
    }

    class Unlit : Shader("unlit") {
        val objCol  = GL20.glGetUniformLocation(name, "u_objectColor")

        fun use(projectionView: Mat4) {
            useProgram(projectionView)
        }
    }

    class SimpleDepth : Shader("simpleDepth") {
        fun use(projectionView: Mat4) {
            useProgram(projectionView)
        }
    }

    open class Shader(shader: String, root: String = "shaders") : Program(root, "$shader.vert", "$shader.frag") {
        val model = GL20.glGetUniformLocation(name, "u_model")
        val projView = GL20.glGetUniformLocation(name, "u_projectionView")

        protected fun useProgram(projectionView: Mat4) {
            glUseProgram(this)

            glUniform(projView, projectionView)
        }
    }
}