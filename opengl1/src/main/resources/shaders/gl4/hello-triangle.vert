
#version 450

#include semantic.glsl

layout (location = POSITION) in vec2 position;

layout (binding = TRANSFORM0) uniform Transform0
{
    mat4 proj;
    mat4 view;
};

layout (location = BLOCK) out vec2 pos;

void main() {
    pos = position;
    gl_Position = proj * (view * vec4(position, 0, 1));
}
