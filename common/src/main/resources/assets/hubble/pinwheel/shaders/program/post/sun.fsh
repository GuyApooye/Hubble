uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D NoiseSampler;

#define MAX_STEPS 200
#define MAX_DIST 1000000.0

in vec2 texCoord;

out vec4 fragColor;

float getGlow(in float dist, float size) {
    return pow(size/(250.0*dist), 0.9);
}

float sdBox(in vec3 p, in vec3 b) {
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

float worldDepthToDepthSample(in float worldDepth) {
    return 0.5-0.5*(2*VeilCamera.NearPlane*VeilCamera.FarPlane/worldDepth-VeilCamera.FarPlane-VeilCamera.NearPlane)/(VeilCamera.FarPlane-VeilCamera.NearPlane);
}

float raymarch(in vec3 ro, in vec3 rd, in int i, in float depth, out float distTraveled) {

    distTraveled = 0.0;

    vec3 dims = SunData.Dims[i];
    float size = SunData.Size[i];

    float glow = 0;

    float stepDistance = .0;

    for(int j = 0; j < MAX_STEPS && distTraveled < MAX_DIST; ++j) {

        if (distTraveled >= depth) break;

        stepDistance = sdBox(ro + rd * distTraveled, dims);

        glow += getGlow(stepDistance, size);

        if(stepDistance <= 1e-5 || glow >= 3) break;

        distTraveled += stepDistance;
    }

    return glow;
}

bool raytrace(in vec3 ro, in vec3 rd, in int i, in int j, in float depth, out float dist, out vec4 color) {

    float near = 0.0;

    bool hit = iBox(ro, rd, SunData.Dims[i]*(1.0-0.12*j), near);

    if (near >= depth) return false;

    if (!hit) return false;

    dist = near;
    color = vec4(SunData.Color[i], 1.0) * ((j/8.0)+1) * 3.0;
    return true;

}

bool raytrace(in vec3 ro, in vec3 rd, in int i, in float depth, out float dist, out vec4 color) {

    for (int j = 3; j >= 1; --j) {
       if (raytrace(ro, rd, i, j, depth, dist, color)) return true;
    }

    return false;

}

bool calculate(in vec3 ro, in vec3 rd, inout float depth, out vec4 hitColor, out vec4 glowColor) {

    if (SunData.Length <= 0) return false;

    glowColor = vec4(0.0);
    hitColor = vec4(0.0);

    int hitIndex = -1;

    for (int i = 0; i < SunData.Length; ++i) {

        float dist = 0;

        vec4 color = vec4(0.0);

        vec3 tRo = (vec4(ro - SunData.Pos[i],1.0) * SunData.Rot[i]).xyz;
        vec3 tRd = (vec4(rd,0.0) * SunData.Rot[i]).xyz;

        bool hit = raytrace(tRo, tRd, i, depth, dist, color);

        if (hit) {
            if (depth > dist) {
                hitColor = color;
                hitIndex = i;
                depth = dist;
            }
            continue;
        }

    }

    for (int i = 0; i < SunData.Length; ++i) {

        if (i == hitIndex) continue;

        float distTraveled = 0.0;

        vec3 tRo = (vec4(ro - SunData.Pos[i],1.0) * SunData.Rot[i]).xyz;
        vec3 tRd = (vec4(rd,0.0) * SunData.Rot[i]).xyz;

        float glow = raymarch(tRo, tRd, i, depth, distTraveled);

        if (glow >= 3 && distTraveled <= depth) {
            hitIndex = i;
            depth = distTraveled;
            hitColor = vec4(SunData.Color[i]*min(glow,3.0),1.0);
            continue;
        }

        glowColor += vec4(SunData.Color[i],1.0) * min(glow,3.0);

    }

    return hitIndex != -1;
}

void main() {

    fragColor = texture(DiffuseSampler0, texCoord);
    gl_FragDepth = texture(DiffuseDepthSampler, texCoord).r;

    float depth = depthSampleToWorldDepth(gl_FragDepth);

    if ((depth+0.1) >= VeilCamera.FarPlane) depth = MAX_DIST;

    vec3 camera = VeilCamera.CameraPosition;
    vec3 rd = viewDirFromUv(texCoord);

    float noise = texture(NoiseSampler, 10.0*texCoord).r;
    noise = (noise - 0.5) * .1;

    vec4 glowColor = vec4(0.0);

    vec4 color = vec4(0.0);

    bool hit = calculate(camera, rd, depth, color, glowColor);

    if (hit) {
        fragColor = color;
        noise = 0;
        gl_FragDepth = worldDepthToDepthSample(depth);
    }

    glowColor.xyz *= 1+noise;

    glowColor.a = clamp(glowColor.a,0.0,1.0);

    fragColor = fragColor*(1.0-glowColor.a/3.0) + glowColor;

}