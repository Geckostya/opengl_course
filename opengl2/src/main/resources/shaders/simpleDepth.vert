#version 330 core

#define POSITION    0

layout (location = POSITION) in vec3 aPos;

uniform mat4 u_projectionView;
uniform mat4 u_model;

void main() {
    gl_Position = u_projectionView * u_model * vec4(aPos, 1.0);
}