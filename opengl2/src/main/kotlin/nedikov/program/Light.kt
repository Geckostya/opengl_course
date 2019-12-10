package nedikov.program

import glm_.vec3.Vec3

open class Light(val color: Vec3)

class DirectionalLight(val direction: Vec3, color: Vec3) : Light(color)