package alexsander.da.hora.bouncingball;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by alex1_000 on 26/05/2016.
 */
public class Ball extends SurfaceView implements SurfaceHolder.Callback {
    private BouncingBallAnimationThread bbThread = null;

    private int xPosition = getWidth()/2;
    private int yPosition = getHeight()/2;
    private int xDirection = 10;
    private int yDirection = 5;
    private static int radius = 20;
    private static int ballColor = Color.BLUE;

    public Ball(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        getHolder().addCallback(this);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawRect(0,0,getWidth(),getHeight(),paint);
        paint.setColor(ballColor);
        canvas.drawCircle(xPosition, yPosition, radius, paint);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (bbThread != null)
            return;

        bbThread = new BouncingBallAnimationThread(getHolder());
        bbThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        bbThread.stop = true;
    }

    @SuppressLint("WrongCall")
    private class BouncingBallAnimationThread extends Thread{
        public boolean stop = false;
        private SurfaceHolder surfaceHolder;

        public BouncingBallAnimationThread(SurfaceHolder surfaceHolder) {
            this.surfaceHolder = surfaceHolder;
        }

        public void run() {
            while (!stop) {
                xPosition += xDirection;
                yPosition += yDirection;

                if (xPosition < 0) {
                    xDirection = -xDirection;
                    xPosition = radius;
                }

                if (xPosition > getWidth() - radius) {
                    xDirection = -xDirection;
                    xPosition = getWidth() - radius;
                }

                if (yPosition < 0) {
                    yDirection = -yDirection;
                    yPosition = radius;
                }

                if (yPosition > getHeight() - radius) {
                    yDirection = -yDirection;
                    yPosition = getHeight() - radius - 1;
                }

                Canvas c = null;

                try{
                    c = surfaceHolder.lockCanvas(null);

                    synchronized (surfaceHolder) {
                        onDraw(c);
                    }
                } finally {
                    if (c != null) {
                        surfaceHolder.unlockCanvasAndPost(c);
                    }
                }
            }
        }
    }
}
