#ifdef GL_ES
precision mediump float;
#endif
varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

const float threshold = .5;

void main(){
    vec3 luminanceVector = vec3(0.2125, 0.7154, 0.0721);
    vec4 color = v_color * texture2D(u_texture, v_texCoords);

    float luminance = dot(luminanceVector, color.xyz);
    luminance = max(.0, luminance - threshold);
    color.xyz *= sign(luminance);
    color.a=1.0;

    gl_FragColor = color;
 }
