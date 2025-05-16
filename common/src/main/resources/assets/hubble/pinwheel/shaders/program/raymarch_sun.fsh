uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;

#define MAX_STEPS 300
#define MAX_DIST 10000.0
#define SOURCE_SIZE 0.1

in vec2 texCoord;

out vec4 fragColor;

float getGlow(in float dist, float size, float intensity){
    return 0.1/dist;
}
float manhattan(in vec3 pos) {
    pos = abs(pos);
    return pos.x + pos.y + pos.z;
}
float sdBox(in vec3 p, in vec3 b )
{
    vec3 q = abs(p) - b;
    return length(max(q,0.0)) - min(max(q.x,max(q.z,q.y)),0.0);
}
float sdBoxx(in vec3 p, in vec3 b )
{
    vec3 q = abs(p) - b;

    return max(q.x,max(q.y,q.z));
}

float sdRotatedBox(in vec3 pos, in vec3 center, in mat4 mat, in vec3 dims) {
    vec4 newPos = vec4(pos - center, 1.0);
    newPos = newPos * mat;
    return sdBox(newPos.xyz, dims);
}

float sdRotatedBoxx(in vec3 pos, in vec3 center, in mat4 mat, in vec3 dims) {
    vec4 newPos = vec4(pos - center, 1.0);
    newPos = newPos * mat;
    return sdBoxx(newPos.xyz, dims);
}

float sceneSDF(in vec3 pos, out int closestHit, out vec4 color) {
    if (SunData.Size <= 0) return 0.0;
    float minDist = -1.0;

    for (int i = 0; i < SunData.Size; i++) {
        float dist = abs(sdRotatedBox(pos, SunData.Pos[i], SunData.Rot[i], SunData.Dims[i]));
        float boxDist = sdRotatedBoxx(pos, SunData.Pos[i], SunData.Rot[i], SunData.Dims[i]);

        if(minDist > dist || minDist < 0) {
            minDist = dist;
            closestHit = i;
        }


//        if (dist <= 0.5/LightData.size[i]) continue;



//        if (boxDist <= -SunData.Length[i]/2.0) {
//            color = 2 * vec4(SunData.Color[i],1.0);
//        }
//        else if (boxDist <= -SunData.Length[i]/4.0) {
//            color = 3 * vec4(SunData.Color[i],1.0);
//        }
//        else {
//
//        }
//        dist = max(dist,2.0);
        color += vec4(SunData.Color[i],1.0) * getGlow(dist, SunData.Length[i], 0.1);
    }

    return minDist;
}

bool calculateDists(vec3 ro, vec3 rd, in float depth, out vec3 closestPoint, out float closestDistance, out int closestHit, out vec4 color) {
//    if (LightData.dataSize <= 0) return false;
    closestPoint = ro;
    float total = 0.0;

    closestDistance = sceneSDF(ro + rd * total, closestHit, color);

    float stepDistance = .0;

    for(int i = 0; i < MAX_STEPS && total < MAX_DIST; i++) {

//        if (total >= depth) return false;

        stepDistance = sceneSDF(ro + rd * total, closestHit, color);
        if (stepDistance < closestDistance) {
            closestPoint = ro + rd * total;
            closestDistance = stepDistance;
        }
//        closestDistance = min(stepDistance, closestDistance);

//        if(stepDistance <= 1e-5) {
//            return true;
//        }

        total += stepDistance;
    }
    return false;
}

void main() {

    fragColor = texture(DiffuseSampler0, texCoord);
    float depth = texture(DiffuseDepthSampler, texCoord).r;
    vec3 camera = VeilCamera.CameraPosition;
    vec3 rd = viewDirFromUv(texCoord);

    vec3 closestPoint = vec3(0.0);
    vec4 color = vec4(0.0);
    float closestDistance = 0.0;
    int closestHit = 0;
    bool hit = calculateDists(camera, rd, depth, closestPoint, closestDistance, closestHit, color);

    acesToneMapping(color.xyz);
//    if (hit) {
//        fragColor = vec4(LightData.color[closestHit],1);
//        fragColor += color;
//    } else {
//        acesToneMapping(color.xyz);
//        fragColor += color;
//    }
    fragColor += color;
//    fragColor = fragColor + vec4(closestDistance);

//    fragColor = vec4(LightData.dataSize);


}