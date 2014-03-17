package sphere3d;

import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.GLBuffers;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.ByteBuffer;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 *
 * @author Christophe
 * 
 * affichage du z-buffer à l'aide d'un framebuffer avec rendu sur texture
 */
public class AnimatedZBuffer implements GLEventListener {

    private GLU glu;

    private int frameBufferID[];
    private int depthBufferID[];
    int renderedTexture[];

    private static int width;
    private static int height;
    boolean firstTime;

    private double theta = 0;
    private double s = 0;
    private double c = 0;

    ByteBuffer pixels;
    float rotateT;

    public AnimatedZBuffer(int width, int height) {
        this.width = height;
        this.height = height;
        firstTime = true;
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();         // get the OpenGL graphics context
        glu = new GLU();                            // get GL Utilities
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);    // set background (clear) color
        gl.glClearDepth(1.0f);                      // set clear depth value to farthest
        gl.glEnable(GL2.GL_DEPTH_TEST);                 // enables depth testing
        gl.glDepthFunc(GL2.GL_LEQUAL);                  // the type of depth test to do
        gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL2.GL_NICEST); // best perspective correction
        gl.glShadeModel(GL2.GL_SMOOTH);                 // blends colors nicely, and smoothes out lighting
        //gl.glDepthRange(0f,0.3f);

//        float[] no_mat = {0.0F, 0.0F, 0.0F, 1.0F};
//        float[] mat_ambient = {0.7F, 0.4F, 0.7F, 1.0F};
//        float[] mat_ambient_color = {0.8F, 0.5F, 0.2F, 1.0F};
//        float[] mat_diffuse = {0.1F, 0.5F, 0.8F, 1.0F};
//        float[] mat_specular = {1.0F, 0.7F, 1.0F, 1.0F};
//        float[] no_shininess = {0.0F};
//        float[] low_shininess = {5.0F};
//        float[] high_shininess = {100.0F};
//        float[] mat_emission = {0.3F, 0.2F, 0.2F, 0.0F};
//
//        float SHINE_ALL_DIRECTIONS = 1;
//        float[] lightPos = {0, 10, 10, SHINE_ALL_DIRECTIONS};
//        float[] lightColorAmbient = {0.2f, 0.2f, 0.2f, 1f};
//        float[] lightColorSpecular = {0.8f, 0.8f, 0.8f, 1f};
//
//        // Set light parameters.
//        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);
//        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightColorAmbient, 0);
//        gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightColorSpecular, 0);
//
//        // Enable lighting in GL.
//        gl.glEnable(GL2.GL_LIGHT1);
//        gl.glEnable(GL2.GL_LIGHTING);
//
//        // Set material properties.
//        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_AMBIENT, mat_ambient, 0);
//        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_DIFFUSE, mat_diffuse, 0);
//        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SPECULAR, mat_specular, 0);
//        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_SHININESS, low_shininess, 0);
//        gl.glMaterialfv(GL2.GL_FRONT, GL2.GL_EMISSION, no_mat, 0);
        
        initFBO(gl);
    }

    private void initFBO(GL2 gl) {
        frameBufferID = new int[1];
        gl.glGenFramebuffers(1, frameBufferID, 0);
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, frameBufferID[0]);

        renderedTexture = new int[2];
        gl.glGenTextures(1, renderedTexture, 0);

        // "Bind" the newly created texture : all future texture functions will modify this texture
        gl.glBindTexture(GL2.GL_TEXTURE_2D, renderedTexture[0]);

        gl.glTexImage2D(GL2.GL_TEXTURE_2D, // target texture type
                0, // mipmap LOD level
                GL2.GL_DEPTH_COMPONENT24, // internal pixel format
                //GL2.GL_DEPTH_COMPONENT
                width, // width of generated image
                height, // height of generated image
                0, // border of image
                GL2.GL_DEPTH_COMPONENT, // external pixel format 
                GL2.GL_FLOAT, // datatype for each value
                null);

        // Poor filtering
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);

        depthBufferID = new int[1];
        gl.glGenRenderbuffers(1, depthBufferID, 0);
        gl.glBindRenderbuffer(GL2.GL_RENDERBUFFER, depthBufferID[0]);

        gl.glRenderbufferStorage(GL2.GL_RENDERBUFFER, GL2.GL_DEPTH_COMPONENT, width, height);
        gl.glFramebufferRenderbuffer(GL2.GL_FRAMEBUFFER, GL2.GL_DEPTH_ATTACHMENT, GL2.GL_RENDERBUFFER, depthBufferID[0]);

        gl.glFramebufferTexture2D(GL2.GL_FRAMEBUFFER, GL2.GL_COLOR_ATTACHMENT0, GL2.GL_TEXTURE_2D, renderedTexture[0], 0);

        // Set the list of draw buffers.
        int[] DrawBuffers = {GL2.GL_COLOR_ATTACHMENT0};
        gl.glDrawBuffers(1, DrawBuffers, 0); // "1" is the size of DrawBuffers

        //Disable color buffer
        //http://stackoverflow.com/questions/12546368/render-the-depth-buffer-into-a-texture-using-a-frame-buffer
        gl.glDrawBuffer(GL2.GL_NONE);
        gl.glReadBuffer(GL2.GL_NONE);
        pixels = GLBuffers.newDirectByteBuffer(width * height * 4);
        // Always check that our framebuffer is ok.
        int status = gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER);
        if (gl.glCheckFramebufferStatus(GL2.GL_FRAMEBUFFER) != GL2.GL_FRAMEBUFFER_COMPLETE) {
            System.err.println("Can not use FBO! Status error:" + status);
        }
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        update();
        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();  // reset the model-view matrix
        // ----- Your OpenGL rendering code here (Render a white triangle for testing) -----
        // Render to our framebuffer
//        if (firstTime) {
//            firstTime = false;
        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, frameBufferID[0]);
//            gl.glViewport(0, 0, width, height); // Render on the whole framebuffer, complete from the lower left corner to the upper right
//            Render scene into Frame buffer first
        render(drawable);

        //Read pixels from buffer
        gl.glBindFramebuffer(GL2.GL_READ_FRAMEBUFFER, frameBufferID[0]);
        //Read pixels 
        gl.glReadPixels(0, 0, width, height, GL2.GL_DEPTH_COMPONENT, GL2.GL_FLOAT, pixels);
//            for (int y = 0; y < height / 1; y++) {
//                for (int x = 0; x < width / 1; x++) {
//                    //System.out.printf(" %.3f",pixels.get(x+height*y));
//                    System.out.printf(" %.3f", 1. / pixels.get(x + height * y));
//                    //System.out.printf(" %.3f",Math.log(pixels.get(x+height*y)));
//                }
//                System.out.println("");
//            }
//        }

        gl.glBindFramebuffer(GL2.GL_FRAMEBUFFER, 0);
        //gl.glViewport(width / 2, 0, width / 2, height / 2);
        render(drawable);
        //gl.glViewport(0, 0, width / 2, height / 2);
        //gl.glPixelZoom(width, height);
        gl.glDrawPixels(width , height , GL2.GL_LUMINANCE, GL2.GL_FLOAT, pixels);



    }

    private void update() {
//        theta += 0.01;
//        s = Math.sin(theta);
//        c = Math.cos(theta);
    }

    private void render(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT); // clear color and depth buffers
        gl.glLoadIdentity();  // reset the model-view matrix

//        GLUquadric qobj0 = glu.gluNewQuadric();
//        gl.glPushMatrix();
//        gl.glColor3f(0, 0, 1);
//        gl.glTranslatef(-4f, 0f, -30f);
//        glu.gluSphere(qobj0, 2f, 20, 20);
//        gl.glPopMatrix();
//
//        gl.glPushMatrix();
//        gl.glColor3f(0, 1, 0);
//        gl.glTranslatef(0f, 0f, -20f);
//        glu.gluSphere(qobj0, 2f, 20, 20);
//        gl.glPopMatrix();
//
//        gl.glPushMatrix();
//        gl.glColor3f(1, 0, 0);
//        gl.glTranslatef(2f, 0f, -10f);
//        glu.gluSphere(qobj0, 2f, 20, 20);
//        gl.glPopMatrix();
        //gl.glTranslatef(0.0f, 0.0f, -5.0f);
        // Gestion de l'eclairage
        // rotate on the three axis
        //gl.glRotatef(this.rotateT, 1.0f, 0.0f, 0.0f);
        //gl.glRotatef(this.rotateT, 0.0f, 0.0f, 1.0f);
        // Draw sphere 
        GLUquadric qobj0 = glu.gluNewQuadric();
        gl.glPushMatrix();
        gl.glColor3f(1, 1, 1);
        gl.glTranslatef(0f, 0f, -10.f);
        glu.gluSphere(qobj0, 1.f, 100, 100);
        gl.glPopMatrix();

        
        GLUquadric qobj1 = glu.gluNewQuadric();
        gl.glPushMatrix();
        gl.glRotatef(this.rotateT, 1f, 0f, -5f);
        gl.glColor3f(1, 1, 1);
        gl.glTranslatef(0f, 0f, -10.f);
        glu.gluSphere(qobj1, 1.f, 100, 100);
        gl.glPopMatrix();

        // Done Drawing 
        //gl.glEnd();

        // increasing rotation for the next iteration                   
        this.rotateT += 1f;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL2 gl = drawable.getGL().getGL2();  // get the OpenGL 2 graphics context
        if (height == 0) {
            height = 1;   // prevent divide by zero
        }
        float aspect = (float) width / height;

        // Set the view port (display area) to cover the entire window
        gl.glViewport(0, 0, width, height);

        // Setup perspective projection, with aspect ratio matches viewport
        gl.glMatrixMode(GL2.GL_PROJECTION);  // choose projection matrix
        gl.glLoadIdentity();             // reset projection matrix
        //glu.gluPerspective(90.0, aspect, 0f, 1000f); // fovy, aspect, zNear, zFar
        final float fh = 0.5f;
        final float fw = fh * aspect;
        gl.glFrustumf(-fw, fw, -fh, fh, 1f, 1000.0f);
        // Enable the model-view transform
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity(); // reset
    }

    public static void main(String[] args) {

        GLProfile glp = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(glp);
        GLCanvas canvas = new GLCanvas(caps);

        AnimatedZBuffer s3d = new AnimatedZBuffer(800, 600);

        Frame frame = new Frame();
        frame.setSize(width, height);
        frame.add(canvas);

        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        }
        );

        canvas.addGLEventListener(s3d);

        FPSAnimator animator = new FPSAnimator(canvas, 60); 
        //animator.add(canvas);
        animator.start();
    }
}
