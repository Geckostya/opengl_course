package nedikov.program

import glm_.glm
import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import nedikov.utils.worldUp
import nedikov.utils.zero

abstract class Light(val color: Vec3) {
    abstract val viewMatrix: Mat4
}

class DirectionalLight(val direction: Vec3, color: Vec3) : Light(color) {
    override val viewMatrix = Mat4()
    init {
        update()
    }

    fun update() {
        direction.normalizeAssign()
        glm.lookAt(
            viewMatrix,
            direction * (-25),
            zero,
            worldUp
        )
    }
}