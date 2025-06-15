uniform sampler2D DiffuseSampler0;
uniform sampler2D DiffuseDepthSampler;
uniform sampler2D NoiseSampler;
//layout (binding = 0) uniform sampler2DArray Textures;
uniform sampler2D PlanetTexture;

#define MAX_DIST 1000000.0

in vec2 texCoord;

out vec4 fragColor;

const float e = 0.001;

float sdBox(in vec3 p, in vec3 b) {
    vec3 q = abs(p) - b;
    return max(length(max(q,0.0)),0.0);
}

//float sdRotatedBox(in vec3 ro, in vec3 center, in vec3 dims, in mat4 mat) {
//    return sdBox((vec4(ro - center, 1.0) * mat).xyz, dims);
//}

bool iSphere( in vec3 ro, in vec3 rd, in float ra, out float tN, out float tF)
{
    vec3 oc = ro;
    float b = dot( oc, rd );
    float c = dot( oc, oc ) - ra*ra;
    float h = b*b - c;
    if( h<0.0 ) return false; // no intersection
    h = sqrt( h );
    tN = -b-h;
    tF = -b+h;
//    if (!(tN>0.0)) tN = 0.0;
    return true;
}

bool iRoundedBox( in vec3 ro, in vec3 rd, in vec3 size, in float rad, out float tN, out float tF)
{
    // bounding box
    vec3 m = 1.0/rd;
    vec3 n = m*ro;
    vec3 k = abs(m)*(size+rad);
    vec3 t1 = -n - k;
    vec3 t2 = -n + k;
    tN = max( max( t1.x, t1.y ), t1.z );
    tF = min( min( t2.x, t2.y ), t2.z );
    if( tN>tF || tF<0.0) return false;
    float t = tN;

    // convert to first octant
    vec3 pos = ro+t*rd;
    vec3 s = sign(pos);
    ro  *= s;
    rd  *= s;
    pos *= s;

    // faces
    pos -= size;
    pos = max( pos.xyz, pos.yzx );
    if( min(min(pos.x,pos.y),pos.z) < 0.0 ) {
        if (!(tN>0.0)) tN = 0.0;
        return true;
    }

    // some precomputation
    vec3 oc = ro - size;
    vec3 dd = rd*rd;
    vec3 oo = oc*oc;
    vec3 od = oc*rd;
    float ra2 = rad*rad;

    t = 1e20;

    // corner
    float b = od.x + od.y + od.z;
    float c = oo.x + oo.y + oo.z - ra2;
    float h = b*b - c;
    if( h>0.0 ) t = -b-sqrt(h);
    // edge X
    float a = dd.y + dd.z;
    b = od.y + od.z;
    c = oo.y + oo.z - ra2;
    h = b*b - a*c;
    if( h>0.0 )
    {
        h = (-b-sqrt(h))/a;
        if( h>0.0 && h<t && abs(ro.x+rd.x*h)<size.x ) t = h;
    }
    // edge Y
    a = dd.z + dd.x;
    b = od.z + od.x;
    c = oo.z + oo.x - ra2;
    h = b*b - a*c;
    if( h>0.0 )
    {
        h = (-b-sqrt(h))/a;
        if( h>0.0 && h<t && abs(ro.y+rd.y*h)<size.y ) t = h;
    }
    // edge Z
    a = dd.x + dd.y;
    b = od.x + od.y;
    c = oo.x + oo.y - ra2;
    h = b*b - a*c;
    if( h>0.0 )
    {
        h = (-b-sqrt(h))/a;
        if( h>0.0 && h<t && abs(ro.z+rd.z*h)<size.z ) t = h;
    }

    if( t>1e19 ) return false;

    if (!(tN>0.0)) tN = 0.0;
    return true;
}

bool iBox(in vec3 ro, in vec3 rd, in vec3 boxSize, out float tN, out float tF) {
    vec3 m = 1.0/rd;
    vec3 n = m*ro;
    vec3 k = abs(m)*boxSize;
    vec3 t1 = -n - k;
    vec3 t2 = -n + k;
    tN = max( max( t1.x, t1.y ), t1.z );
    tF = min( min( t2.x, t2.y ), t2.z );
    if( tN>tF || tF<0.0) return false;
    if (!(tN>0.0)) tN = 0.0;
    return true;
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

bool iBox(in vec3 ro, in vec3 rd, in vec3 boxSize, out float tN, out float tF, out vec2 uv) {

    bool hit = iBox(ro, rd, boxSize, tN, tF);

    if (!hit) return false;
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

//bool iRotatedBox(in vec3 ro, in vec3 rd, in vec3 center, in vec3 dims, in mat4 mat, out float near, out float far, out vec3 normal) {
//    return iBox((vec4(ro - center, 1.0) * mat).xyz, (vec4(rd, 0.0) * mat).xyz, dims, near, far, normal);
//}
//
//bool iRotatedBox(in vec3 ro, in vec3 rd, in vec3 center, in vec3 dims, in mat4 mat, out float near, out float far, out vec3 normal, out vec2 uv) {
//    return iBox((vec4(ro - center, 1.0) * mat).xyz, (vec4(rd, 0.0) * mat).xyz, dims, near, far, normal, uv);
//}

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

    bool hit = iBox(ro, rd, PlanetData.Dims[i], near, far, uv);

    if (!hit) return false;
    if (near >= depth) return false;


    dist = near;


//    float light = 0.0;
//
//    normal = normalize(ro+near*rd);
//
//    if (SunData.Length <= 0) return true;
//
//    for (int i = 0; i < SunData.Length; ++i) {
//        vec3 diff = ro-SunData.Pos[i];
//        float distSquared = diff.x*diff.x+diff.y*diff.y+diff.z*diff.z;
////        float mul = dot(normal, normalize(ro-SunData.Pos[i]));
//        vec3 dirToSun = normalize(PlanetData.Pos[i]-SunData.Pos[i]);
//        dirToSun = (vec4(dirToSun, 0.0)*PlanetData.Rot[i]).xyz;
//        light -= min(SunData.Intensity[i]*dot(normal, dirToSun)/distSquared,0.0);
//    }

    color = texture(PlanetTexture, uv)*1.0;


    return true;

}

/*
* Thank you to our lord and savior:
*   Sebastion Lague
* <3
*/

float densityAtPoint(in vec3 densitySamplePoint, in int i) {
    float heightAboveSurface = sdBox(densitySamplePoint, PlanetData.Dims[i]);
    float height01 = clamp(heightAboveSurface / (AtmosphereData.Scale[i]*PlanetData.Size[i]),0.0,1.0);
    float localDensity = exp(-height01 * AtmosphereData.DensityFalloff[i]) * (1.0 - height01);
    return localDensity;
}

//TODO: implement a baked optical depth function fro better performance
float opticalDepth(in vec3 ro, in vec3 rd, in float rl, in int i) {
    vec3 densitySamplePoint = ro;
    float stepSize = rl / (AtmosphereData.OpticalDepthPoints - 1);
    float opticalDepth = 0.0;

    for (int k = 0; k < AtmosphereData.OpticalDepthPoints; ++k) {
        float localDensity = densityAtPoint(densitySamplePoint, i);
        opticalDepth += localDensity * stepSize;
        densitySamplePoint += rd * stepSize;
    }

    return opticalDepth;
}

vec4 calculateLight(vec3 rayOrigin, vec3 rayDir, vec3 dirToSun, float rayLength, inout float light, float noise, int i) {

    vec3 inScatterPoint = rayOrigin;
    float stepSize = rayLength / (AtmosphereData.InScatteringPoints - 1);
    vec4 inScatteredLight = vec4(0.0);
    float viewRayOpticalDepth = 0.0;

    for (int j = 0; j < AtmosphereData.InScatteringPoints; ++j) {

        float near = 0.0;
        float far = 0.0;

//        if (!iBox(inScatterPoint, dirToSun, PlanetData.Dims[i]*(1.0+AtmosphereData.Scale[i]), near, far)) continue;
        iBox(inScatterPoint, dirToSun, PlanetData.Dims[i]*(1.0+AtmosphereData.Scale[i]+e), near, far);

        float sunRayLength = far-near-e;
        float sunRayOpticalDepth = opticalDepth(inScatterPoint, dirToSun, sunRayLength, i);
        float localDensity = densityAtPoint(inScatterPoint, i);
        viewRayOpticalDepth = opticalDepth(rayOrigin, rayDir, stepSize * j, i);
        vec4 transmittance = exp(-(sunRayOpticalDepth + viewRayOpticalDepth) * vec4(AtmosphereData.ScatteringCoefficients[i],1.0));

        inScatteredLight += localDensity * transmittance;
        inScatterPoint += rayDir * stepSize;
    }
    inScatteredLight *= vec4(AtmosphereData.ScatteringCoefficients[i],1.0) * AtmosphereData.Strength[i] * stepSize;
    inScatteredLight *= 1.0+noise;

/**    // Attenuate brightness of original col (i.e light reflected from planet surfaces)
    // This is a hacky mess, TO//DO: figure out a proper way to do this
    const float brightnessAdaptionStrength = 0.15;
    const float reflectedLightOutScatterStrength = 3;
    float brightnessAdaption = dot (inScatteredLight,1) * brightnessAdaptionStrength;
    float brightnessSum = viewRayOpticalDepth * 1 * reflectedLightOutScatterStrength + brightnessAdaption;
    float reflectedLightStrength = exp(-brightnessSum);
    float hdrStrength = saturate(dot(originalCol,1)/3-1);
    reflectedLightStrength = lerp(reflectedLightStrength, 1, hdrStrength);
    vec3 reflectedLight = originalCol * reflectedLightStrength;

    vec3 finalCol = reflectedLight + inScatteredLight;*/

    light += inScatteredLight.a;

    return inScatteredLight;
}

bool calculate(in vec3 ro, in vec3 rd, in float noise, inout float depth, out vec4 hitColor, out vec4 glowColor) {

    if (PlanetData.Length <= 0) return false;

    glowColor = vec4(0.0);
    hitColor = vec4(0.0);

    int hitIndex = -1;

    for (int i = 0; i < PlanetData.Length; ++i) {

        float dist = 0;

        vec4 color = vec4(0.0);

        vec3 tRo = (vec4(ro - PlanetData.Pos[i],1.0) * PlanetData.Rot[i]).xyz;
        vec3 tRd = (vec4(rd,0.0) * PlanetData.Rot[i]).xyz;

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

    for (int i = 0; i < PlanetData.Length; ++i) {

        float near = 0.0;
        float far = 0.0;

        vec3 tRo = (vec4(ro - PlanetData.Pos[i],1.0) * PlanetData.Rot[i]).xyz;
        vec3 tRd = (vec4(rd,0.0) * PlanetData.Rot[i]).xyz;

        bool hit = iBox(tRo, tRd, PlanetData.Dims[i]*(1.0+AtmosphereData.Scale[i]), near, far);
//        bool hit = iSphere(tRo, tRd, 12.0, near, far);

        if (!hit) continue;
        if (near >= depth) continue;

        float distThroughAtmosphere = min(far-near, depth-near);

        if (distThroughAtmosphere > 0) {
            vec3 pointInAtmosphere = tRo + tRd * (near + e);
            float light = 0;
            for (int j = 0; j < SunData.Length; ++j) {
                glowColor += calculateLight(pointInAtmosphere, tRd, (vec4(normalize(SunData.Pos[j] - PlanetData.Pos[i]),0.0)*PlanetData.Rot[i]).xyz, (distThroughAtmosphere - e), light, noise, i);
            }
            hitColor *= light;
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
    if (calculate(camera, rd, noise, depth, hitColor, glowColor)) {
        fragColor = hitColor + glowColor;
        gl_FragDepth = worldDepthToDepthSample(depth);
    } else {
        glowColor.a = clamp(glowColor.a,0.0,1.0);

        fragColor = fragColor * (1-glowColor.a) + glowColor;
    }

}