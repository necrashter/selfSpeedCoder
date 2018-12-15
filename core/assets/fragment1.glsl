#ifdef GL_ES
precision mediump float;
#endif
varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_resolution;
uniform int horizontal;
uniform float alpha;

const float pi = 3.14159265;
const float numBlurPixelsPerSide = 48.0;
const float sigma = 20.0;

void main(){
    vec4 color= v_color * texture2D(u_texture, v_texCoords);

    vec2 blurMultiplyVec = horizontal==0 ? vec2(1.0/u_resolution.x, 0.0) : vec2(0.0, 1.0/u_resolution.y);

    vec3 incrementalGaussian;
      incrementalGaussian.x = 1.0 / (sqrt(2.0 * pi) * sigma);
      incrementalGaussian.y = exp(-0.5 / (sigma * sigma));
      incrementalGaussian.z = incrementalGaussian.y * incrementalGaussian.y;
      vec4 avgValue = vec4(0.0);
      float coefficientSum = 0.0;

    /// first value (center)
    avgValue += texture2D(u_texture, v_texCoords) * incrementalGaussian.x;
      coefficientSum += incrementalGaussian.x;
      incrementalGaussian.xy *= incrementalGaussian.yz;
    /// other pixels around
    for (float i = 1.0; i <= numBlurPixelsPerSide; i++) {
        avgValue += texture2D(u_texture, v_texCoords - i  *
                              blurMultiplyVec) * incrementalGaussian.x;
        avgValue += texture2D(u_texture, v_texCoords + i  *
                              blurMultiplyVec) * incrementalGaussian.x;
        coefficientSum += 2.0 * incrementalGaussian.x;
          incrementalGaussian.xy *= incrementalGaussian.yz;
      }

    //vec4 color= v_color * texture2D(u_texture, v_texCoords);
    gl_FragColor = v_color * avgValue/coefficientSum;
    gl_FragColor.xyz += alpha*gl_FragColor.xyz;
    //gl_FragColor.w *= alpha;
}