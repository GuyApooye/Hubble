uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;

#define MAX_DIST 10000.0

in vec2 texCoord;
in float fragDist;

out vec4 fragColor;

float getGlow(in float dist, in float size, in float intensity){
    return exp(8.0*dist);
}

float manhattan(in vec3 pos) {
    pos = abs(pos);
    return pos.x + pos.y + pos.z;
}

float sdBox(in vec3 p, in vec3 b ) {
    vec3 q = abs(p) - b;
    return length(max(q,0.0)) - min(max(q.x,max(q.z,q.y)),0.0);
}

float sdSphere( vec3 p, vec3 s )
{
    return length(p)-s.x;
}

float sdBoxx(in vec3 p, in vec3 b ) {
    vec3 q = abs(p) - b;

    return max(q.x,max(q.y,q.z));
}

bool iSphere( in vec3 ro, in vec3 rd, vec3 ra, out vec3 outNormal, out float tN, out float tF)
{
    float b = dot( ro, rd );
    float c = dot( ro, ro ) - ra.x*ra.x;
    float h = b*b - c;
    if( h<0.0 ) return false; // no intersection
    h = sqrt( h );
    tN = -b-h;
    tF = -b+h;
    if (!(tN>0.0)) {
//        tN = tF;
        tN = 0;
    }
    outNormal = vec3(0.0);
    return true;
}

bool iBox( in vec3 ro, in vec3 rd, vec3 boxSize, out vec3 outNormal, out float tN, out float tF) {
    vec3 m = 1.0/rd;
    vec3 n = m*ro;
    vec3 k = abs(m)*boxSize;
    vec3 t1 = -n - k;
    vec3 t2 = -n + k;
    tN = max( max( t1.x, t1.y ), t1.z );
    tF = min( min( t2.x, t2.y ), t2.z );
    if( tN>tF || tF<0.0) return false; // no intersection
    outNormal = (tN>0.0) ? step(vec3(tN),t1) : // ro ouside the box
    step(t2,vec3(tF));  // ro inside the box
    outNormal *= -sign(rd);

    if (!(tN>0.0)) {
//        tN = tF;
        tN = 0;
    }

    return true;
}

float lerp(in float a, in float b, in float t) {
    return a+t*(b-a);
}

vec4 calculateColor(in vec3 ro, in vec3 rd, in float depth, in int i, out bool asd) {
    ro = (vec4(ro - SunData.Pos[i], 1.0) * SunData.Rot[i]).xyz;
    rd = (vec4(rd, 0.0) * SunData.Rot[i]).xyz;

    float tN0 = 0;
    float tN1 = 0;
    float tF0 = 0;
    float tF1 = 0;

    vec3 normal0 = vec3(0.0);
    vec3 normal1 = vec3(0.0);

    if (!iBox(ro, rd, 10.0*SunData.Dims[i], normal0, tN0, tF0)) return vec4(0.0);

    vec4 color = vec4(SunData.Color[i],1.0);

    if (iBox(ro, rd, SunData.Dims[i], normal1, tN1, tF1)) {
        asd = true;
        if (tN1 < tN0) tF0 = tN1;
//        return 2*color;
    } else asd = false;

    float glow = 0.0;

    for (int j = 0; j <= 10; ++j) {
        float t = lerp(tN0, tF0, 0.1*j+0.1);
        vec3 dims = SunData.Dims[i];
        float dist = sdBox(t*rd+ro, dims);
        dims *= 10;
        glow += getGlow(dist, SunData.Length[i], SunData.Intensity[i]) * clamp(min(min(dims.x,dims.y),dims.z) - dist,0.0,1.0);
    }

    glow = min(glow,2.0);

    return color *= glow;


}

vec4 calculateFinalColor(in vec3 ro, in vec3 rd, in float depth, out bool asd) {
    vec4 color = vec4(0.0);

    for (int i = 0; i < SunData.Size; ++i) {
        color += calculateColor(ro, rd, depth, i, asd);
    }

    return color;
}

void main() {

    fragColor = texture(DiffuseSampler0, texCoord);
    float depth = texture(DiffuseDepthSampler, texCoord).r;
    vec3 camera = VeilCamera.CameraPosition;
    vec3 rd = viewDirFromUv(texCoord);

    bool asd = false;

    vec4 color = calculateFinalColor(camera, rd, depth, asd);

    color.a = clamp(color.a, 0.0, 1.0);

    fragColor = fragColor*(1 - color.a) + color;

    if (asd) {
//        fragColor = vec4(1.0);
        return;
    }

}