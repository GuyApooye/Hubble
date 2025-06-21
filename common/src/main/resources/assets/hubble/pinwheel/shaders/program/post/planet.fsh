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

vec2 raySphere(float sphereRadius, vec3 rayOrigin, vec3 rayDir) {
    vec3 offset = rayOrigin;
    float a = 1.0; // Set to dot(rayDir, rayDir) if rayDir might not be normalized
    float b = 2.0 * dot(offset, rayDir);
    float c = dot (offset, offset) - sphereRadius * sphereRadius;
    float d = b * b - 4.0 * a * c; // Discriminant from quadratic formula

    // Number of intersections: 0 when d < 0; 1 when d = 0; 2 when d > 0
    if (d > 0.0) {
        float s = sqrt(d);
        float dstToSphereNear = max(0.0, (-b - s) / (2.0 * a));
        float dstToSphereFar = (-b + s) / (2.0 * a);

        // Ignore intersections that occur behind the ray
        if (dstToSphereFar >= 0.0) {
            return vec2(dstToSphereNear, dstToSphereFar - dstToSphereNear);
        }
    }
    // Ray did not intersect sphere
    return vec2(100000.0, 0.0);
}

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

float densityAtPoint(vec3 densitySamplePoint, int i) {
    float heightAboveSurface = length(densitySamplePoint) - PlanetData.Size[i];
    float height01 = heightAboveSurface / (AtmosphereData.Scale[i]*PlanetData.Size[i]);
    float localDensity = exp(-height01 * AtmosphereData.DensityFalloff[i]) * (1.0 - height01);
    return localDensity;
}

float opticalDepth(vec3 rayOrigin, vec3 rayDir, float rayLength, int i) {
    vec3 densitySamplePoint = rayOrigin;
    float stepSize = rayLength / (AtmosphereData.OpticalDepthPoints);
    float opticalDepth = 0.0;

    for (int j = 0; j < AtmosphereData.OpticalDepthPoints; ++j) {
       float localDensity = densityAtPoint(densitySamplePoint, i);
        opticalDepth += localDensity * stepSize;
        densitySamplePoint += rayDir * stepSize;
    }

    return opticalDepth / rayLength;
}

vec4 calculateLight(vec3 rayOrigin, vec3 rayDir, vec3 dirToSun, float rayLength, int i) {
    vec3 inScatterPoint = rayOrigin;
    float stepSize = rayLength / (AtmosphereData.InScatteringPoints);
    float inScatteredLight = 0.0;
    for (int j = 0; j < AtmosphereData.InScatteringPoints; ++j) {
        float sunRayLength = raySphere((AtmosphereData.Scale[i] + 1.0) * PlanetData.Size[i], inScatterPoint, dirToSun).y;
        float sunRayOpticalDepth = opticalDepth(inScatterPoint, dirToSun, sunRayLength, i);
        float viewRayopticalDepth = opticalDepth(inScatterPoint, -rayDir, stepSize * j, i);
        float transmittance = exp(-(sunRayOpticalDepth + viewRayopticalDepth));
        float localDensity = densityAtPoint(inScatterPoint, i);

        inScatteredLight += localDensity * transmittance * stepSize;
        inScatterPoint += rayDir * stepSize;
    }
    return vec4(inScatteredLight);
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
//            if (depth > dist) {
//                hitColor = color;
//                hitIndex = i;
//                depth = dist;
//            }
//            continue;
        }

    }

    float light = 0;

    for (int i = 0; i < PlanetData.Length; ++i) {

        float near = 0.0;
        float far = 0.0;

        vec3 tRo = (vec4(ro - PlanetData.Pos[i],1.0) * PlanetData.Rot[i]).xyz;
        vec3 tRd = (vec4(rd,0.0) * PlanetData.Rot[i]).xyz;

        if (iSphere(tRo, tRd, PlanetData.Size[i], near, far)) {
            glowColor = vec4(0.0, 1.0, 0.0, 1.0);
            continue;
        }
        vec2 hitInfo = raySphere(PlanetData.Size[i]*(1.0+AtmosphereData.Scale[i]), tRo, tRd);
        near = hitInfo.x;
//        if (!hit) continue;
        if (near >= depth) continue;

        float distThroughAtmosphere = min(hitInfo.y, depth-near);

        if (distThroughAtmosphere > 0) {
            vec3 pointInAtmosphere = tRo + tRd * (near + e);
            for (int j = 0; j < SunData.Length; ++j) {
                glowColor += calculateLight(pointInAtmosphere, tRd, (vec4(normalize(SunData.Pos[j] - PlanetData.Pos[i]),0.0)*PlanetData.Rot[i]).xyz, (distThroughAtmosphere - e)/**, light, noise*/, i);
            }

        }

    }

    hitColor *= light;

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
    noise = (noise - 0.5) * 0.1;

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