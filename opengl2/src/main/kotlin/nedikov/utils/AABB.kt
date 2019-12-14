package nedikov.utils

import glm_.mat4x4.Mat4
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import kotlin.Float.Companion.NEGATIVE_INFINITY
import kotlin.Float.Companion.POSITIVE_INFINITY
import kotlin.math.max
import kotlin.math.min

class AABB(val min: Vec3, val max: Vec3) {
    private fun put(x: Float, y: Float, z: Float) {
        min.put(min(min.x, x), min(min.y, y), min(min.z, z))
        max.put(max(max.x, x), max(max.y, y), max(max.z, z))
    }

    fun put(tm: Mat4, vertices: FloatArray, vSize: Int) {
        val v = Vec4()
        for (i in 0 until vertices.size step vSize) {
            v.put(vertices[i], vertices[i + 1], vertices[i + 2], 1f)
            tm.times_(v)
            put(v.x, v.y, v.z)
        }
    }

    // returns left, right, bottom, top for view
    fun bounds(view: Mat4): Vec4 {
        val res = Vec4(POSITIVE_INFINITY, NEGATIVE_INFINITY, POSITIVE_INFINITY, NEGATIVE_INFINITY)
        val vec = Vec4()
        for (i in 0..7) {
            vec.put(
                if (i and 1 == 0) min.x else max.x,
                if (i and 2 == 0) min.y else max.y,
                if (i and 4 == 0) min.z else max.z,
                1f
            )
            view.times_(vec)
            res.x = min(res.x, vec.x)
            res.y = max(res.y, vec.x)
            res.z = min(res.z, vec.y)
            res.w = max(res.w, vec.y)
        }
        return res
    }
}