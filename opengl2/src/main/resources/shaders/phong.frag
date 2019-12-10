#version 330 core

#define FRAG_COLOR    0

layout (location = FRAG_COLOR) out vec4 fragColor;

in vec3 Normal;
in vec3 FragPos;
in vec2 TexCoord;

uniform vec3 u_lightPos;
uniform vec3 u_viewPos;
uniform vec3 u_lightColor;
uniform vec3 u_objectColor;

uniform sampler2D u_noise;
uniform float u_time;

vec3 ambient() {
    float ambientStrength = 0.1f;
    return ambientStrength * u_lightColor;
}

vec3 diffuse(vec3 norm, vec3 lightDir) {
    float diff = max(dot(norm, lightDir), 0.0f);
    return diff * u_lightColor;
}

vec3 specular(vec3 norm, vec3 lightDir) {
    float specularStrength = 0.5f;
    vec3 viewDir = normalize(u_viewPos - FragPos);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32);
    return specularStrength * spec * u_lightColor;
}

vec3 light() {
    vec3 norm = normalize(FragPos);
    vec3 lightDir = normalize(u_lightPos - FragPos);
    return ambient() + diffuse(norm, lightDir) + specular(norm, lightDir);
}

void main() {
    vec4 noise = texture(u_noise, TexCoord);

    float revTime = 1 - u_time;

    if (noise.x < revTime) discard;

    float border = 0.1;
    float dissolve = 1 - (clamp(noise.x, revTime, revTime + border) - revTime) / border;
    vec3 dissolveLight = vec3(0, 1, 1);

    fragColor = vec4(mix(light() * u_objectColor, dissolveLight, dissolve), 1);
}