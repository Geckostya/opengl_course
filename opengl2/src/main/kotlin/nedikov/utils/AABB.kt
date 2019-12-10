package nedikov.utils

import glm_.vec3.Vec3
import kotlin.math.max
import kotlin.math.min
import kotlin.ranges.IntProgression.Companion.fromClosedRange

class AABB(val min: Vec3, val max: Vec3) {
    fun put(x: Float, y: Float, z: Float) {
        min.put(min(min.x, x), min(min.y, y), min(min.z, z))
        max.put(max(max.x, x), max(max.y, y), max(max.z, z))
    }

    fun put(vertices: FloatArray, vSize: Int) {
        for (i in fromClosedRange(0, vertices.size, vSize)) {
            put(vertices[i], vertices[i + 1], vertices[i + 2])
        }
    }
}