#version 400 core

in vec2 texCoords;
in vec3 normal;
in vec3 lightVector[4];
in vec3 fragToCam;
in float visibility;

out vec4 out_color;

uniform sampler2D textureImage;
uniform vec3 lightColor[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform float useFakeLighting;
uniform vec3 skyColor;

void main() {
	vec3 normal = normalize(normal);
	vec3 fragToCam = normalize(fragToCam);

	if(useFakeLighting > 0.5) {
		normal= vec3(0.0, 1.0, 0.0);
	}

	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);

	for(int i=0; i<4; i++) {
		float distance = length(lightVector[i]);
		float attenuationFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);

		vec3 lightVector = normalize(lightVector[i]);

		//Diffuse
		float nDotL = dot(normal, lightVector);
		float brightness = max(nDotL, 0.0);
		totalDiffuse = totalDiffuse + (brightness * lightColor[i]) / attenuationFactor;

		//Specular
		vec3 fragToLight = -lightVector;
		vec3 reflectedLight = reflect(fragToLight, normal);

		float specularFact=  dot(reflectedLight, fragToCam);
		specularFact = max(specularFact, 0.0);
		float dampedFactor = pow(specularFact, shineDamper);
		totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColor[i]) /attenuationFactor;
	}

	totalDiffuse = max(totalDiffuse, 0.1);


	vec4 textureColor = texture(textureImage, texCoords);
	if(textureColor.a < 0.5) {
		discard;
	}

	out_color = vec4(totalDiffuse, 1.0) * textureColor + vec4(totalSpecular, 1.0);
	out_color = mix(vec4(skyColor, 1.0), out_color, visibility);
}
