package nedikov.program

import org.lwjgl.opengl.GL20
import uno.glsl.Program

open class Shader(shader: String, root: String = "shaders") : Program(root, "$shader.vert", "$shader.frag") {
    val model = GL20.glGetUniformLocation(name, "u_model")
    val view = GL20.glGetUniformLocation(name, "u_view")
    val proj = GL20.glGetUniformLocation(name, "u_projection")
    val objCol  = GL20.glGetUniformLocation(name, "u_objectColor")
}