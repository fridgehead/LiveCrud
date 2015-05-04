package core;

import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.ImageProducer;

import ddf.minim.analysis.FFT;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PMatrix;
import processing.core.PMatrix2D;
import processing.core.PMatrix3D;
import processing.core.PShape;
import processing.core.PStyle;
import processing.core.PVector;
import processing.opengl.PGL;
import processing.opengl.PShader;

/* useful functions for player to use 
 * these are all proxied from the PApplet instance so that you dont have to write p.method in your code
 * yes its horrible BUT I DONT CARE
 * */
public class DrawableBase {
	
	protected LiveCrud p;

	/* useful things to speed up coding */
	protected FFT fft; 
	protected PGraphics buffer;
	
	PFont font;

	
	public PFont getFont(){
		if(font == null){
			font = p.loadFont("BitstreamVeraSansMono-Bold-48.vlw");
			
		}
		return font;
	}
	
	public void centre() {
		p.centre();
	}

	public PVector[] makeVectorGrid(int w, int h, int d){
		
		PVector[] ret = new PVector[w*h*d];
		int c = 0;
		for(int x = 0; x < w; x++){
			for(int y = 0; y < h; y++){
				for(int z = 0; z < d; z++){
					ret[c] = new PVector(x - w/2,y - h/2,z - d/2);
					c++;
				}
			}
			
		}
		return ret;
	}
	
	public PVector[] makeVectorSphere(int ct, float size){
		PVector[] ret = new PVector[ct];
		for(int i = 0; i < ct; i++){
			ret[i] = PVector.mult(PVector.random3D(), size);
			
			
		}
		return ret;
	}
	
	public float getFft(int band){
		return fft.getBand(band);
	}
	
	
	//delegates
	
	public float sin(float v){
		return PApplet.sin(v);
	}
	public float cos(float v){
		return PApplet.cos(v);
	}
	
	public void translate(float x, float y, float z) {
		p.translate(x, y, z);
	}

	public void translate(float x, float y) {
		p.translate(x, y);
	}

	public void translate(PVector pi) {
		p.translate(pi);
	}

	public void noDepth(){
		p.hint(PApplet.DISABLE_DEPTH_TEST);

	}
	
	public void depth(){
		p.hint(PApplet.ENABLE_DEPTH_TEST);

	}

	public final float alpha(int rgb) {
		return p.alpha(rgb);
	}

	public void ambient(float v1, float v2, float v3) {
		p.ambient(v1, v2, v3);
	}

	public void ambient(float gray) {
		p.ambient(gray);
	}

	public void ambient(int rgb) {
		p.ambient(rgb);
	}

	public void ambientLight(float v1, float v2, float v3, float x, float y,
			float z) {
		p.ambientLight(v1, v2, v3, x, y, z);
	}

	public void ambientLight(float v1, float v2, float v3) {
		p.ambientLight(v1, v2, v3);
	}

	public void arc(float a, float b, float c, float d, float start,
			float stop, int mode) {
		p.arc(a, b, c, d, start, stop, mode);
	}

	public void arc(float a, float b, float c, float d, float start, float stop) {
		p.arc(a, b, c, d, start, stop);
	}

	public void background(float v1, float v2, float v3, float alpha) {
		p.background(v1, v2, v3, alpha);
	}

	public void background(float v1, float v2, float v3) {
		p.background(v1, v2, v3);
	}

	public void background(float gray, float alpha) {
		p.background(gray, alpha);
	}

	public void background(float gray) {
		p.background(gray);
	}

	public void background(int rgb, float alpha) {
		p.background(rgb, alpha);
	}

	public void background(int rgb) {
		p.background(rgb);
	}

	public void background(PImage image) {
		p.background(image);
	}

	public void beginCamera() {
		p.beginCamera();
	}

	public void beginContour() {
		p.beginContour();
	}

	public PGL beginPGL() {
		return p.beginPGL();
	}

	public void beginRaw(PGraphics rawGraphics) {
		p.beginRaw(rawGraphics);
	}

	public PGraphics beginRaw(String renderer, String filename) {
		return p.beginRaw(renderer, filename);
	}

	public void beginRecord(PGraphics recorder) {
		p.beginRecord(recorder);
	}

	public PGraphics beginRecord(String renderer, String filename) {
		return p.beginRecord(renderer, filename);
	}

	public void beginShape() {
		p.beginShape();
	}

	public void beginShape(int kind) {
		p.beginShape(kind);
	}

	public void bezier(float x1, float y1, float z1, float x2, float y2,
			float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
		p.bezier(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4);
	}

	public void bezier(float x1, float y1, float x2, float y2, float x3,
			float y3, float x4, float y4) {
		p.bezier(x1, y1, x2, y2, x3, y3, x4, y4);
	}

	public void bezier(PVector p1, PVector p2, PVector p3, PVector p4) {
		p.bezier(p1, p2, p3, p4);
	}

	public void bezierDetail(int detail) {
		p.bezierDetail(detail);
	}

	public float bezierPoint(float a, float b, float c, float d, float t) {
		return p.bezierPoint(a, b, c, d, t);
	}

	public float bezierTangent(float a, float b, float c, float d, float t) {
		return p.bezierTangent(a, b, c, d, t);
	}

	public void bezierVertex(float x2, float y2, float z2, float x3, float y3,
			float z3, float x4, float y4, float z4) {
		p.bezierVertex(x2, y2, z2, x3, y3, z3, x4, y4, z4);
	}

	public void bezierVertex(float x2, float y2, float x3, float y3, float x4,
			float y4) {
		p.bezierVertex(x2, y2, x3, y3, x4, y4);
	}

	public void blend(int sx, int sy, int sw, int sh, int dx, int dy, int dw,
			int dh, int mode) {
		p.blend(sx, sy, sw, sh, dx, dy, dw, dh, mode);
	}

	public void blend(PImage src, int sx, int sy, int sw, int sh, int dx,
			int dy, int dw, int dh, int mode) {
		p.blend(src, sx, sy, sw, sh, dx, dy, dw, dh, mode);
	}

	public void blendMode(int mode) {
		p.blendMode(mode);
	}

	public final float blue(int rgb) {
		return p.blue(rgb);
	}

	public void box(float w, float h, float d) {
		p.box(w, h, d);
	}

	public void box(float size) {
		p.box(size);
	}

	public final float brightness(int rgb) {
		return p.brightness(rgb);
	}

	public void camera() {
		p.camera();
	}

	public void camera(float eyeX, float eyeY, float eyeZ, float centerX,
			float centerY, float centerZ, float upX, float upY, float upZ) {
		p.camera(eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
	}

	public void clear() {
		p.clear();
	}

	public void clip(float a, float b, float c, float d) {
		p.clip(a, b, c, d);
	}

	public final int color(float v1, float v2, float v3, float alpha) {
		return p.color(v1, v2, v3, alpha);
	}

	public final int color(float v1, float v2, float v3) {
		return p.color(v1, v2, v3);
	}

	public final int color(float fgray, float falpha) {
		return p.color(fgray, falpha);
	}

	public final int color(float fgray) {
		return p.color(fgray);
	}

	public final int color(int v1, int v2, int v3, int alpha) {
		return p.color(v1, v2, v3, alpha);
	}

	public final int color(int v1, int v2, int v3) {
		return p.color(v1, v2, v3);
	}

	public final int color(int gray, int alpha) {
		return p.color(gray, alpha);
	}

	public final int color(int gray) {
		return p.color(gray);
	}

	public void colorMode(int mode, float max1, float max2, float max3,
			float maxA) {
		p.colorMode(mode, max1, max2, max3, maxA);
	}

	public void colorMode(int mode, float max1, float max2, float max3) {
		p.colorMode(mode, max1, max2, max3);
	}

	public void colorMode(int mode, float max) {
		p.colorMode(mode, max);
	}

	public void colorMode(int mode) {
		p.colorMode(mode);
	}

	public void copy(int sx, int sy, int sw, int sh, int dx, int dy, int dw,
			int dh) {
		p.copy(sx, sy, sw, sh, dx, dy, dw, dh);
	}

	public void copy(PImage src, int sx, int sy, int sw, int sh, int dx,
			int dy, int dw, int dh) {
		p.copy(src, sx, sy, sw, sh, dx, dy, dw, dh);
	}

	public PFont createFont(String name, float size, boolean smooth,
			char[] charset) {
		return p.createFont(name, size, smooth, charset);
	}

	public PFont createFont(String name, float size, boolean smooth) {
		return p.createFont(name, size, smooth);
	}

	public PFont createFont(String name, float size) {
		return p.createFont(name, size);
	}

	public PGraphics createGraphics(int w, int h, String renderer, String path) {
		return p.createGraphics(w, h, renderer, path);
	}

	public PGraphics createGraphics(int w, int h, String renderer) {
		return p.createGraphics(w, h, renderer);
	}

	public PGraphics createGraphics(int w, int h) {
		return p.createGraphics(w, h);
	}

	public Image createImage(ImageProducer arg0) {
		return p.createImage(arg0);
	}

	public PImage createImage(int w, int h, int format) {
		return p.createImage(w, h, format);
	}

	public Image createImage(int arg0, int arg1) {
		return p.createImage(arg0, arg1);
	}

	public PShape createShape() {
		return p.createShape();
	}

	
	public PShape createShape(int type) {
		return p.createShape(type);
	}

	public PShape createShape(PShape source) {
		return p.createShape(source);
	}

	public void curve(float x1, float y1, float z1, float x2, float y2,
			float z2, float x3, float y3, float z3, float x4, float y4, float z4) {
		p.curve(x1, y1, z1, x2, y2, z2, x3, y3, z3, x4, y4, z4);
	}

	public void curve(float x1, float y1, float x2, float y2, float x3,
			float y3, float x4, float y4) {
		p.curve(x1, y1, x2, y2, x3, y3, x4, y4);
	}

	public void curveDetail(int detail) {
		p.curveDetail(detail);
	}

	public float curvePoint(float a, float b, float c, float d, float t) {
		return p.curvePoint(a, b, c, d, t);
	}

	public float curveTangent(float a, float b, float c, float d, float t) {
		return p.curveTangent(a, b, c, d, t);
	}

	public void curveTightness(float tightness) {
		p.curveTightness(tightness);
	}

	public void curveVertex(float x, float y, float z) {
		p.curveVertex(x, y, z);
	}

	public void curveVertex(float x, float y) {
		p.curveVertex(x, y);
	}

	public void directionalLight(float v1, float v2, float v3, float nx,
			float ny, float nz) {
		p.directionalLight(v1, v2, v3, nx, ny, nz);
	}

	public void edge(boolean edge) {
		p.edge(edge);
	}

	public void ellipse(float a, float b, float c, float d) {
		p.ellipse(a, b, c, d);
	}

	public void ellipseMode(int mode) {
		p.ellipseMode(mode);
	}

	public void emissive(float v1, float v2, float v3) {
		p.emissive(v1, v2, v3);
	}

	public void emissive(float gray) {
		p.emissive(gray);
	}

	public void emissive(int rgb) {
		p.emissive(rgb);
	}

	public void endCamera() {
		p.endCamera();
	}

	public void endContour() {
		p.endContour();
	}

	public void endPGL() {
		p.endPGL();
	}

	public void endShape() {
		p.endShape();
	}

	public void endShape(int mode) {
		p.endShape(mode);
	}

	public void fill(float v1, float v2, float v3, float alpha) {
		p.fill(v1, v2, v3, alpha);
	}

	public void fill(float v1, float v2, float v3) {
		p.fill(v1, v2, v3);
	}

	public void fill(float gray, float alpha) {
		p.fill(gray, alpha);
	}

	public void fill(float gray) {
		p.fill(gray);
	}

	public void fill(int rgb, float alpha) {
		p.fill(rgb, alpha);
	}

	public void fill(int rgb) {
		p.fill(rgb);
	}

	public void filter(int kind, float param) {
		p.filter(kind, param);
	}

	public void filter(int kind) {
		p.filter(kind);
	}

	public void filter(PShader shader) {
		p.filter(shader);
	}

	public void frustum(float left, float right, float bottom, float top,
			float near, float far) {
		p.frustum(left, right, bottom, top, near, far);
	}

	public void hint(int which) {
		p.hint(which);
	}

	public final float hue(int rgb) {
		return p.hue(rgb);
	}

	public void image(PImage img, float a, float b, float c, float d, int u1,
			int v1, int u2, int v2) {
		p.image(img, a, b, c, d, u1, v1, u2, v2);
	}

	public void image(PImage img, float a, float b, float c, float d) {
		p.image(img, a, b, c, d);
	}

	public void image(PImage img, float a, float b) {
		p.image(img, a, b);
	}

	public void imageMode(int mode) {
		p.imageMode(mode);
	}

	public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3,
			int arg4, int arg5) {
		return p.imageUpdate(arg0, arg1, arg2, arg3, arg4, arg5);
	}

	public int lerpColor(int c1, int c2, float amt) {
		return p.lerpColor(c1, c2, amt);
	}

	public void lightFalloff(float constant, float linear, float quadratic) {
		p.lightFalloff(constant, linear, quadratic);
	}

	public void lightSpecular(float v1, float v2, float v3) {
		p.lightSpecular(v1, v2, v3);
	}

	public void lights() {
		p.lights();
	}

	public void line(float x1, float y1, float z1, float x2, float y2, float z2) {
		p.line(x1, y1, z1, x2, y2, z2);
	}

	public void line(float x1, float y1, float x2, float y2) {
		p.line(x1, y1, x2, y2);
	}

	public void line(PVector p1, PVector p2) {
		p.line(p1, p2);
	}

	public void line(PVector p1, PVector p2, PVector p3) {
		p.line(p1, p2, p3);
	}

	public PShader loadShader(String fragFilename, String vertFilename) {
		return p.loadShader(fragFilename, vertFilename);
	}

	public PShader loadShader(String fragFilename) {
		return p.loadShader(fragFilename);
	}

	public PShape loadShape(String filename, String options) {
		return p.loadShape(filename, options);
	}

	public PShape loadShape(String filename) {
		return p.loadShape(filename);
	}

	public String[] loadStrings(String filename) {
		return p.loadStrings(filename);
	}

	public void mask(PImage img) {
		p.mask(img);
	}

	public int millis() {
		return p.millis();
	}

	public void noLights() {
		p.noLights();
	}

	public void noSmooth() {
		p.noSmooth();
	}

	public void noStroke() {
		p.noStroke();
	}

	public void noTexture() {
		p.noTexture();
	}

	public void noTint() {
		p.noTint();
	}

	public float noise(float x, float y, float z) {
		return p.noise(x, y, z);
	}

	public float noise(float x, float y) {
		return p.noise(x, y);
	}

	public float noise(float x) {
		return p.noise(x);
	}

	public void noiseDetail(int lod, float falloff) {
		p.noiseDetail(lod, falloff);
	}

	public void noiseDetail(int lod) {
		p.noiseDetail(lod);
	}

	public void noiseSeed(long seed) {
		p.noiseSeed(seed);
	}

	public void normal(float nx, float ny, float nz) {
		p.normal(nx, ny, nz);
	}

	public void ortho() {
		p.ortho();
	}

	public void ortho(float left, float right, float bottom, float top,
			float near, float far) {
		p.ortho(left, right, bottom, top, near, far);
	}

	public void ortho(float left, float right, float bottom, float top) {
		p.ortho(left, right, bottom, top);
	}

	public void perspective() {
		p.perspective();
	}

	public void perspective(float fovy, float aspect, float zNear, float zFar) {
		p.perspective(fovy, aspect, zNear, zFar);
	}

	public void point(float x, float y, float z) {
		p.point(x, y, z);
	}

	public void point(float x, float y) {
		p.point(x, y);
	}

	public void pointLight(float v1, float v2, float v3, float x, float y,
			float z) {
		p.pointLight(v1, v2, v3, x, y, z);
	}

	public void popMatrix() {
		p.popMatrix();
	}

	public void popMatrix(String tag) {
		p.popMatrix(tag);
	}

	public void popStyle() {
		p.popStyle();
	}

	public void pushMatrix() {
		p.pushMatrix();
	}

	public void pushMatrix(String tag) {
		p.pushMatrix(tag);
	}

	public void pushStyle() {
		p.pushStyle();
	}

	public void quad(float x1, float y1, float x2, float y2, float x3,
			float y3, float x4, float y4) {
		p.quad(x1, y1, x2, y2, x3, y3, x4, y4);
	}

	public void quadraticVertex(float cx, float cy, float cz, float x3,
			float y3, float z3) {
		p.quadraticVertex(cx, cy, cz, x3, y3, z3);
	}

	public void quadraticVertex(float cx, float cy, float x3, float y3) {
		p.quadraticVertex(cx, cy, x3, y3);
	}

	public final float random(float low, float high) {
		return p.random(low, high);
	}

	public final float random(float high) {
		return p.random(high);
	}

	public final float randomGaussian() {
		return p.randomGaussian();
	}

	public final void randomSeed(long seed) {
		p.randomSeed(seed);
	}

	public void rect(float a, float b, float c, float d, float tl, float tr,
			float br, float bl) {
		p.rect(a, b, c, d, tl, tr, br, bl);
	}

	public void rect(float a, float b, float c, float d, float r) {
		p.rect(a, b, c, d, r);
	}

	public void rect(float a, float b, float c, float d) {
		p.rect(a, b, c, d);
	}

	public void rectMode(int mode) {
		p.rectMode(mode);
	}

	public final float red(int rgb) {
		return p.red(rgb);
	}

	public void resetMatrix() {
		p.resetMatrix();
	}

	public void resetShader() {
		p.resetShader();
	}

	public void resetShader(int kind) {
		p.resetShader(kind);
	}

	public void rotate(float angle, float x, float y, float z) {
		p.rotate(angle, x, y, z);
	}

	public void rotate(float angle) {
		p.rotate(angle);
	}

	public void rotateX(float angle) {
		p.rotateX(angle);
	}

	public void rotateY(float angle) {
		p.rotateY(angle);
	}

	public void rotateZ(float angle) {
		p.rotateZ(angle);
	}

	public final float saturation(int rgb) {
		return p.saturation(rgb);
	}

	public void scale(float x, float y, float z) {
		p.scale(x, y, z);
	}

	public void scale(float x, float y) {
		p.scale(x, y);
	}

	public void scale(float s) {
		p.scale(s);
	}

	public float screenX(float x, float y, float z) {
		return p.screenX(x, y, z);
	}

	public float screenX(float x, float y) {
		return p.screenX(x, y);
	}

	public float screenY(float x, float y, float z) {
		return p.screenY(x, y, z);
	}

	public float screenY(float x, float y) {
		return p.screenY(x, y);
	}

	public float screenZ(float x, float y, float z) {
		return p.screenZ(x, y, z);
	}

	public void set(int x, int y, int c) {
		p.set(x, y, c);
	}

	public void set(int x, int y, PImage img) {
		p.set(x, y, img);
	}

	public void setBackground(Color arg0) {
		p.setBackground(arg0);
	}

	public void setFont(Font arg0) {
		p.setFont(arg0);
	}

	public void setForeground(Color arg0) {
		p.setForeground(arg0);
	}

	public void setMatrix(PMatrix source) {
		p.setMatrix(source);
	}

	public void setMatrix(PMatrix2D source) {
		p.setMatrix(source);
	}

	public void setMatrix(PMatrix3D source) {
		p.setMatrix(source);
	}

	public void shader(PShader shader, int kind) {
		p.shader(shader, kind);
	}

	public void shader(PShader shader) {
		p.shader(shader);
	}

	public void shape(PShape shape, float a, float b, float c, float d) {
		p.shape(shape, a, b, c, d);
	}

	public void shape(PShape shape, float x, float y) {
		p.shape(shape, x, y);
	}

	public void shape(PShape shape) {
		p.shape(shape);
	}

	public void shapeMode(int mode) {
		p.shapeMode(mode);
	}

	public void shearX(float angle) {
		p.shearX(angle);
	}

	public void shearY(float angle) {
		p.shearY(angle);
	}

	public void shininess(float shine) {
		p.shininess(shine);
	}

	public void smooth() {
		p.smooth();
	}

	public void smooth(int level) {
		p.smooth(level);
	}

	public void specular(float v1, float v2, float v3) {
		p.specular(v1, v2, v3);
	}

	public void specular(float gray) {
		p.specular(gray);
	}

	public void specular(int rgb) {
		p.specular(rgb);
	}

	public void sphere(float r) {
		p.sphere(r);
	}

	public void sphereDetail(int ures, int vres) {
		p.sphereDetail(ures, vres);
	}

	public void sphereDetail(int res) {
		p.sphereDetail(res);
	}

	public void spotLight(float v1, float v2, float v3, float x, float y,
			float z, float nx, float ny, float nz, float angle,
			float concentration) {
		p.spotLight(v1, v2, v3, x, y, z, nx, ny, nz, angle, concentration);
	}

	public void stroke(float v1, float v2, float v3, float alpha) {
		p.stroke(v1, v2, v3, alpha);
	}

	public void stroke(float v1, float v2, float v3) {
		p.stroke(v1, v2, v3);
	}

	public void stroke(float gray, float alpha) {
		p.stroke(gray, alpha);
	}

	public void stroke(float gray) {
		p.stroke(gray);
	}

	public void stroke(int rgb, float alpha) {
		p.stroke(rgb, alpha);
	}

	public void stroke(int rgb) {
		p.stroke(rgb);
	}

	public void strokeCap(int cap) {
		p.strokeCap(cap);
	}

	public void strokeJoin(int join) {
		p.strokeJoin(join);
	}

	public void strokeWeight(float weight) {
		p.strokeWeight(weight);
	}

	public void style(PStyle s) {
		p.style(s);
	}

	public void text(char c, float x, float y, float z) {
		p.text(c, x, y, z);
	}

	public void text(char c, float x, float y) {
		p.text(c, x, y);
	}

	public void text(char[] chars, int start, int stop, float x, float y,
			float z) {
		p.text(chars, start, stop, x, y, z);
	}

	public void text(char[] chars, int start, int stop, float x, float y) {
		p.text(chars, start, stop, x, y);
	}

	public void text(float num, float x, float y, float z) {
		p.text(num, x, y, z);
	}

	public void text(float num, float x, float y) {
		p.text(num, x, y);
	}

	public void text(int num, float x, float y, float z) {
		p.text(num, x, y, z);
	}

	public void text(int num, float x, float y) {
		p.text(num, x, y);
	}

	public void text(String str, float x1, float y1, float x2, float y2) {
		p.text(str, x1, y1, x2, y2);
	}

	public void text(String str, float x, float y, float z) {
		p.text(str, x, y, z);
	}

	public void text(String str, float x, float y) {
		p.text(str, x, y);
	}

	public void textAlign(int alignX, int alignY) {
		p.textAlign(alignX, alignY);
	}

	public void textAlign(int alignX) {
		p.textAlign(alignX);
	}

	public float textAscent() {
		return p.textAscent();
	}

	public float textDescent() {
		return p.textDescent();
	}

	public void textFont(PFont which, float size) {
		p.textFont(which, size);
	}

	public void textFont(PFont which) {
		p.textFont(which);
	}

	public void textLeading(float leading) {
		p.textLeading(leading);
	}

	public void textMode(int mode) {
		p.textMode(mode);
	}

	public void textSize(float size) {
		p.textSize(size);
	}

	public float textWidth(char c) {
		return p.textWidth(c);
	}

	public float textWidth(char[] chars, int start, int length) {
		return p.textWidth(chars, start, length);
	}

	public float textWidth(String str) {
		return p.textWidth(str);
	}

	public void texture(PImage image) {
		p.texture(image);
	}

	public void textureMode(int mode) {
		p.textureMode(mode);
	}

	public void textureWrap(int wrap) {
		p.textureWrap(wrap);
	}

	public void tint(float v1, float v2, float v3, float alpha) {
		p.tint(v1, v2, v3, alpha);
	}

	public void tint(float v1, float v2, float v3) {
		p.tint(v1, v2, v3);
	}

	public void tint(float gray, float alpha) {
		p.tint(gray, alpha);
	}

	public void tint(float gray) {
		p.tint(gray);
	}

	public void tint(int rgb, float alpha) {
		p.tint(rgb, alpha);
	}

	public void tint(int rgb) {
		p.tint(rgb);
	}

	public void triangle(float x1, float y1, float x2, float y2, float x3,
			float y3) {
		p.triangle(x1, y1, x2, y2, x3, y3);
	}

	public void vertex(float x, float y, float z, float u, float v) {
		p.vertex(x, y, z, u, v);
	}

	public void vertex(float x, float y, float u, float v) {
		p.vertex(x, y, u, v);
	}

	public void vertex(float x, float y, float z) {
		p.vertex(x, y, z);
	}

	public void vertex(float x, float y) {
		p.vertex(x, y);
	}

	public void vertex(float[] v) {
		p.vertex(v);
	}

}
