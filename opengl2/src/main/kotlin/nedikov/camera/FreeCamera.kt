package nedikov.camera

import glm_.f
import glm_.func.cos
import glm_.func.rad
import glm_.func.sin
import glm_.glm
import glm_.vec2.Vec2d
import glm_.vec3.Vec3

class FreeCamera(
    position: Vec3 = Vec3(),
    worldUp: Vec3 = Vec3(0f, 1f, 0f),
    yaw: Float = (-90f).rad,
    pitch: Float = 0f
) : Camera(position, worldUp, yaw, pitch) {

    init {
        updateCameraVectors()
    }

    override fun processKeyboard(direction: Movement, deltaTime: Float) {

        val velocity = movementSpeed * deltaTime

        position += when (direction) {
            Movement.Forward -> front * velocity
            Movement.Backward -> -front * velocity
            Movement.Left -> -right * velocity
            Movement.Right -> right * velocity
        }
    }

    /** Processes input received from a mouse input system. Expects the offset value in both the x and y direction. */

    override fun processMouseMovement(offset: Vec2d) {
        val x = offset.x * mouseSensitivity
        val y = offset.y * mouseSensitivity

        yaw += x.f
        pitch += y.f

        // Make sure that when pitch is out of bounds, screen doesn't get flipped
        pitch = glm.clamp(pitch, -pitchConstraint, pitchConstraint)

        // Update Front, Right and Up Vectors using the updated Eular angles
        updateCameraVectors()
    }

    /** Processes input received from a mouse scroll-wheel event. Only requires input on the vertical wheel-axis    */
    override fun processMouseScroll(yOffset: Float) {
        if (zoom in 1f..45f) {
            zoom -= yOffset
        }

        zoom = glm.clamp(zoom, 1f, 45f)
    }

    // Calculates the front vector from the Camera's (updated) Eular Angles
    override fun updateCameraVectors() {
        // Calculate the new Front vector
        front.put(
            x = yaw.cos * pitch.cos,
            y = pitch.sin,
            z = yaw.sin * pitch.cos
        ).normalizeAssign()
        /*  Also re-calculate the Right and Up vector, by taking care to normalize the vectors, because their length
            gets closer to 0 the more you look up or down which results in slower movement.         */
        right put (front cross worldUp).normalizeAssign()
        up put (right cross front).normalizeAssign()
    }

    companion object {
        private val pitchConstraint: Float = (Math.PI / 2 - 0.1).f
    }

    enum class Movement { Forward, Backward, Left, Right }
}