package nedikov.program

import org.lwjgl.opengl.GL20

object ShaderLibrary {
    val phong = Phong()
    val lamp = Lamp()

    class Phong : Shader("phong") {
        val lgtCol  = GL20.glGetUniformLocation(name, "u_lightColor")
        val lgtDir  = GL20.glGetUniformLocation(name, "u_lightDir")
        val viewPos = GL20.glGetUniformLocation(name, "u_viewPos")
    }

    class Lamp : Shader("lamp")
}