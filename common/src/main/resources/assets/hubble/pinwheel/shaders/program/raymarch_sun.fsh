//#veil:buffer veil:camera VeilCamera
//#include veil:space_helper

uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;

#define MAX_STEPS 200
#define MAX_DIST 10000.0

in vec2 texCoord;

out vec4 fragColor;

void main() {

    vec3 camera = VeilCamera.CameraPosition;
    vec3 rd = viewDirFromUv(texCoord);


}