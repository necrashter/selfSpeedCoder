#ifdef GL_ES
precision lowp float;
#endif
varying vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform vec2 u_resolution;

uniform float u_time;

float random (vec2 st) {
    return fract(sin(dot(st.xy, vec2(u_time,9)))* 43758.5453123);
}

void main(){
    //v_color * texture2D(u_texture, v_texCoords);
    vec3 r = vec3(random(gl_FragCoord.xy/u_resolution.xy));
    r.r *=.7f; r.b *= .9f;
    gl_FragColor = vec4(r,.3f);
 }