#version 400

layout(location = 0) in vec2 in_position;

void main(void) {
	gl_Position = vec4(in_position.x, 0, in_position.y, 1);
}
