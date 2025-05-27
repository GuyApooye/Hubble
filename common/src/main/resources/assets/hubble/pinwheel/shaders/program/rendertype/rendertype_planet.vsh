
layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;
layout(location = 5) in vec3 Normal;
layout(location = 2) in vec2 UV0;

uniform sampler2D Sampler2;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform mat3 NormalMat;

out vec2 texCoord0;
out vec3 fragPos;
out vec3 fragNormal;

void main() {
    vec3 pos = Position + ChunkOffset;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

    fragPos = pos + VeilCamera.CameraPosition;
    fragNormal = Normal * NormalMat;
    #ifdef VEIL_NORMAL
    // #veil:normal
    vec3 normal = fragNormal;
    #endif

    texCoord0 = UV0;
}
