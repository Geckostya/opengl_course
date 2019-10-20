package fractal;

import com.jogamp.opengl.GL4;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.math.FloatUtil;
import com.jogamp.opengl.util.GLBuffers;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;
import framework.Semantic;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import static com.jogamp.opengl.GL.GL_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_DEPTH_TEST;
import static com.jogamp.opengl.GL.GL_DONT_CARE;
import static com.jogamp.opengl.GL.GL_ELEMENT_ARRAY_BUFFER;
import static com.jogamp.opengl.GL.GL_FLOAT;
import static com.jogamp.opengl.GL.GL_MAP_INVALIDATE_BUFFER_BIT;
import static com.jogamp.opengl.GL.GL_MAP_WRITE_BIT;
import static com.jogamp.opengl.GL.GL_TRIANGLES;
import static com.jogamp.opengl.GL.GL_UNSIGNED_SHORT;
import static com.jogamp.opengl.GL2ES2.GL_DEBUG_SEVERITY_HIGH;
import static com.jogamp.opengl.GL2ES2.GL_DEBUG_SEVERITY_MEDIUM;
import static com.jogamp.opengl.GL2ES2.GL_FRAGMENT_SHADER;
import static com.jogamp.opengl.GL2ES2.GL_VERTEX_SHADER;
import static com.jogamp.opengl.GL2ES3.*;
import static com.jogamp.opengl.GL2GL3.GL_TEXTURE_1D;
import static com.jogamp.opengl.GL4.GL_MAP_COHERENT_BIT;
import static com.jogamp.opengl.GL4.GL_MAP_PERSISTENT_BIT;

public class JuliaFractal implements GLEventListener {
  private int sizeX;
  private int sizeY;
  private int size;

  private float[] center = new float[]{0, 0};
  private float zoom = 1;

  private float[] vertexData = {
      -10, -10,
      -10, +10,
      +10, -10,
      +10, +10
  };

  private int rThresholdUniform;
  private int maxIterUniform;
  private int colorsUniform;
  private int colorsCountUniform;

  private float rThreshold;
  private int maxIter;
  private int textureId;


  private float[] colorData = {
      0, 0, 0,
      1, 1, 0,
      0, 0, 1,
      0, 1, 0,
      1, 0, 0,
      1, 0.7f, 0,
      1, 1, 1,
      0, 0, 1,
      0, 0, 0.5f
  };

  private short[] elementData = {0, 2, 1, 1, 2, 3};

  private interface Buffer {
    int VERTEX = 0;
    int ELEMENT = 1;
    int GLOBAL_MATRICES = 2;
    int MAX = 3;
  }

  private IntBuffer bufferName = GLBuffers.newDirectIntBuffer(Buffer.MAX);
  private IntBuffer vertexArrayName = GLBuffers.newDirectIntBuffer(1);

  private FloatBuffer clearColor = GLBuffers.newDirectFloatBuffer(4);
  private FloatBuffer clearDepth = GLBuffers.newDirectFloatBuffer(1);

  private ByteBuffer globalMatricesPointer;

  private Program program;

  JuliaFractal(int sizeX, int sizeY) {
    this.sizeX = sizeX;
    this.sizeY = sizeY;
    size = Math.max(sizeX, sizeY);
  }


  void moveCenter(int x, int y) {
    center[0] += 2f * x / size / zoom;
    center[1] -= 2f * y / size / zoom;
  }

  void changeR(float r) {
    rThreshold = r;
  }

  void changeMaxIter(int n) {
    maxIter = n;
  }

  private float toCoordinate(int v, int size, float center, float zoom, int sign) {
    return sign * ((2f * v - size) / this.size / zoom - sign * center);
  }

  void zoom(float val, int x, int y) {
    float oldZoom = zoom;
    zoom *= val;

    center[0] += (toCoordinate(x, sizeX, center[0], zoom, 1) - toCoordinate(x, sizeX, center[0], oldZoom, 1));
    center[1] += (toCoordinate(y, sizeY, center[1], zoom, -1) - toCoordinate(y, sizeY, center[1], oldZoom, -1));
  }

  @Override
  public void init(GLAutoDrawable drawable) {

    GL4 gl = drawable.getGL().getGL4();

    initDebug(gl);

    initBuffers(gl);

    initVertexArray(gl);

    program = new Program(gl, "shaders/gl4", "hello-triangle", "hello-triangle");


    rThresholdUniform = gl.glGetUniformLocation(program.name, "rThreshold");
    maxIterUniform = gl.glGetUniformLocation(program.name, "maxIter");
    colorsUniform = gl.glGetUniformLocation(program.name, "colors");
    colorsCountUniform = gl.glGetUniformLocation(program.name, "colorsCount");

    textureId = create1DTexture(gl);

    gl.glEnable(GL_DEPTH_TEST);
  }

  private void initDebug(GL4 gl) {

    gl.glDebugMessageControl(
        GL_DONT_CARE,
        GL_DONT_CARE,
        GL_DONT_CARE,
        0,
        null,
        false);

    gl.glDebugMessageControl(
        GL_DONT_CARE,
        GL_DONT_CARE,
        GL_DEBUG_SEVERITY_HIGH,
        0,
        null,
        true);

    gl.glDebugMessageControl(
        GL_DONT_CARE,
        GL_DONT_CARE,
        GL_DEBUG_SEVERITY_MEDIUM,
        0,
        null,
        true);
  }

  private void initBuffers(GL4 gl) {

    FloatBuffer vertexBuffer = GLBuffers.newDirectFloatBuffer(vertexData);
    ShortBuffer elementBuffer = GLBuffers.newDirectShortBuffer(elementData);

    gl.glCreateBuffers(Buffer.MAX, bufferName);

    gl.glBindBuffer(GL_ARRAY_BUFFER, bufferName.get(Buffer.VERTEX));
    gl.glBufferStorage(GL_ARRAY_BUFFER, vertexBuffer.capacity() * Float.BYTES, vertexBuffer, 0);
    gl.glBindBuffer(GL_ARRAY_BUFFER, 0);

    gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferName.get(Buffer.ELEMENT));
    gl.glBufferStorage(GL_ELEMENT_ARRAY_BUFFER, elementBuffer.capacity() * Short.BYTES, elementBuffer, 0);
    gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

    IntBuffer uniformBufferOffset = GLBuffers.newDirectIntBuffer(1);
    gl.glGetIntegerv(GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT, uniformBufferOffset);
    int globalBlockSize = Math.max(16 * 4 * 2, uniformBufferOffset.get(0));

    gl.glBindBuffer(GL_UNIFORM_BUFFER, bufferName.get(Buffer.GLOBAL_MATRICES));
    gl.glBufferStorage(GL_UNIFORM_BUFFER, globalBlockSize, null, GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT);
    gl.glBindBuffer(GL_UNIFORM_BUFFER, 0);

    // map the transform buffers and keep them mapped
    globalMatricesPointer = gl.glMapNamedBufferRange(
        bufferName.get(Buffer.GLOBAL_MATRICES),
        0,
        16 * 4 * 2,
        GL_MAP_WRITE_BIT | GL_MAP_PERSISTENT_BIT | GL_MAP_COHERENT_BIT | GL_MAP_INVALIDATE_BUFFER_BIT); // flags

  }

  private int create1DTexture(GL4 gl) {
    IntBuffer textures = GLBuffers.newDirectIntBuffer(1);
    gl.glGenTextures(1, textures);

    gl.glBindTexture(GL_TEXTURE_1D, textures.get(0));

    gl.glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

    FloatBuffer colors = GLBuffers.newDirectFloatBuffer(colorData);

    gl.glTexImage1D(
        GL_TEXTURE_1D,
        0,
        GL_RGBA32F,
        colors.capacity() / 3,
        0,
        GL_RGB,
        GL_FLOAT,
        colors
    );

    // texture sampling/filtering operation.
    gl.glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_WRAP_S, GL_REPEAT);
    gl.glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_WRAP_T, GL_REPEAT);
    gl.glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    gl.glTexParameteri(GL_TEXTURE_1D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

    gl.glBindTexture(GL_TEXTURE_1D, 0);

    return textures.get(0);
  }

  private void initVertexArray(GL4 gl) {

    gl.glCreateVertexArrays(1, vertexArrayName);

    gl.glVertexArrayAttribBinding(vertexArrayName.get(0), Semantic.Attr.POSITION, Semantic.Stream.A);

    gl.glVertexArrayAttribFormat(vertexArrayName.get(0), Semantic.Attr.POSITION, 2, GL_FLOAT, false, 0);

    gl.glEnableVertexArrayAttrib(vertexArrayName.get(0), Semantic.Attr.POSITION);

    gl.glVertexArrayElementBuffer(vertexArrayName.get(0), bufferName.get(Buffer.ELEMENT));
    gl.glVertexArrayVertexBuffer(vertexArrayName.get(0), Semantic.Stream.A, bufferName.get(Buffer.VERTEX), 0, 2 * 4);
  }

  @Override
  public void display(GLAutoDrawable drawable) {

    GL4 gl = drawable.getGL().getGL4();


    // view matrix
    {
      float[] view = FloatUtil.makeScale(new float[16], false, zoom, zoom, zoom);
      view[4 * 3] = center[0] * zoom;
      view[4 * 3 + 1] = center[1] * zoom;
      for (int i = 0; i < 16; i++)
        globalMatricesPointer.putFloat(16 * 4 + i * 4, view[i]);
    }


    gl.glClearBufferfv(GL_COLOR, 0, clearColor.put(0, colorData[0]).put(1, colorData[1]).put(2, colorData[2]).put(3, 1f));
    gl.glClearBufferfv(GL_DEPTH, 0, clearDepth.put(0, 1f));


    gl.glUseProgram(program.name);
    gl.glBindVertexArray(vertexArrayName.get(0));

    // values
    {
      gl.glUniform1i(maxIterUniform, maxIter);
      gl.glUniform1f(rThresholdUniform, rThreshold);
      int currentTextureUnit_ = 0;
      gl.glUniform1i(colorsUniform, currentTextureUnit_);

      gl.glActiveTexture(GL_TEXTURE0 + currentTextureUnit_);
      gl.glBindTexture(GL_TEXTURE_1D, textureId);

      gl.glUniform1i(colorsCountUniform, colorData.length / 3);
    }

    gl.glBindBufferBase(
        GL_UNIFORM_BUFFER,
        Semantic.Uniform.TRANSFORM0,
        bufferName.get(Buffer.GLOBAL_MATRICES));

    gl.glDrawElements(
        GL_TRIANGLES,
        elementData.length,
        GL_UNSIGNED_SHORT,
        0);

    gl.glUseProgram(0);
    gl.glBindVertexArray(0);
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    GL4 gl = drawable.getGL().getGL4();

    sizeX = width;
    sizeY = height;
    size = Math.max(sizeX, sizeY);
    float right = 1f * width / size;
    float top = 1f * height / size;
    float[] ortho = FloatUtil.makeOrtho(new float[16], 0, false, -right, right, -top, top, 1f, -1f);
    globalMatricesPointer.asFloatBuffer().put(ortho);

    gl.glViewport(x, y, width, height);
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {

    GL4 gl = drawable.getGL().getGL4();

    gl.glUnmapNamedBuffer(bufferName.get(Buffer.GLOBAL_MATRICES));

    gl.glDeleteProgram(program.name);
    gl.glDeleteVertexArrays(1, vertexArrayName);
    gl.glDeleteBuffers(Buffer.MAX, bufferName);
  }

  private static class Program {

    int name;

    Program(GL4 gl, String root, String vertex, String fragment) {
      ShaderCode vertShader = ShaderCode.create(gl, GL_VERTEX_SHADER, this.getClass(), root, null, vertex,
          "vert", null, true);
      ShaderCode fragShader = ShaderCode.create(gl, GL_FRAGMENT_SHADER, this.getClass(), root, null, fragment,
          "frag", null, true);

      ShaderProgram shaderProgram = new ShaderProgram();

      shaderProgram.add(vertShader);
      shaderProgram.add(fragShader);

      shaderProgram.init(gl);

      name = shaderProgram.program();

      shaderProgram.link(gl, System.err);
    }
  }

}