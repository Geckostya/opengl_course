package nedikov.program

import org.lwjgl.opengl.GL20
import uno.glsl.Program

open class Model(root: String, shader: String) : Program(root, "$shader.vert", "$shader.frag") {
    val model = GL20.glGetUniformLocation(name, "model")
    val view = GL20.glGetUniformLocation(name, "view")
    val proj = GL20.glGetUniformLocation(name, "projection")
}