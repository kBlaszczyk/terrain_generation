#version 400

layout (quads, fractional_odd_spacing, cw) in;

out vec3 position_cs;
out vec3 normal_cs;
out vec3 tes_out_position_ws;
out vec3 tes_out_normal_ms;

uniform mat4 model;
uniform mat4 model_view;
uniform mat4 model_view_projection;
uniform sampler2D heightMap;
uniform sampler2D normalMap;

void main() {
	float height = texture(heightMap, gl_TessCoord.xy).r;
	vec3 position_ms = vec3(gl_TessCoord.x - 0.5, height, gl_TessCoord.y - 0.5);

	gl_Position = model_view_projection * vec4(position_ms, 1);
	tes_out_position_ws = (model * vec4(position_ms, 1)).xyz;

	position_cs = (model_view * vec4(position_ms, 1)).xyz;
	vec3 normal_ms = texture(normalMap, gl_TessCoord.xy).xyz;
	normal_cs = normalize((model_view * vec4(normal_ms, 0)).xyz);
	tes_out_normal_ms = normal_ms;
}
