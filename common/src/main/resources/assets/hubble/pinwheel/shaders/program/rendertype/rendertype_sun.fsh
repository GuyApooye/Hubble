
uniform vec4 ColorModulator;

in float vertexDistance;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    fragColor = vertexColor * ColorModulator;
}
