#version 400

layout (vertices = 4) out;

void main() {
	gl_TessLevelOuter[0] = 4;
	gl_TessLevelOuter[1] = 4;
	gl_TessLevelOuter[2] = 4;
	gl_TessLevelOuter[3] = 4;

	gl_TessLevelInner[0] = 7;
	gl_TessLevelInner[1] = 7;

	gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;
}
