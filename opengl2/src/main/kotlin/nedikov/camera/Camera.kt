package nedikov.camera

import glm_.f
import glm_.func.rad
import glm_.glm
import glm_.vec2.Vec2d
import glm_.vec3.Vec3

abstract class Camera(
    val position: Vec3 = Vec3(),
    var worldUp: Vec3 = Vec3(0f, 1f, 0f),
    var yaw: Float = (-90f).rad,
    var pitch: Float = 0f
) {

    var front = Vec3(0f, 0f, -1f)
    var up = Vec3()
    var right = Vec3()

    var movementSpeed = 2.5f
    var mouseSensitivity = 0.001f
    var zoom = 45f

    /** Returns the view matrix calculated using Eular Angles and the LookAt Matrix */
    val viewMatrix get() = glm.lookAt(position, position + front, up)

    /**  Processes input received from any keyboard-like input system. Accepts input parameter in the form of camera
     *   defined ENUM (to abstract it from windowing systems)    */
    abstract fun processKeyboard(direction: Movement, deltaTime: Float)

    /** Processes input received from a mouse input system. Expects the offset value in both the x and y direction. */

    abstract fun processMouseMovement(offset: Vec2d)

    /** Processes input received from a mouse scroll-wheel event. Only requires input on the vertical wheel-axis    */
    abstract fun processMouseScroll(yOffset: Float)

    // Calculates the front vector from the Camera's (updated) Eular Angles
    abstract fun updateCameraVectors();

    enum class Movement { Forward, Backward, Left, Right }


    companion object {
        @JvmStatic
        protected val pitchConstraint: Float = (Math.PI / 2 - 0.1).f
    }
}