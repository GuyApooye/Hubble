in vec3 Position;
in vec3 Normal;
in vec4 Color;
in vec2 UV0;

uniform sampler2D Sampler2;

out vec2 texCoord;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform int FogShape;


out vec2 texCoord0;
out vec3 fragPos;
out vec3 fragNormal;

void main() {
    vec3 pos = Position + ChunkOffset;
    gl_Position = ProjMat * ModelViewMat * vec4(pos, 1.0);

    fragPos = pos + VeilCamera.CameraPosition;
    fragNormal = Normal;
    texCoord0 = UV0;
}
