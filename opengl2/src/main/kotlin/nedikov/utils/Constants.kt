package nedikov.utils

import glm_.vec2.Vec2i
import glm_.vec3.Vec3
import glm_.vec4.Vec4
import kotlin.math.max
import kotlin.math.min


val clearColor0 = Vec4(0.1f, 0.1f, 0.1f, 1f)

val windowSize = Vec2i(1280, 720)

val verticesCube = gridCubeVertices(32, 8, 1f, 0.5f, 0.75f)
val indicesCube = gridCubeIndices(32)

fun gridCubeVertices(n: Int, k: Int, x: Float, y: Float, z: Float): FloatArray {
    assert(2 * k < n)
    val vertices = FloatArray((n + 1) * (n + 1) * 6 * 8)
    val size = 2f / n
    var ind = 0
    val sx = k * size * x
    val sy = k * size * y
    val sz = k * size * z
    val s = Vec3(sx, sy, sz)
    for (i in 0..n) { // bottom
        for (j in 0..n) {
            vertices[ind] = (i * size - 1f) * x
            vertices[ind + 1] = ((n - j) * size - 1f) * y
            vertices[ind + 2] = -z
            vertices[ind + 5] = -1f
            vertices[ind + 6] = i * size / 2
            vertices[ind + 7] = j * size / 2
            if (onEdge(i, j, k, n)) {
                val x1 = choose(i, k, n, sx - x, vertices[ind])
                val y1 = choose(j, k, n, y - sy, vertices[ind + 1])
                val z1 = -z + sz
                smooth(x1, y1, z1, vertices, ind, s)
            }
            ind += 8
        }
    }
    for (i in 0..n) { // front
        for (j in 0..n) {
            vertices[ind] = x
            vertices[ind + 1] = ((n - j) * size - 1f) * y
            vertices[ind + 2] = (i * size - 1f) * z
            vertices[ind + 3] = 1f
            vertices[ind + 6] = i * size / 2
            vertices[ind + 7] = j * size / 2
            if (onEdge(i, j, k, n)) {
                val x1 = x - sx
                val y1 = choose(j, k, n, y - sy, vertices[ind + 1])
                val z1 = choose(i, k, n, sz - z, vertices[ind + 2])
                smooth(x1, y1, z1, vertices, ind, s)
            }
            ind += 8
        }
    }
    for (i in 0..n) { // right
        for (j in 0..n) {
            vertices[ind] = (j * size - 1f) * x
            vertices[ind + 1] = y
            vertices[ind + 2] = (i * size - 1f) * z
            vertices[ind + 4] = 1f
            vertices[ind + 6] = i * size / 2
            vertices[ind + 7] = j * size / 2
            if (onEdge(i, j, k, n)) {
                val x1 = choose(j, k, n, sx - x, vertices[ind])
                val y1 = y - sy
                val z1 = choose(i, k, n, sz - z, vertices[ind + 2])
                smooth(x1, y1, z1, vertices, ind, s)
            }
            ind += 8
        }
    }
    for (i in 0..n) { // back
        for (j in 0..n) {
            vertices[ind] = -x
            vertices[ind + 1] = (j * size - 1f) * y
            vertices[ind + 2] = (i * size - 1f) * z
            vertices[ind + 3] = -1f
            vertices[ind + 6] = i * size / 2
            vertices[ind + 7] = j * size / 2
            if (onEdge(i, j, k, n)) {
                val x1 = -x + sx
                val y1 = choose(j, k, n, sy - y, vertices[ind + 1])
                val z1 = choose(i, k, n, sz - z, vertices[ind + 2])
                smooth(x1, y1, z1, vertices, ind, s)
            }
            ind += 8
        }
    }
    for (i in 0..n) { // left
        for (j in 0..n) {
            vertices[ind] = ((n - j) * size - 1f) * x
            vertices[ind + 1] = -y
            vertices[ind + 2] = (i * size - 1f) * z
            vertices[ind + 4] = -1f
            vertices[ind + 6] = i * size / 2
            vertices[ind + 7] = j * size / 2
            if (onEdge(i, j, k, n)) {
                val x1 = choose(j, k, n, x - sx, vertices[ind])
                val y1 = -y + sy
                val z1 = choose(i, k, n, sz - z, vertices[ind + 2])
                smooth(x1, y1, z1, vertices, ind, s)
            }
            ind += 8
        }
    }
    for (i in 0..n) { // top
        for (j in 0..n) {
            vertices[ind] = ((n - i) * size - 1f) * x
            vertices[ind + 1] = ((n - j) * size - 1f) * y
            vertices[ind + 2] = z
            vertices[ind + 5] = 1f
            vertices[ind + 6] = i * size / 2
            vertices[ind + 7] = j * size / 2

            if (onEdge(i, j, k, n)) {
                val x1 = choose(i, k, n, x - sx, vertices[ind])
                val y1 = choose(j, k, n, y - sy, vertices[ind + 1])
                val z1 = z - sz
                smooth(x1, y1, z1, vertices, ind, s)
            }
            ind += 8
        }
    }
    return vertices
}

private fun choose(ind: Int, k: Int, n: Int, v: Float, default: Float): Float {
    return when {
        ind <= k -> v
        ind >= n - k -> -v
        else -> default
    }
}

private fun onEdge(i: Int, j: Int, k: Int, n: Int) = min(i, j) < k || max(i, j) > n - k

private fun smooth(x1: Float, y1: Float, z1: Float, vertices: FloatArray, ind: Int, shift: Vec3) {
    val center = Vec3(x1, y1, z1)
    val point = Vec3(vertices[ind], vertices[ind + 1], vertices[ind + 2])
    val norm = (point - center).normalizeAssign()
    center += norm * shift
    vertices[ind] = center.x
    vertices[ind + 1] = center.y
    vertices[ind + 2] = center.z
    vertices[ind + 3] = norm.x
    vertices[ind + 4] = norm.y
    vertices[ind + 5] = norm.z
}

fun gridCubeIndices(n: Int): IntArray {
    val indices = IntArray(6 * n * n * 6)
    var ind = 0
    var vInd = 0
    for (side in 1..6) {
        for (i in 1..n) {
            for (j in 1..n) {
                indices[ind] = vInd
                indices[ind + 1] = vInd + n + 1
                indices[ind + 2] = vInd + 1
                indices[ind + 3] = vInd + 1
                indices[ind + 4] = vInd + n + 1
                indices[ind + 5] = vInd + n + 2
                ind += 6
                vInd++
            }
            vInd++
        }
        vInd += n + 1
    }
    return indices
}