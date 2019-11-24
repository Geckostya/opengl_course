package nedikov.camera

import glm_.f
import glm_.func.cos
import glm_.func.rad
import glm_.func.sin
import glm_.glm
import glm_.vec2.Vec2d
import nedikov.camera.Camera.Movement.*
import kotlin.math.sqrt

class OrbitCamera : Camera(yaw = 125f.rad, pitch = 25f.rad) {

    var distance = 5f

    init {
        updateCameraVectors()
    }

    override fun processKeyboard(direction: Movement, deltaTime: Float) {
        processMouseMovement( when(direction) {
            Left -> Vec2d(1, 0)
            Forward -> Vec2d(0, -1)
            Backward -> Vec2d(0, 1)
            Right -> Vec2d(-1, 0)
        } * deltaTime * 1000)
    }

    override fun processMouseMovement(offset: Vec2d) {
        yaw += offset.x.f * mouseSensitivity
        pitch -= offset.y.f * mouseSensitivity
        pitch = glm.clamp(pitch, -pitchConstraint, pitchConstraint)
        updateCameraVectors()
    }

    override fun processMouseScroll(yOffset: Float) {
        val offset = yOffset * (sqrt(distance) / 10f)
        distance = glm.clamp(distance - offset, 1f, 50f)
        updateCameraVectors()
    }

    override fun updateCameraVectors() {
        position.put(
            x = yaw.cos * pitch.cos,
            y = pitch.sin,
            z = yaw.sin * pitch.cos
        ).normalizeAssign() *= distance

        front = (-position).normalizeAssign()
        right put (front cross worldUp).normalizeAssign()
        up put (right cross front).normalizeAssign()
    }

}