#version 330

layout(location = 0) in vec2 in_position;

out vec2 uv;

void main(void) {
	gl_Position = vec4(in_position * 2 - 1, 0, 1);
	uv = in_position;
}
