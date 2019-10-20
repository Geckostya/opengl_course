
#version 450


#include semantic.glsl


layout (location = BLOCK) in vec2 pos;

uniform float rThreshold = 1.3;
uniform int maxIter = 100;
uniform sampler1D colors;
uniform int colorsCount;


layout (location = FRAG_COLOR) out vec4 outputColor;

float make(int n, float r, vec2 c, vec2 z) {
    for (int i = 1; i < n; i++) {
        z = vec2(z.x * z.x - z.y * z.y + c.x, z.y * z.x + z.x * z.y + c.y);
        if (length(z) > r) {
            return 1. * i / n;
        }
    }
    return 1.;
}

void main() {
    float val = make(maxIter, rThreshold, pos, vec2(0));
    float shift = 0.5 / colorsCount;
    outputColor = vec4(texture(colors, shift + val * (1 - 2 * shift)).rgb, 1);
}
