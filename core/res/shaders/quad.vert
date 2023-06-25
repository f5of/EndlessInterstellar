attribute vec4 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_proj;
uniform mat4 u_trans;
uniform mat4 u_model;

varying vec2 v_tpos;

void main(){
    v_tpos = a_texCoord0;
    gl_Position = u_proj * u_trans * u_model * a_position;
}