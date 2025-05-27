uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D NoiseSampler;

#define MAX_STEPS 200
#define MAX_DIST 10000.0

in vec2 texCoord;
in float fragDist;

out vec4 fragColor;

float getGlow(in float dist, float size, float intensity){
    return pow(size/(250.0*dist), 0.8);
}

float sdBox(in vec3 p, in vec3 b ) {
    vec3 q = abs(p) - b;
    return length(max(q,0.0)) - min(max(q.x,max(q.z,q.y)),0.0);
}

float sdRotatedBox(in vec3 ro, in vec3 center, in vec3 dims, in mat4 mat) {
    return sdBox((vec4(ro - center, 1.0) * mat).xyz, dims);
}

bool iBox( in vec3 ro, in vec3 rd, in vec3 boxSize, out float tN) {
    vec3 m = 1.0/rd;
    vec3 n = m*ro;
    vec3 k = abs(m)*boxSize;
    vec3 t1 = -n - k;
    vec3 t2 = -n + k;
    tN = max( max( t1.x, t1.y ), t1.z );
    float tF = min( min( t2.x, t2.y ), t2.z );
    if( tN>tF || tF<0.0) return false;

    if (!(tN>0.0)) tN = 0.0;

    return true;
}

bool iRotatedBox(in vec3 ro, in vec3 rd, in vec3 center, in vec3 dims, in mat4 mat, out float near) {
    return iBox((vec4(ro - center, 1.0) * mat).xyz, (vec4(rd, 0.0) * mat).xyz, dims, near);
}

float depthSampleToWorldDepth(in float depthSample) {
    float f = depthSample * 2.0 - 1.0;
    return 2.0 * VeilCamera.NearPlane * VeilCamera.FarPlane / (VeilCamera.FarPlane + VeilCamera.NearPlane - f * (VeilCamera.FarPlane - VeilCamera.NearPlane));
}

vec4 raymarch(in vec3 ro, in vec3 rd, in int i, in float depth) {

    float total = 0.0;

    vec3 pos = SunData.Pos[i];
    vec3 dims = SunData.Dims[i];
    mat4 rot = SunData.Rot[i];
    float size = SunData.Length[i];
    float intensity = SunData.Intensity[i];

    float glow = 0;

    float stepDistance = .0;

    for(int j = 0; j < MAX_STEPS && total < MAX_DIST; ++j) {

        if (total >= depth) return vec4(0.0);

        stepDistance = sdRotatedBox(ro + rd * total, pos, dims, rot);

        glow += getGlow(stepDistance, size, intensity);

        if(stepDistance <= 1e-5 || glow >= 3) return vec4(SunData.Color[i],1.0)*min(glow,3.0);

        total += stepDistance;
    }

    return vec4(SunData.Color[i],1.0) * min(glow,3.0);
}

bool raytrace(in vec3 ro, in vec3 rd, in int i, in int j, in float depth, out float dist, out vec4 color) {

    float near = 0.0;

    bool hit = iRotatedBox(ro, rd, SunData.Pos[i], SunData.Dims[i]*(1.0-0.2*j), SunData.Rot[i], near);

    if (near >= depth) return false;

    if (!hit) return false;

    dist = near;
    color = vec4(SunData.Color[i], 1.0) * /**sqrt(j+1) **/ 5.0;
    return true;

}

bool raytrace(in vec3 ro, in vec3 rd, in int i, in float depth, out float dist, out vec4 color) {

    for (int j = 0; j <= 4; ++j) {
       if (raytrace(ro, rd, i, 4-j, depth, dist, color)) return true;
    }

    return false;

}

vec4 calculate(in vec3 ro, in vec3 rd, in float noise, in float depth) {

    if (SunData.Size <= 0) return vec4(0.0);

    vec4 finalColor = vec4(0.0);

    vec4 traceColor = vec4(0.0);

    float closestDistance = 100000.0;

    for (int i = 0; i < SunData.Size; ++i) {

        float dist = 0;

        vec4 color = vec4(0.0);

        bool hit = raytrace(ro, rd, i, closestDistance, dist, color);

        if (hit) {
            if (closestDistance > dist) {
                traceColor = color;
                closestDistance = dist;
            }
            continue;
        }

        color = raymarch(ro, rd, i, closestDistance);

        color.xyz *= noise;

        finalColor += color*color.a;

    }

    finalColor += traceColor;

    return finalColor;
}

void main() {

    fragColor = texture(DiffuseSampler0, texCoord);
    float depth = texture(DiffuseDepthSampler, texCoord).r;
    vec3 camera = VeilCamera.CameraPosition;
    vec3 rd = viewDirFromUv(texCoord);

    float noise = texture(NoiseSampler, 10.0*texCoord).r;
    noise = 1 + (noise - 0.5) * .2;


    vec4 color = calculate(camera, rd, noise, depth);

    color.a = clamp(color.a,0.0,1.0);

    fragColor = fragColor * (1-color.a) + color;

}