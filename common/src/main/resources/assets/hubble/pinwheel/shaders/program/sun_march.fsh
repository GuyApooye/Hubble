uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;

#define MAX_STEPS 200
#define MAX_DIST 10000.0

in vec2 texCoord;
in float fragDist;

out vec4 fragColor;

float getGlow(in float dist, float size, float intensity){
    return pow(0.1/dist, 0.8);
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
//        float boxDist = sdRotatedBoxx(pos, SunData.Pos[i], SunData.Rot[i], SunData.Dims[i]);

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
        color += vec4(SunData.Color[i],1.0) * getGlow(dist, SunData.Length[i], SunData.Intensity[i]);
    }

    return minDist;
}

float depthSampleToWorldDepth(float depthSample) {
    float f = depthSample * 2.0 - 1.0;
    return 2.0 * VeilCamera.NearPlane * VeilCamera.FarPlane / (VeilCamera.FarPlane + VeilCamera.NearPlane - f * (VeilCamera.FarPlane - VeilCamera.NearPlane));
}

bool raymarch(vec3 ro, vec3 rd, int i, in float depth, out float closestDistance, out vec4 color) {

    float total = 0.0;

    vec3 pos = SunData.Pos[i];
    vec3 dims = SunData.Dims[i];
    mat4 rot = SunData.Rot[i];

    float glow = 0;

    color = vec4(SunData.Color[i],1.0);

    closestDistance = sdRotatedBox(ro, pos, rot, dims);

    float stepDistance = .0;

    for(int j = 0; j < MAX_STEPS && total < MAX_DIST; j++) {

        //        if (total >= depth) return false;

        stepDistance = sdRotatedBox(ro + rd * total, pos, rot, dims);

        if (stepDistance < closestDistance) closestDistance = stepDistance;

        glow += getGlow(stepDistance, 0.0, 0.0);

        if(stepDistance <= 1e-5 || glow >= 3) {
            color *= min(glow,3.0);
            return true;
        }

        total += stepDistance;
    }
    color *= min(glow,3.0);
    return false;
}

bool calculate(vec3 ro, vec3 rd, in float depth, out float closestDistance, out int closestHit, out vec4 finalColor) {

    if (SunData.Size <= 0) return false;

    bool finalHit = false;

    finalColor = vec4(0.0);

    closestHit = -1;
    closestDistance = -1;

    for (int i = 0; i < SunData.Size; i++) {

        float dist = 0;

        vec4 color = vec4(0.0);

        bool hit = raymarch(ro, rd, i, depth, closestDistance, color);

        if(closestDistance > dist || closestDistance < 0) {
            closestDistance = dist;
            closestHit = i;
        }

        finalHit = hit || finalHit;

        finalColor += color;

    }

    return finalHit;
}

void main() {

    fragColor = texture(DiffuseSampler0, texCoord);
    float depth = depthSampleToWorldDepth(texture(DiffuseDepthSampler, texCoord).r);
    vec3 camera = VeilCamera.CameraPosition;
    vec3 rd = viewDirFromUv(texCoord);
//
    vec4 color = vec4(0.0);
    float closestDistance = 0.0;
    int closestHit = 0;
    bool hit = calculate(camera, rd, depth, closestDistance, closestHit, color);

    acesToneMapping(color.xyz);
//    if (hit) {
//        fragColor = vec4((depthSampleToWorldDepth(depth)+1.001)*100.0);
////        fragColor = vec4(LightData.color[closestHit],1);
////        fragColor += color;
//    } else {
////        acesToneMapping(color.xyz);
////        fragColor += color;
//    }

//    fragColor = vec4(depth);

    color.a = clamp(color.a,0.0,1.0);

    fragColor = fragColor*(1-color.a) + color;

//    fragColor = vec4(-depth)*0.1;
//
//    fragColor = fragColor + vec4(closestDistance);

//    fragColor = vec4(LightData.dataSize);


}