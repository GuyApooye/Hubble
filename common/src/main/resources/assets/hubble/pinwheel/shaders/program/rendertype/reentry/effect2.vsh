#include veil:fog
#include veil:light

in vec3 Position;
in vec4 Color;
in vec2 UV0;
in ivec2 UV2;
in vec3 Normal;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform mat3 NormalMat;
uniform vec3 ChunkOffset;
uniform int FogShape;

uniform sampler2D Sampler2;

const int gSubImageX = 0;
const int gSubImageY = 0;
const int gSubImageChannel = 0;
const int gSubImageXN = 0;
const int gSubImageYN = 0;
const int gSubImageChannelN = 0;
const float gNoiseLerp = 0;

out float vertexDistance;
out vec4 vertexColor;
out vec2 texCoord0;
out GS_INPUT {
    vec3 normal;
    vec3 localNormal;
    vec3 viewDir;
    vec2 noiseTexCoord0;
    vec2 noiseTexCoord1;
} gs_in;

void main() {
    vec3 pos = Position + ChunkOffset;
    gl_Position = vec4(pos,1.0);

    vertexDistance = fog_distance(pos, FogShape);
    vertexColor = Color * minecraft_sample_lightmap(Sampler2, UV2);
    texCoord0 = UV0;
    gs_in.normal = Normal;
    //just in case this needs to be replaced when we get access to sable.
    //also idk what its supposed to do...
    gs_in.localNormal = Normal;
    //:cry:
    gs_in.viewDir = Normal;
    gs_in.noiseTexCoord0 = vec2(gSubImageX*0.25f+UV0.x*0.25f, gSubImageY*0.25f+UV0.y*0.25f);
    gs_in.noiseTexCoord1 = vec2(gSubImageXN*0.25f+UV0.x*0.25f, gSubImageYN*0.25f+UV0.y*0.25f);
}

