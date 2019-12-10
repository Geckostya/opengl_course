#version 330 core

#define POSITION    0
#define NORMAL      1
#define TEX_COORD   4

layout (location = POSITION) in vec3 aPos;
layout (location = NORMAL) in vec3 aNormal;
layout (location = TEX_COORD) in vec2 aTexCoord;

in vec2 texCoord;

uniform mat4 u_model;
uniform mat4 u_projectionView;
uniform mat4 u_lightSpaceMatrix;

out vec3 FragPos;
out vec3 Normal;
out vec2 TexCoord;
out vec4 FragPosLightSpace;

void main() {
    vec4 modelPos = (u_model * vec4(aPos, 1.0f));
    FragPos = modelPos.xyz;
    Normal = mat3(transpose(inverse(u_model))) * aNormal;
    TexCoord = aTexCoord;

    FragPosLightSpace = u_lightSpaceMatrix * vec4(FragPos, 1.0);

    gl_Position = u_projectionView * modelPos;
}