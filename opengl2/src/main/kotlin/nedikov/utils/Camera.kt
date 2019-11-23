package nedikov.utils

import glm_.f
import glm_.func.cos
import glm_.func.rad
import glm_.func.sin
import glm_.glm
import glm_.vec2.Vec2d
import glm_.vec3.Vec3
import nedikov.utils.Camera.Movement.*
import java.lang.Math.PI

/**
 * Created by GBarbieri on 27.04.2017.
 */

/** An abstract camera class that processes input and calculates the corresponding Eular Angles, Vectors and Matrices
 *  for use in OpenGL   */
class Camera(
    //  Camera Attributes
    val position: Vec3 = Vec3(),
    var worldUp: Vec3 = Vec3(0f, 1f, 0f),
    //  Eular Angles
    var yaw: Float = (-90f).rad,
    var pitch: Float = 0f) {

    //  Camera Attributes
    var front = Vec3(0f, 0f, -1f)
    var up = Vec3()
    var right = Vec3()
    //  Camera options
    var movementSpeed = 2.5f
    var mouseSensitivity = 0.01f
    var zoom = 45f

    private val pitchConstaint: Float = (PI / 2 - 0.1).f

    val cursor = Cursor()

    init {
        updateCameraVectors()
    }

    /** Returns the view matrix calculated using Eular Angles and the LookAt Matrix */
    val viewMatrix get() = glm.lookAt(position, position + front, up)

    /**  Processes input received from any keyboard-like input system. Accepts input parameter in the form of camera
     *   defined ENUM (to abstract it from windowing systems)    */
    fun processKeyboard(direction: Movement, deltaTime: Float) {

        val velocity = movementSpeed * deltaTime

        position += when (direction) {
            Forward -> front * velocity
            Backward -> -front * velocity
            Left -> -right * velocity
            Right -> right * velocity
        }
    }

    /** Processes input received from a mouse input system. Expects the offset value in both the x and y direction. */

    private fun processMouseMovement(offset: Vec2d, constrainPitch: Boolean = true) {
        val x = offset.x * mouseSensitivity
        val y = offset.y * mouseSensitivity

        yaw += x.f
        pitch += y.f

        // Make sure that when pitch is out of bounds, screen doesn't get flipped
        if (constrainPitch) {
            pitch = glm.clamp(pitch, -pitchConstaint, pitchConstaint)
        }

        // Update Front, Right and Up Vectors using the updated Eular angles
        updateCameraVectors()
    }

    /** Processes input received from a mouse scroll-wheel event. Only requires input on the vertical wheel-axis    */
    fun processMouseScroll(yOffset: Float) {

        if (zoom in 1f..45f) {
            zoom -= yOffset
        }

        zoom = glm.clamp(zoom, 1f, 45f)
    }

    /** Calculates the front vector from the Camera's (updated) Eular Angles    */
    fun updateCameraVectors() {
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

    fun mouseCallback(pos: Vec2d) {
        cursor.update(pos)
        processMouseMovement(cursor.offset)
    }

    inner class Cursor {
        private val last = Vec2d()
        val offset = Vec2d()

        private var firstMouse = true

        fun update(pos: Vec2d) {
            if (firstMouse) {
                last.put(pos)
                firstMouse = false
            }

            offset.put(pos.x - last.x, last.y - pos.y) *= 0.1
            last.put(pos)
        }
    }

    enum class Movement { Forward, Backward, Left, Right }
}