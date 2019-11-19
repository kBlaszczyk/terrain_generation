#version 330

layout(location = 0) in vec3 position_ms;
layout(location = 1) in vec3 normal_ms;
layout(location = 2) in vec2 texcoords;

out vec3 position_cs;
out vec3 normal_cs;
out vec2 uv;
out float height;

uniform mat4 model_view;
uniform mat4 model_view_projection;

void main(void) {
	position_cs = (model_view * vec4(position_ms, 1)).xyz;
	normal_cs = normalize((model_view * vec4(normal_ms, 0)).xyz);
	uv = texcoords;

	height = position_ms.y;
	gl_Position = model_view_projection * vec4(position_ms, 1);
}
