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
uniform vec4 Color0;
uniform vec4 Color1;
uniform vec4 Color2;

const vec3 gLightDirection = vec3(-.577, -.577, .577);
const float gShadowMapBias = 0.0008f;
const ivec2 gShadowMapSize = ivec2(1920, 1080);
const float gShadowStrength = 1.0;
const float gSunStrength = 0.8f;

const float gReentryStrength = 1.0;

const int gSubImageX = 0;
const int gSubImageY = 0;
const int gSubImageChannel = 0;
const int gSubImageXN = 0;
const int gSubImageYN = 0;
const int gSubImageChannelN = 0;
const float gNoiseLerp = 0;

const float gVelMultiplier = 0.00006;
const float gIncrement = 0.25;
const float gTaperSize = 0.5;


const float gBounceOffset = 1.0;
const vec4 gBounceColor = vec4(0.5, 0.5, 0.99, 0.1);
const float gBounceColLerpOffset = 0.5;

in float vertexDistance[3];
in vec4 vertexColor[3];
in vec2 texCoord0[3];
in GS_INPUT {
    vec3 worldPosition;
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

    float lengthGeneral = Velocity*gVelMultiplier;
    vec3 lengthVar = vec3(lengthGeneral);
    for(int i = 0; i < 3; i++) lengthVar[i] += getNoiseLength(gs_in[i].noiseTexCoord0, gs_in[i].noiseTexCoord1) * lengthGeneral*Length;


    //movement "Shadow" occlusion
//    vec3 occlusion = vec3(CalculateShadow(vertex[0][0].LightPosition, gMoveShadowMap, gMoveShadowBias),
//                              CalculateShadow(vertex[1][0].LightPosition, gMoveShadowMap, gMoveShadowBias),
//                              CalculateShadow(vertex[2][0].LightPosition, gMoveShadowMap, gMoveShadowBias));

    vec3 occlusion = vec3(1.0);

    lengthVar *= occlusion;

    float strength = Velocity/4000;
    vec3 brightCol = lerp(Color2.rgb, gBounceColor.rgb, saturate(strength-gBounceColLerpOffset));
    vec4 col1 = vec4(brightCol, Color2.a)*strength*Strength;
    vec4 col2 = lerp(Color2, Color1, Increment)*strength*Strength;
    vec4 col3 = Color1*strength*Strength;

    float curve = Increment*1.5;
    vec3 nMove = normalize(Direction);

    for(int i = 0; i < 3; i++) {

        vec3 normal = normalize(gs_in[i].normal);
        float velDot = -dot(normal, nMove);
        //determining length based on normal
        if (velDot > -0.1 && occlusion[i] > 0.9) {

            int j = (i + 1) % 3;

            vec3 nCenter = normalize(gs_in[i].worldPosition);
            vec3 nSize = normalize(cross(nMove, nCenter));

            velDot = pow(1.0 - velDot, 3.0);//assuming velDot larger 0

            vec3 length_i = lengthVar[i] * velDot * nMove;
            vec3 length_j = lengthVar[j] * velDot * nMove;

            vec3 outward = -cross(nMove, normal) * Increment * lengthVar[i] * velDot;
            vec3 sizeEnd = nSize * TaperSize;
            vec3 sizeTop = nSize * lerp(1.0, TaperSize, Increment);

            vec4 end_j = vec4(sizeEnd + length_j + (outward), 0.0);
            vec4 end_i = vec4(-sizeEnd + length_i + (outward), 0.0);
            vec4 top_j = vec4(sizeTop + length_j * Increment + (outward * curve), 0.0);
            vec4 top_i = vec4(-sizeTop + length_i * Increment + (outward * curve), 0.0);
            //Create Geometry (Trianglestrip) alternating between this and its adjacent edge
            createVertex(gl_in[i].gl_Position, col1);
            createVertex(gl_in[j].gl_Position, col1);
            createVertex(gl_in[i].gl_Position + top_i, col2);
            createVertex(gl_in[j].gl_Position + top_j, col2);
            createVertex(gl_in[i].gl_Position + end_i, col3);
            createVertex(gl_in[j].gl_Position + end_j, col3);
            EndPrimitive();
        }
    }
}