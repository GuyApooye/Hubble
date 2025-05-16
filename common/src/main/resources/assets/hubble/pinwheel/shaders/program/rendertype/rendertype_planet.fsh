//#include veil:fog

#define MAX_STEPS 100
#define MAX_DIST 1000.
#define SOURCE_SIZE 0.5
#define DATA_SIZE

struct PlanetRenderData
{
    vec3[25] pos;
    vec3[25] dims;
    mat4[25] rot;
};

uniform sampler2D Sampler0;

uniform PlanetRenderData RenderData;

uniform vec3 Light;
uniform int DataSize;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec3 fragPos;

out vec4 fragColor;

float sdBox( vec3 p, vec3 b )
{
    vec3 q = abs(p) - b;
    return length(max(q,0.0)) + min(max(q.x,max(q.z,q.y)),0.0);
}

float sdRotatedBox(vec3 pos, vec3 center, mat4 mat, vec3 dims) {
    vec4 newPos = vec4(pos - center, 1.0);
    newPos = newPos * mat;
    return sdBox(newPos.xyz, dims);
}

float sceneSDF(vec3 pos) {
//    if (RenderData.length() <= 0) return 0.0;
    float minDist = sdRotatedBox(pos, RenderData.pos[0], RenderData.rot[0], RenderData.dims[0]);
    for (int i = 1; i < DataSize; i++) {
        minDist = min(minDist, sdRotatedBox(pos, RenderData.pos[i], RenderData.rot[i], RenderData.dims[i]));
    }
    return minDist;
}

float raymarchLight(vec3 ro, vec3 lo) {

    float rayLength = length(lo - ro);
    vec3 rd = (lo - ro) / rayLength;

    float res = 1.0;

    float total = 0.0;

    float stepDistance = .0;

    float ph = 1e20;
    for(int i = 0; i < MAX_STEPS && total < MAX_DIST; i++) {

        stepDistance = sceneSDF(ro + rd * total);
        if(stepDistance < 0.0005) {
            return 0.05;
        }

        float y = stepDistance*stepDistance/(2.0*ph);
        float d = sqrt(stepDistance*stepDistance-y*y);

        res = min(res, d/(SOURCE_SIZE*max(0.0,total-y)));

        ph = stepDistance;
        total += stepDistance;
//        if (total >= (rayLength - SOURCE_SIZE)) break;
//        if (length(normalize(lo - (ro + rd * total)) + rd) <= 0.05) break;
    }

    return max(res,0.05) * 25.0 / rayLength;
}


void main() {
    fragColor = texture(Sampler0, texCoord0) * raymarchLight(fragPos, Light);
}
