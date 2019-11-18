#version 330

in vec3 normal;
in vec2 uv;

out vec4 color;

uniform sampler2D texture_sampler;

void main(void) {
	float greyscale = dot(normal, vec3(0, 1, 0));
	color = vec4(texture(texture_sampler, uv).xyz * greyscale, 1);
}
