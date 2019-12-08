package com.example.a3520096.myapplication;

import android.annotation.SuppressLint;
import android.graphics.*;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.content.*;
import android.util.*;
import android.view.WindowManager;

import java.util.ListIterator;

public class TouchBoard extends SurfaceView implements SurfaceHolder.Callback {
        static final int size_of_instant=10;
        TheApplication app;
        int circ_siz;

        static String[] note={"DO","DO#","RE","RE#","MI","FA","FA#","SOL","SOL#","LA","LA#","SI"};
        LinearGradient grad=new LinearGradient(0,0,0,getHeight(),0xff009688,0xffa0CCC4,Shader.TileMode.REPEAT);
        Paint p=new Paint();

        public TouchBoard(Context context) {
            super(context);
            getHolder().addCallback(this);
            getHolder().setFormat(PixelFormat.RGBA_8888);
            app = (TheApplication) (context.getApplicationContext());
            WindowManager wm = (WindowManager) app.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            circ_siz = 110;
        }
        public TouchBoard(Context context, AttributeSet as) {
            super(context,as);
            getHolder().addCallback(this);
            getHolder().setFormat(PixelFormat.RGBA_8888);
            app = (TheApplication) (context.getApplicationContext());
            WindowManager wm = (WindowManager) app.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            circ_siz = 110;
        }
        @SuppressLint("WrongCall")
        public void reDraw() {
            Canvas c = getHolder().lockCanvas();
            if (c != null) {
                this.onDraw(c);
                getHolder().unlockCanvasAndPost(c);
            }

        }

        @Override
        public void onDraw(Canvas c) {
            Model m = app.getModel();
            ListIterator<Position> it = m.getAll();
            ListIterator<Integer> itD = m.getAllD();
            int relatif = m.getRelatif();
            int col=155;
            int lig=140;
            int vertical = 0;
            int hauteur=getWidth();
            int nb_cercles=hauteur/12;
            if(getWidth()>getHeight()) {
                vertical = m.getVertical();
                lig=getHeight()/6;
                hauteur=getHeight();
            }
            p.setShader(grad);
            c.drawPaint(p);
            p.reset();
            int i, j;

            for (j = 0; j <= hauteur * size_of_instant; j += col) {
//--------------------------------BARRE DU BAS AVEC LES CHIFFRES-----------------------------------------//
                for (i = circ_siz*24; i <= getHeight(); i++) {
                    p.setColor(Color.argb(255,160,204,196));
                    c.drawLine(j,i,hauteur,i,p);
                    p.setColor(Color.BLACK);
                }
                p.setStyle(Paint.Style.FILL);
                p.setTypeface(Typeface.SERIF);
                p.setTextSize(circ_siz);
                c.drawText(Integer.toString(j / col), j + (circ_siz/2) - (relatif * col / 10), getHeight(), p);
//-------------------------------------------------------------------------------------------------------//
//---------------------------------------------CERCLES--------------------------------------------------//
                int tmp = 0;
                for (i = 0; i < 12; i ++) {

                        p.setStyle(Paint.Style.STROKE);
                        c.drawCircle(j + circ_siz - (relatif * col / 10), tmp + circ_siz -(vertical * lig /10), circ_siz/2, p);
                        tmp +=(nb_cercles+40);//20 correspond a l'espace entre les cercles sur la hauteur
                }
            }
//-------------------------------------------------------------------------------------------------------//
//-----------------------------------------ECRITURE DES NOTES--------------------------------------------//
            int tmp = 0;
            for (i = 0; i < 12; i ++) {
                p.setStyle(Paint.Style.FILL);
                p.setTextSize(circ_siz/2);
                p.setTypeface(Typeface.SERIF);
                c.drawText(note[i], getWidth()-circ_siz,tmp+circ_siz-(vertical * lig /10)+10 , p);
                tmp+=(nb_cercles+40);
            }
//-------------------------------------------------------------------------------------------------------//
//-------------------------------------------CERCLES PLEINS----------------------------------------------//
            while ((it.hasNext()) && (itD.hasNext())) {
                Position xy = it.next();
                int d = itD.next();
                p.setColor(Color.BLACK);
                int X=xy.getX();
                int Y=xy.getY();

                c.drawCircle(X - (relatif * col / 10)+9, Y -(vertical * lig /10)+7, circ_siz/2+1, p);
                p.setStrokeWidth(10);
                c.drawLine(X - (relatif * col / 10)+9, Y -(vertical * lig /10)+6, X + ((d - 1) * col) - (relatif * col / 10)+9, Y-(vertical * lig /10)+6, p);
                c.drawCircle(X - (relatif * col / 10)+ ((d - 1) * col)+9, Y -(vertical * lig /10)+7, circ_siz/4, p);
            }
        }
//-------------------------------------------------------------------------------------------------------//

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
        }

        @Override
        public void surfaceChanged (SurfaceHolder holder, int format, int width, int height) {
            reDraw();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder){
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            Model m = app.getModel();
            int relatif=m.getRelatif();
            int vertical=m.getVertical();
            int col=155;
            int lig=130;
            int x = (int) event.getX()+(relatif*col/10);//le + relatif si on slide vers la droite
            int y = (int) event.getY()+(vertical*lig/10);//le + relatif si on slide vers le bas
            int action = event.getAction();
            ListIterator<Position> xys;
            ListIterator<Position> occupe;
            ListIterator<Integer> duree;
            int hauteur=getHeight();
            int largeur=getWidth();
            if(getWidth()>getHeight()) {
                hauteur=getWidth();
                largeur=getHeight();
            }
            switch (action) {

                case MotionEvent.ACTION_DOWN:
                    xys=m.getAll();//recupere toute les positions
                    duree=m.getAllD();
                    occupe=m.getAllO();
                    int posx;
                    //if(getWidth()>getHeight()) posx=-95;
                    int posy;
                    posx=x/col;//Donne l'abscisse en fonction du nombre de case
                    posy=y/lig;
                    if(y>12*lig) return false;
                    while (xys.hasNext()) {
                        int index = xys.nextIndex();
                        Position pos = xys.next();
                        int temp;
                        if (pos.getY()/lig == posy && pos.getX()/col == posx) {
                            xys.remove();
                            temp = 0;
                            while ((temp <= index) && (duree.hasNext())) {
                                int dutmp=duree.next();
                                if (temp == index) {
                                    while(occupe.hasNext()){
                                        Position poss = occupe.next();
                                        for(int k=0;k<dutmp;k++){
                                            if (poss.getX()==posx+(k*col)){
                                                occupe.remove();
                                                occupe=m.getAllO();
                                            }
                                        }
                                    }
                                    duree.remove();
                                }
                                temp++;
                            }
                            reDraw();
                            return true;
                        }
                    }
                    while ( occupe.hasNext()){
                        Position pos = occupe.next();
                        if (pos.getY()/lig == posy && pos.getX()/col == posx) {
                            return false;
                        }
                    }

                    m.add(posx*col+100, posy*lig+100);//*col pour multiplier la case par la taille de la case
                    m.addD(1);
                    reDraw();
                    return true;/*

/*

 */
                case MotionEvent.ACTION_UP:
                    xys=m.getAll();
                    duree=m.getAllD();
                    Position last_pos=null;
                    while(xys.hasNext())
                        last_pos=xys.next();

                    if (last_pos==null) return false;
                    int last_y=last_pos.getY();
                    int last_x=last_pos.getX();

                    posx=-53;
                    if(getWidth()>getHeight()) posx=-95;
                    for(int i=1;i<=hauteur*size_of_instant/col;i++) {
                        posx += col;
                        posy = -29;
                        if(getWidth()>getHeight()) posy=-36;
                        for (int j = 1; j <= largeur / lig; j++) {
                            posy += lig;
                            if ((x/col < i) && (y/lig < j) && (last_y/lig == y/lig) && (last_x/col<x/col)) {
                                if (m.contains(last_x,last_y)) return false;
                                else {
                                    while(duree.hasNext())
                                        duree.next();
                                    duree.set((x/col-last_x/col+1));
                                    for(int k=0;k<(x/col-last_x/col+1);k++){
                                        m.addO(posx-(k*col),posy);
                                    }
                                    reDraw();
                                    return true;
                                }
                            }
                        }
                    }
                default:
                    return false;
            }
        }

}
