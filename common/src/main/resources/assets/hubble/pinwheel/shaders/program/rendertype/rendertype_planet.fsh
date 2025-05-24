#define MAX_STEPS 200
#define MAX_DIST 10000.0
#define SOURCE_SIZE 0.5

uniform vec4 ColorModulator;
uniform sampler2D Sampler0;

in float vertexDistance;
in vec4 vertexColor;
in vec2 texCoord0;
in vec3 fragPos;
in vec3 fragNormal;

out vec4 fragColor;

bool boxIntersect(in vec3 ro, in vec3 rd, vec3 boxSize, out vec3 normal) {
    vec3 m = 1.0/rd; // can precompute if traversing a set of aligned boxes
    vec3 n = m*ro;   // can precompute if traversing a set of aligned boxes
    vec3 k = abs(m)*boxSize;
    vec3 t1 = -n - k;
    vec3 t2 = -n + k;
    float tN = max( max( t1.x, t1.y ), t1.z );
    float tF = min( min( t2.x, t2.y ), t2.z );
    if( tN>tF || tF<0.0) return false; // no intersection
    normal = (tN>0.0) ? step(vec3(tN),t1) : // ro ouside the box
                           step(t2,vec3(tF));  // ro inside the box
    normal *= -sign(rd);
    return true;
}

bool rotatedBoxIntersect(vec3 pos, vec3 dir, vec3 center, vec3 dims, mat4 mat, out vec3 normal) {
    vec4 newPos = vec4(pos - center, 1.0) * mat;
    vec4 newDir = vec4(dir, 1.0) * mat;
    bool hit = boxIntersect(newPos.xyz, newDir.xyz, dims, normal);
    vec4 newNormal = vec4(normal, 1.0) * mat;
    normal = newNormal.xyz;
    return hit;
}

float sdBox( vec3 p, vec3 b ) {
    vec3 q = abs(p) - b;
    return length(max(q,0.0)) + min(max(q.x,max(q.y,q.z)),0.0);
}

float sdRotatedBox(vec3 pos, vec3 center, vec3 dims, mat4 mat) {
    vec4 newPos = vec4(pos - center, 1.0) * mat;
//    vec4 newDir = vec4(dir, 1.0) * mat;
    return sdBox(newPos.xyz, dims);
}

bool sceneIntersect(vec3 pos, vec3 dir, out vec3 normal) {
    if (PlanetData.Size <= 0) return false;
    for (int i = 0; i < PlanetData.Size; i++) {
        if(rotatedBoxIntersect(pos, dir, PlanetData.Pos[i], PlanetData.Dims[i], PlanetData.Rot[i], normal)) return true;
    }
    return false;
}

float sceneSDF(vec3 pos) {
    if (PlanetData.Size <= 0) return -1.0;
    float minDist = -1.0;
    for (int i = 0; i < PlanetData.Size; i++) {
        float dist = sdRotatedBox(pos, PlanetData.Pos[i], PlanetData.Dims[i], PlanetData.Rot[i]);
        minDist = min(minDist, dist);
    }
    return minDist;
}

bool lightIntersect(vec3 pos, vec3 dir, out vec3 normal) {
    if (SunData.Size <= 0) return false;
    for (int i = 0; i < SunData.Size; i++) {
        if(rotatedBoxIntersect(pos, dir, SunData.Pos[i], SunData.Dims[i], SunData.Rot[i], normal)) return false;
    }
    return true;
}

float raymarchLight(vec3 ro, vec3 lo, float intensity) {

    float rayLength = length(lo - ro);
    vec3 rd = (lo - ro) / rayLength;

    float res = 1.0;

    float total = 0.0;

    float stepDistance = .0;

    float ph = 1e20;
    for(int i = 0; i < MAX_STEPS && total < MAX_DIST; i++) {

        //Previous:
        stepDistance = sceneSDF(ro + rd * total);

        //stepDistance = min(sceneSDF(ro + rd * total), length(lo - (ro + rd * total)));


        if(stepDistance < 0.0001) {
            return 0.0;
        }

        stepDistance /= 3.0;

        float y = stepDistance*stepDistance/(2.0*ph);
        float d = sqrt(stepDistance*stepDistance-y*y);

        res = min(res, d/(SOURCE_SIZE*max(0.0,total-y)));

        ph = stepDistance;
        total += stepDistance;
    }

    return max(res * intensity / rayLength ,0.0) ;
}

float calculateLight(vec3 fragPos) {
    if (SunData.Size <= 0) return 0.0;
    float light = 0;
    for (int i = 0; i < SunData.Size; i++) {
        vec3 dir = SunData.Pos[i] - fragPos;
        float dist = length(dir);
        vec3 normal = vec3(1.0);
//        dir /= dist;
        if (sceneIntersect(fragPos + 0.01*dir, dir, normal)) continue;
//        float light = raymarchLight(fragPos, SunData.Pos[i], SunData.Intensity[i]);
//        finalLight += light;
//        finalColor += vec4(SunData.Color[i],1.0) * light;
        light += SunData.Intensity[i]*SunData.Length[i] / (dist * dist);
//        light += fragNormal.y;
//        finalColor += SunData.Color[i] * light;
    }
    return light;
}

void main() {
    float light = calculateLight(fragPos);
//    vec4 color = vec4(0.0);

//    color = ((texture(Sampler0, texCoord0) * light + vec4(color,1.0)) * ColorModulator).xyz;
//    acesToneMapping(color);
//    fragColor = vec4(color,1.0);
    fragColor = vec4(fragNormal,1.0);
//    fragColor = vec4(fragNormal,1.0);
//    fragColor *= ColorModulator;
//    fragColor = vec4(fragNormal*1000,1.0);
}
