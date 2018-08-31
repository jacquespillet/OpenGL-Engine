#version 400 core

in vec3 aPosition;
in vec2 aTexCoords;
in vec3 aNormal;

out vec2 texCoords;
out vec3 normal;
out vec3 lightVector[4];
out vec3 fragToCam;
out float visibility;
out vec4 shadowCoords;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform vec3 lightPos[4];
uniform vec4 plane;
uniform mat4 toShadowMapSpace;

const float density = 0.002;
const float gradient = 5.0;
const float shadowDistance = 150.0;
const float transitionDistance = 10.0;

void main(void) {
	vec4 worldPosition = transformationMatrix * vec4(aPosition, 1.0);

	shadowCoords = toShadowMapSpace * worldPosition;

	gl_ClipDistance[0] = dot(worldPosition, plane);

	vec4 positionFromCam = viewMatrix * worldPosition;
	gl_Position = projectionMatrix * positionFromCam;

	normal = (transformationMatrix * vec4(aNormal, 0.0)).xyz;

	for(int i=0; i<4; i++) {
		lightVector[i] = lightPos[i] - worldPosition.xyz;
	}

	fragToCam = (inverse(viewMatrix) * vec4(0.0, 0.0, 0.0, 1.0)).xyz - worldPosition.xyz;
	texCoords = aTexCoords;


	float distance = length(positionFromCam.xyz);
	visibility = clamp(exp(-pow((distance * density), gradient)), 0.0, 1.0);

	distance = distance - (shadowDistance - transitionDistance);
	distance = distance / transitionDistance;
	shadowCoords.w = clamp(1.0-distance, 0.0, 1.0);

}
