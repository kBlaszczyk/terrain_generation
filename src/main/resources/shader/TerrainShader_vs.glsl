#version 330

layout(location = 0) in vec3 in_position;
layout(location = 1) in vec3 in_normal;
layout(location = 2) in vec2 in_texcoords;

out vec3 normal;
out vec2 uv;

uniform mat4 model_view;
uniform mat4 model_view_projection;

void main(void) {
	gl_Position = model_view_projection * vec4(in_position, 1);
	normal = in_normal;
	uv = in_texcoords;
}
