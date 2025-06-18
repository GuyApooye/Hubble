#extension GL_ARB_geometry_shader4 : enable

layout (triangles) in;
layout (triangle_strip, max_vertices = 30) out;

uniform sampler2D NoiseSampler;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 NormalMat;
uniform vec3 Direction;
uniform float Length;
uniform float Velocity;
uniform float Strength;
uniform float Increment;
uniform float TaperSize;
uniform vec4 LightColor;
uniform vec4 TrailColor;
uniform vec4 TrailColorHot;
uniform vec4 BowShockColor;
uniform float BowShockOffset;
uniform float BowShockColorLerpOffset;

const vec3 gLightDirection = vec3(-.577, -.577, .577);
const float gShadowMapBias = 0.0008f;
const ivec2 gShadowMapSize = ivec2(1920, 1080);
const float gShadowStrength = 1.0;
const float gSunStrength = 0.8f;

const int gSubImageX = 0;
const int gSubImageY = 0;
const int gSubImageChannel = 0;
const int gSubImageXN = 0;
const int gSubImageYN = 0;
const int gSubImageChannelN = 0;
const float gNoiseLerp = 0;

in float vertexDistance[3];
in vec4 vertexColor[3];
in vec2 texCoord0[3];
in GS_INPUT {
    //    vec3 worldPosition;
    vec3 normal;
    vec2 noiseTexCoord0;
    vec2 noiseTexCoord1;
} gs_in[3];

out vec4 fVertexColor;

float lerp(float a, float b, float t) {
    return a + t * (b - a);
}

vec2 lerp(vec2 a, vec2 b, float t) {
    return a + t * (b - a);
}

vec3 lerp(vec3 a, vec3 b, float t) {
    return a + t * (b - a);
}

vec4 lerp(vec4 a, vec4 b, float t) {
    return a + t * (b - a);
}

float saturate(float a) {
    return clamp(a, 0.0, 1.0);
}

float getNoiseLength(vec2 tc, vec2 tcN)
{
    float lengthVar = texture(NoiseSampler, tc).x;
    float lengthVarNext = texture(NoiseSampler, tcN).x;
    return lerp(lengthVarNext, lengthVar, gNoiseLerp);
}

void createVertex(vec4 pos, vec4 color) {
    gl_Position = ProjMat * ModelViewMat * pos;
    fVertexColor = color;
    EmitVertex();
}

void main() {

    float strength = Velocity/4000.0;
//    vec3 occlusion = vec3(CalculateShadow(vertex[0][0].LightPosition, gMoveShadowMap, gMoveShadowBias),
//                              CalculateShadow(vertex[1][0].LightPosition, gMoveShadowMap, gMoveShadowBias),
//                              CalculateShadow(vertex[2][0].LightPosition, gMoveShadowMap, gMoveShadowBias));

    vec3 occlusion = vec3(1.0);

    vec3 nMove = normalize(Direction);

    for(int i = 0; i < 3; i++)
    {
        vec3 normal = normalize(gs_in[i].normal);
        float velDot = dot(normal, nMove);
        velDot = pow(velDot, 3.0);
        if(occlusion[i]>0.9/** && velDot>0.0*/)
        {

            float alpha = -velDot*occlusion[i]*strength*Strength;
            vec4 col = BowShockColor*alpha;
            vec4 offset = vec4(alpha * BowShockOffset * normal, 0.0);

            createVertex(gl_in[i].gl_Position+offset, col);
        }
    }
}