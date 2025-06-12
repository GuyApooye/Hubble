#version 420

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D NoiseSampler;
layout (binding = 0) uniform sampler2DArray Textures;

in vec2 texCoord;

out vec4 fragColor;

float depthSampleToWorldDepth(in float depthSample) {
    float f = depthSample * 2.0 - 1.0;
    return 2.0 * VeilCamera.NearPlane * VeilCamera.FarPlane / (VeilCamera.FarPlane + VeilCamera.NearPlane - f * (VeilCamera.FarPlane - VeilCamera.NearPlane));
}

void main() {

    fragColor = texture(DiffuseSampler0, texCoord);
    gl_FragDepth = texture(DiffuseDepthSampler, texCoord).r;

    float depth = depthSampleToWorldDepth(gl_FragDepth);
    vec3 camera = VeilCamera.CameraPosition;
    vec3 rd = viewDirFromUv(texCoord);

    float noise = texture(NoiseSampler, 10.0*texCoord).r;
    noise = (noise - 0.5) * .1;

    if (PlanetData.Size > 0) {
        fragColor = texture(Textures, vec3(texCoord, 0.0));
    }

}