#version 330

in vec2 uv;

out vec4 color;

uniform sampler2D texture_sampler;

void main(void) {
	color = texture(texture_sampler, uv);
}
