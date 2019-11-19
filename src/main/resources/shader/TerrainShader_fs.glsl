#version 330

const int MAX_LAYERS = 10;

in vec3 position_cs;
in vec3 normal_cs;
in vec2 uv;
in float height;

out vec4 color;

uniform vec3 light_direction_cs;
uniform sampler2D texture_sampler;

uniform vec3 layer_colors[MAX_LAYERS];
uniform float layer_limits[MAX_LAYERS];

float ambient_intensity = 0.2;
float diffuse_intensity = 0.8;
float specular_intensity = 0.5;
float specular_power = 2;

void main(void) {

	vec4 material_color = vec4(layer_colors[MAX_LAYERS - 1], 1);
	for (int i = MAX_LAYERS - 1; i >= 0; i--) {
		float draw_strength = clamp(sign(layer_limits[i] - height), 0, 1);
		material_color = material_color * (1 - draw_strength) + vec4(layer_colors[i], 1) * draw_strength;
	}

	vec4 ambient_color = material_color * ambient_intensity;

	float diffuse_factor = -min(dot(normal_cs, light_direction_cs), 0);
	vec4 diffuse_color = material_color * diffuse_intensity * diffuse_factor;

	vec3 reflected_light = normalize(reflect(light_direction_cs, normal_cs));
	float specular_factor = max(dot(normalize(-position_cs), reflected_light), 0);
	vec4 specular_color = material_color * pow(specular_factor, specular_power) * specular_intensity;

	color = ambient_color + diffuse_color + specular_color;
}
