#version 330 core

#define FRAG_COLOR    0

layout (location = FRAG_COLOR) out vec4 fragColor;

in vec3 Normal;
in vec3 FragPos;
in vec2 TexCoord;
in vec4 FragPosLightSpace;

uniform vec3 u_lightDir;
uniform vec3 u_viewPos;
uniform vec3 u_lightColor;
uniform vec3 u_objectColor;

uniform sampler2D u_shadowMap;

float ambient() {
    float ambientStrength = 0.1f;
    return ambientStrength;
}

float diffuse(vec3 norm, vec3 lightNorm) {
    float diff = max(dot(norm, lightNorm), 0.0f);
    return diff;
}

float specular(vec3 norm, vec3 lightDir) {
    float specularStrength = 0.5f;
    vec3 viewDir = normalize(u_viewPos - FragPos);
    vec3 reflectDir = reflect(lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
    return specularStrength * spec;
}
float shadow() {
    vec3 projCoords = FragPosLightSpace.xyz / FragPosLightSpace.w;
    projCoords = projCoords * 0.5 + 0.5;
    float closestDepth = texture(u_shadowMap, projCoords.xy).r;
    float currentDepth = projCoords.z;
    float shadow = currentDepth > closestDepth  ? 1.0 : 0.0;
    return shadow;
}

float light() {
    vec3 norm = normalize(Normal);
    return ambient() + (1.0 - shadow()) * (diffuse(norm, -u_lightDir) + specular(norm, u_lightDir));
}

void main() {
    fragColor = vec4(light() * u_objectColor, 1);
}