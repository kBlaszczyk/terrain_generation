#version 330

const int MAX_LAYERS = 10;
const float EPSILON = 0.0001;

in vec3 position_cs;
in vec3 normal_cs;
in vec2 uv;
in vec3 vs_out_position_ms;
in vec3 vs_out_normal_ms;

out vec4 color;

uniform vec3 light_direction_cs;
uniform sampler2DArray layer_textures;
uniform float layer_limits[MAX_LAYERS];
uniform float layer_blending_heights[MAX_LAYERS];
uniform int layers_count;

float ambient_intensity = 0.2;
float diffuse_intensity = 0.8;
float specular_intensity = 0.5;
float specular_power = 2;
float texture_scale = 0.05;

vec4 sampleTextureTriplanar(vec3 blend_axes, int index) {
	vec4 x_projection_color = texture(layer_textures, vec3(vs_out_position_ms.yz * texture_scale, index)) * blend_axes.x;
	vec4 y_projection_color = texture(layer_textures, vec3(vs_out_position_ms.xz * texture_scale, index)) * blend_axes.y;
	vec4 z_projection_color = texture(layer_textures, vec3(vs_out_position_ms.xy * texture_scale, index)) * blend_axes.z;

	return x_projection_color + y_projection_color + z_projection_color;
}

vec4 applyLighting(vec4 color) {
	vec4 ambient_color = color * ambient_intensity;

	float diffuse_factor = -min(dot(normal_cs, light_direction_cs), 0);
	vec4 diffuse_color = color * diffuse_intensity * diffuse_factor;

	vec3 reflected_light = normalize(reflect(light_direction_cs, normal_cs));
	float specular_factor = max(dot(normalize(-position_cs), reflected_light), 0);
	vec4 specular_color = color * pow(specular_factor, specular_power) * specular_intensity;

	return ambient_color + diffuse_color + specular_color;
}

void main(void) {

	vec3 blend_axes = abs(vs_out_normal_ms);
	blend_axes /= (blend_axes.x + blend_axes.y + blend_axes.z);

	vec4 material_color = vec4(0);
	for (int i = layers_count - 1; i >= 0; i--) {
		float blending_range = layer_limits[i] - layer_blending_heights[i] + EPSILON;
		float draw_strength = clamp((layer_limits[i] - vs_out_position_ms.y) / blending_range, 0, 1);

		vec4 texture_color = sampleTextureTriplanar(blend_axes, i);
		material_color = material_color * (1 - draw_strength) + texture_color * draw_strength;
	}

	color = applyLighting(material_color);
}
