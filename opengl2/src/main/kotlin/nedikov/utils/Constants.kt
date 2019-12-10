package nedikov.utils

import glm_.vec2.Vec2i
import glm_.vec3.Vec3
import glm_.vec4.Vec4

val zero = Vec3(0f)

val worldUp = Vec3(0f, 0f, 1f)

val clearColor0 = Vec4(0.1f, 0.1f, 0.1f, 1f)

val windowSize = Vec2i(1280, 720)

val verticesCube = floatArrayOf(
    -0.5f, +0.5f, -0.5f,  0f, 0f, -1f,  0f, 1f,
    +0.5f, +0.5f, -0.5f,  0f, 0f, -1f,  1f, 1f,
    +0.5f, -0.5f, -0.5f,  0f, 0f, -1f,  1f, 0f,
    -0.5f, -0.5f, -0.5f,  0f, 0f, -1f,  0f, 0f,

    -0.5f, -0.5f, +0.5f,  0f, 0f, 1f,  0f, 0f,
    +0.5f, -0.5f, +0.5f,  0f, 0f, 1f,  1f, 0f,
    +0.5f, +0.5f, +0.5f,  0f, 0f, 1f,  1f, 1f,
    -0.5f, +0.5f, +0.5f,  0f, 0f, 1f,  0f, 1f,

    -0.5f, +0.5f, +0.5f,  -1f, 0f, 0f,  1f, 1f,
    -0.5f, +0.5f, -0.5f,  -1f, 0f, 0f,  1f, 0f,
    -0.5f, -0.5f, -0.5f,  -1f, 0f, 0f,  0f, 0f,
    -0.5f, -0.5f, +0.5f,  -1f, 0f, 0f,  0f, 1f,

    +0.5f, -0.5f, +0.5f,  1f, 0f, 0f,  0f, 1f,
    +0.5f, -0.5f, -0.5f,  1f, 0f, 0f,  0f, 0f,
    +0.5f, +0.5f, -0.5f,  1f, 0f, 0f,  1f, 0f,
    +0.5f, +0.5f, +0.5f,  1f, 0f, 0f,  1f, 1f,

    -0.5f, -0.5f, -0.5f,  0f, -1f, 0f,  0f, 0f,
    +0.5f, -0.5f, -0.5f,  0f, -1f, 0f,  1f, 0f,
    +0.5f, -0.5f, +0.5f,  0f, -1f, 0f,  1f, 1f,
    -0.5f, -0.5f, +0.5f,  0f, -1f, 0f,  0f, 1f,

    -0.5f, +0.5f, +0.5f,  0f, 1f, 0f,  0f, 1f,
    +0.5f, +0.5f, +0.5f,  0f, 1f, 0f,  1f, 1f,
    +0.5f, +0.5f, -0.5f,  0f, 1f, 0f,  1f, 0f,
    -0.5f, +0.5f, -0.5f,  0f, 1f, 0f,  0f, 0f
)

val indicesCube = intArrayOf(
    0, 1, 2,
    2, 3, 0,

    4, 5, 6,
    6, 7, 4,

    8, 9, 10,
    10, 11, 8,

    12, 13, 14,
    14, 15, 12,

    16, 17, 18,
    18, 19, 16,

    20, 21, 22,
    22, 23, 20
)
