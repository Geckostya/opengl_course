#version 330 core

#define POSITION    0

layout (location = POSITION) in vec3 aPos;

uniform mat4 u_model;
uniform mat4 u_projectionView;


void main() {
    gl_Position = u_projectionView * u_model * vec4(aPos, 1.0f);
}