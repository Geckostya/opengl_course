#version 330 core

#define FRAG_COLOR    0

layout (location = FRAG_COLOR) out vec4 fragColor;

in vec3 Normal;
in vec3 FragPos;
in vec2 TexCoord;

uniform vec3 u_lightDir;
uniform vec3 u_viewPos;
uniform vec3 u_lightColor;
uniform vec3 u_objectColor;

vec3 ambient() {
    float ambientStrength = 0.1f;
    return ambientStrength * u_lightColor;
}

vec3 diffuse(vec3 norm, vec3 lightNorm) {
    float diff = max(dot(norm, lightNorm), 0.0f);
    return diff * u_lightColor;
}

vec3 specular(vec3 norm, vec3 lightDir) {
    float specularStrength = 0.5f;
    vec3 viewDir = normalize(u_viewPos - FragPos);
    vec3 reflectDir = reflect(lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
    return specularStrength * spec * u_lightColor;
}

vec3 light() {
    vec3 norm = normalize(Normal);
    return ambient() + diffuse(norm, -u_lightDir) + specular(norm, u_lightDir);
}

void main() {
    fragColor = vec4(light() * u_objectColor, 1);
}