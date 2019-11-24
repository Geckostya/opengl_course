#version 330 core

#define POSITION    0
#define NORMAL      1

layout (location = POSITION) in vec3 aPos;
layout (location = NORMAL) in vec3 aNormal;

uniform mat4 u_model;
uniform mat4 u_view;
uniform mat4 u_projection;

out vec3 FragPos;
out vec3 Normal;

void main()
{
    FragPos = (u_model * vec4(aPos, 1.0f)).xyz;
    Normal = mat3(transpose(inverse(u_model))) * aNormal;

    gl_Position = u_projection * (u_view * vec4(FragPos, 1.0f));
}