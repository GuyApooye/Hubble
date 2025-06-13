uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D NoiseSampler;
//layout (binding = 0) uniform sampler2DArray Textures;
uniform sampler2D PlanetTexture;

#define MAX_DIST 1000000.0

in vec2 texCoord;

out vec4 fragColor;

float sdBox(in vec3 p, in vec3 b) {
    vec3 q = abs(p) - b;
    return length(max(q,0.0)) - min(max(q.x,max(q.z,q.y)),0.0);
}

float sdRotatedBox(in vec3 ro, in vec3 center, in vec3 dims, in mat4 mat) {
    return sdBox((vec4(ro - center, 1.0) * mat).xyz, dims);
}

bool iBox(in vec3 ro, in vec3 rd, in vec3 boxSize, out float tN, out float tF, out vec3 normal) {
    vec3 m = 1.0/rd;
    vec3 n = m*ro;
    vec3 k = abs(m)*boxSize;
    vec3 t1 = -n - k;
    vec3 t2 = -n + k;
    tN = max( max( t1.x, t1.y ), t1.z );
    tF = min( min( t2.x, t2.y ), t2.z );
    if( tN>tF || tF<0.0) return false;
    if (!(tN>0.0)) tN = 0.0;
    normal = (tN>0.0) ? step(vec3(tN),t1) /* ro ouside the box */
                           :
                           step(t2,vec3(tF)); /* ro inside the box */
    normal *= -sign(rd);
    return true;
}

bool iBox(in vec3 ro, in vec3 rd, in vec3 boxSize, out float tN, out float tF, out vec3 normal, out vec2 uv) {

    bool hit = iBox(ro, rd, boxSize, tN, tF, normal);

    if (!hit) return false;
    const float e = 0.0001;
    vec3 i = ro+tN*rd;

    uv = vec2(0.0);
    if ((i.z-e) <= -boxSize.z) uv = vec2(-i.x/boxSize.x+3.0, -i.y/boxSize.y+3.0)/8.0;
    else if ((i.z+e) >= boxSize.z) uv = vec2(i.x/boxSize.x+7.0, -i.y/boxSize.y+3.0)/8.0;
    else if ((i.x-e) <= -boxSize.x) uv = vec2(i.z/boxSize.z+5.0, -i.y/boxSize.y+3.0)/8.0;
    else if ((i.x+e) >= boxSize.x) uv = vec2(-i.z/boxSize.z+1.0, -i.y/boxSize.y+3.0)/8.0;
    else if ((i.y-e) <= -boxSize.y) uv = vec2(-i.x/boxSize.x+3.0, i.z/boxSize.z+5.0)/8.0;
    else if ((i.y+e) >= boxSize.y) uv = vec2(-i.x/boxSize.x+3.0, -i.z/boxSize.z+1.0)/8.0;

    return true;
}

bool iRotatedBox(in vec3 ro, in vec3 rd, in vec3 center, in vec3 dims, in mat4 mat, out float near, out float far, out vec3 normal) {
    return iBox((vec4(ro - center, 1.0) * mat).xyz, (vec4(rd, 0.0) * mat).xyz, dims, near, far, normal);
}

bool iRotatedBox(in vec3 ro, in vec3 rd, in vec3 center, in vec3 dims, in mat4 mat, out float near, out float far, out vec3 normal, out vec2 uv) {
    return iBox((vec4(ro - center, 1.0) * mat).xyz, (vec4(rd, 0.0) * mat).xyz, dims, near, far, normal, uv);
}

float depthSampleToWorldDepth(in float depthSample) {
    float f = depthSample * 2.0 - 1.0;
    return 2.0 * VeilCamera.NearPlane * VeilCamera.FarPlane / (VeilCamera.FarPlane + VeilCamera.NearPlane - f * (VeilCamera.FarPlane - VeilCamera.NearPlane));
}

float worldDepthToDepthSample(in float worldDepth) {
    return 0.5-0.5*(2*VeilCamera.NearPlane*VeilCamera.FarPlane/worldDepth-VeilCamera.FarPlane-VeilCamera.NearPlane)/(VeilCamera.FarPlane-VeilCamera.NearPlane);
}

bool raytrace(in vec3 ro, in vec3 rd, in int i, in float depth, out float dist, out vec4 color) {

    float near = 0.0;
    float far = 0.0;
    vec2 uv = vec2(0.0);

    vec3 normal = vec3(0.0);

    bool hit = iRotatedBox(ro, rd, PlanetData.Pos[i], PlanetData.Dims[i], PlanetData.Rot[i], near, far, normal, uv);

    if (!hit) return false;
    if (near >= depth) return false;


    dist = near;


    float light = 0.0;

    normal = normalize(ro+near*rd-PlanetData.Pos[i]);

    for (int i = 0; i < SunData.Size; ++i) {
        vec3 diff = ro-SunData.Pos[i];
        float distSquared = diff.x*diff.x+diff.y*diff.y+diff.z*diff.z;
//        float mul = dot(normal, normalize(ro-SunData.Pos[i]));
        light -= min(SunData.Intensity[i]*dot(normal, normalize(ro-SunData.Pos[i]))/distSquared,0.0);
    }

    color = texture(PlanetTexture, uv)*light;


    return true;

}

bool calculate(in vec3 ro, in vec3 rd, inout float depth, out vec4 hitColor, out vec4 glowColor) {

    if (PlanetData.Size <= 0) return false;

    glowColor = vec4(0.0);
    hitColor = vec4(0.0);

    int hitIndex = -1;

    for (int i = 0; i < PlanetData.Size; ++i) {

        float dist = 0;

        vec4 color = vec4(0.0);

        bool hit = raytrace(ro, rd, i, depth, dist, color);

        if (hit) {
            if (depth > dist) {
                hitColor = color;
                hitIndex = i;
                depth = dist;
            }
            continue;
        }

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

    vec4 hitColor = vec4(0.0);
    vec4 glowColor = vec4(0.0);
    if (calculate(camera, rd, depth, hitColor, glowColor)) {
        fragColor = hitColor;
        gl_FragDepth = worldDepthToDepthSample(depth);
    }

}