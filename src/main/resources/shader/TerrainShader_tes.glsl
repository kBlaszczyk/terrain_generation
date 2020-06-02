#version 400

layout (quads, fractional_odd_spacing, cw) in;

out vec3 position_cs;
out vec3 normal_cs;
out vec3 tes_out_position_ws;
out vec3 tes_out_normal_ms;

uniform mat4 model;
uniform mat4 model_view;
uniform mat4 model_view_projection;
uniform float terrain_width;
uniform vec2 tessellation_origin;
uniform float tessellation_width;
uniform sampler2D height_map;
uniform sampler2D normal_map;

void main() {
	vec2 terrain_sample = tessellation_origin + gl_TessCoord.xy * vec2(tessellation_width);
	float height = texture(height_map, terrain_sample).r;
	vec3 position_ms = vec3(gl_TessCoord.x - 0.5, height, gl_TessCoord.y - 0.5);

	gl_Position = model_view_projection * vec4(position_ms, 1);
	tes_out_position_ws = (model * vec4(position_ms, 1)).xyz;

	position_cs = (model_view * vec4(position_ms, 1)).xyz;
	vec3 normal_ms = texture(normal_map, terrain_sample).xyz;
	normal_cs = normalize((model_view * vec4(normal_ms, 0)).xyz);
	tes_out_normal_ms = normal_ms;
}
