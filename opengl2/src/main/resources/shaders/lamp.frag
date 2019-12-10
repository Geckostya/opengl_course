#version 330 core

#define FRAG_COLOR    0

layout (location = FRAG_COLOR) out vec4 fragColor;

uniform vec3 u_objectColor;

void main() {
    fragColor = vec4(u_objectColor, 1.0f);
}