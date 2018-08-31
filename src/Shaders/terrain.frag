#version 400 core

in vec2 texCoords;
in vec3 normal;
in vec3 lightVector[4];
in vec3 fragToCam;
in float visibility;
in vec4 shadowCoords;

out vec4 out_color;

uniform sampler2D textureBackground;
uniform sampler2D textureImageR;
uniform sampler2D textureImageG;
uniform sampler2D textureImageB;
uniform sampler2D blendMap;
uniform sampler2D shadowMap;

uniform vec3 lightColor[4];
uniform vec3 attenuation[4];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColor;

const int pcf = 2;
const float totalTexels = (pcf * 2.0 +1.0) * (pcf * 2.0 +1.0);

void main() {

	float mapSize = 4096;
	float texelSize = 1.0 / mapSize;
	float total = 0.0;

	for(int x = -pcf; x <= pcf; x++) {
		for(int y=-pcf; y <= pcf; y++) {
			float objectNearstLight = texture(shadowMap, shadowCoords.xy + vec2(x, y) * texelSize).r;
			if(shadowCoords.z > objectNearstLight) {
				total+=1.0;
			}

		}
	}

	total/=totalTexels;

	float lightFactor = 1.0 - (total * shadowCoords.w);

	vec4 blendMapCol = texture(blendMap, texCoords);

	float backTexureAmount = 1 - (blendMapCol.r + blendMapCol.g + blendMapCol.b);
	vec2 tiledCoords = texCoords * 40.0;

	vec4 backgroundTextureColor = texture(textureBackground, tiledCoords) * backTexureAmount;
	vec4 rTextureColor = texture(textureImageR, tiledCoords) * blendMapCol.r;
	vec4 gTextureColor = texture(textureImageG, tiledCoords) * blendMapCol.g;
	vec4 bTextureColor = texture(textureImageB, tiledCoords) * blendMapCol.b;

	vec4 totalCol = backgroundTextureColor + rTextureColor + gTextureColor + bTextureColor;

	vec3 normal = normalize(normal);

	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);
	for(int i=0; i<4; i++) {
		float distance = length(lightVector[i]);
		float attenuationFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);

		vec3 lightVector = normalize(lightVector[i]);

		//Diffuse
		float nDotL = dot(normal, lightVector);
		float brightness = max(nDotL, 0.0);

		//Specular
		vec3 fragToLight = -lightVector;
		vec3 reflectedLight = reflect(fragToLight, normal);

		float specularFact=  dot(reflectedLight, fragToCam);
		specularFact = max(specularFact, 0.0);
		float dampedFactor = pow(specularFact, shineDamper);
		totalDiffuse = totalDiffuse + (brightness * lightColor[i]) / attenuationFactor;
		totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColor[i]) /attenuationFactor;
	}
	totalDiffuse = max(totalDiffuse* lightFactor, 0.0);

	out_color = vec4(totalDiffuse, 1.0) * totalCol + vec4(totalSpecular, 1.0);
	out_color = mix(vec4(skyColor, 1.0), out_color, visibility);
}
