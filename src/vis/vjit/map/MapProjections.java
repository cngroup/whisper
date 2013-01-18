package vis.vjit.map;
// Decompiled by DJ v3.12.12.96 Copyright 2011 Atanas Neshkov  Date: 2012-3-8 18:05:37
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   MapProjections.java

import java.applet.Applet;
import java.awt.*;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

/***
 * 
 * This piece of code is a joint research between HKUST and Harvard University. 
 * It is based on the CPL opensource license. Please check the 
 * term before using.
 * 
 * The paper is published in InfoVIs 2013: 
 * "Whisper: Tracing the Spatiotemporal Process of Information Diffusion in Real Time"
 * 
 * Visit Whisper's main website here : whipserseer.com
 * 
 * @author NanCao(nancao@gmail.com)
 *
 */
public class MapProjections extends Applet
{

    public MapProjections()
    {
        centreChoice = new Choice();
        projChoice = new Choice();
        eastBox = new TextField(3);
        northBox = new TextField(3);
        rotBox = new TextField(3);
        uChoose = new Button("You choose");
    }

    public void init()
    {
        projChoice.addItem("Longitude Latitude");
        projChoice.addItem("Mercator");
        projChoice.addItem("Cylindrical Equal Area");
        projChoice.addItem("Mollweide");
        projChoice.addItem("Sinusoidal");
        projChoice.addItem("Baar equal area");
        projChoice.addItem("Azimuthal Equal Area");
        projChoice.addItem("Azimuthal Distance");
        projChoice.addItem("Azimuthal Orthographic");
        projChoice.addItem("Azimuthal Stereographic");
        projChoice.addItem("Azimuthal Gnomonic");
        projChoice.addItem("Conic Distance (fat)");
        projChoice.addItem("Conic Distance (thin)");
        projChoice.addItem("Heart Equal Area");
        projChoice.addItem("Bonne");
        projChoice.addItem("Triangle Equal Area");
        add(projChoice);
        proJection = "Longitude Latitude";
        centreChoice.addItem("Standard");
        centreChoice.addItem("North pole");
        centreChoice.addItem("South pole");
        centreChoice.addItem("Pacific");
        centreChoice.addItem("Random");
        add(centreChoice);
        projCentre = "Standard";
        add(eastBox);
        add(northBox);
        add(rotBox);
        eastVal = 0.0D;
        northVal = 0.0D;
        rotVal = 0.0D;
        add(uChoose);
        imgName = getParameter("basicimg");
        if(imgName == null)
            imgName = "world.gif";
        MediaTracker mediatracker = new MediaTracker(this);
        img = getImage(getCodeBase(), imgName);
        mediatracker.addImage(img, 0);
        try
        {
            mediatracker.waitForAll();
        }
        catch(InterruptedException interruptedexception) { }
        endx = imgWidth = img.getWidth(null);
        endy = imgHeight = img.getHeight(null);
        imgTotal = imgWidth * imgHeight;
        pixels = new int[imgTotal];
        PixelGrabber pixelgrabber = new PixelGrabber(img, 0, 0, imgWidth, imgHeight, pixels, 0, imgWidth);
        try
        {
            pixelgrabber.grabPixels();
        }
        catch(InterruptedException interruptedexception1) { }
    }

    public void paint(Graphics g)
    {
        int ai[] = newmap(pixels, imgWidth, imgHeight, endx, endy, eastVal, northVal, rotVal, proJection);
        Image image = createImage(new MemoryImageSource(endx, endy, ai, 0, endx));
        eastVal -= 6.2831853071795862D * Math.rint((0.5D * eastVal) / 3.1415926535897931D);
        northVal -= 6.2831853071795862D * Math.rint((0.5D * northVal) / 3.1415926535897931D);
        rotVal -= 6.2831853071795862D * Math.rint((0.5D * rotVal) / 3.1415926535897931D);
        eastBox.setText(Long.toString(Math.round(eastVal / 0.017453292519943295D)));
        northBox.setText(Long.toString(Math.round(northVal / 0.017453292519943295D)));
        rotBox.setText(Long.toString(Math.round(rotVal / 0.017453292519943295D)));
        g.drawImage(image, 0, 45, null);
        g.setColor(Color.white);
        g.fillRect(0, 0, size().width, 45);
        g.fillRect(0, endy + 45, size().width, size().height - endy - 45);
        g.fillRect(endx, 45, size().width - endx, endy);
        g.setColor(Color.black);
        g.drawString("Projection", 310, 40);
        g.drawString("Centre", 450, 40);
        g.drawString("(East     North    Direction)", 520, 40);
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public boolean action(Event event, Object obj)
    {
        if(event.target == projChoice)
        {
            proJection = (String)obj;
            if(proJection == "Cylindrical Equal Area")
            {
                endx = imgWidth;
                endy = (int)((double)endx / 3.1415926535897931D);
            } else
            if(proJection == "Baar equal area")
            {
                endy = imgHeight;
                endx = endy;
            } else
            if(proJection == "Azimuthal Distance" || proJection == "Azimuthal Equal Area" || proJection == "Heart Equal Area")
            {
                endy = imgHeight;
                endx = endy;
            } else
            if(proJection == "Conic Distance (fat)" || proJection == "Conic Distance (thin)" || proJection == "Bonne")
            {
                endy = imgHeight;
                endx = (4 * endy) / 3;
            } else
            {
                endx = imgWidth;
                endy = endx / 2;
            }
        } else
        if(event.target == centreChoice)
        {
            projCentre = (String)obj;
            if(projCentre == "Standard")
            {
                eastVal = 0.0D;
                northVal = 0.0D;
                rotVal = 0.0D;
            } else
            if(projCentre == "Pacific")
            {
                eastVal = 3.1415926535897931D;
                northVal = 0.0D;
                rotVal = 3.1415926535897931D;
            } else
            if(projCentre == "North pole")
            {
                eastVal = 0.0D;
                northVal = 1.5707963267948966D;
                rotVal = 0.0D;
            } else
            if(projCentre == "South pole")
            {
                eastVal = 0.0D;
                northVal = -1.5707963267948966D;
                rotVal = 0.0D;
            } else
            if(projCentre == "Random")
            {
                eastVal = 3.1415926535897931D * (Math.random() * 2D - 1.0D);
                northVal = Math.asin(Math.random() * 2D - 1.0D);
                rotVal = 3.1415926535897931D * (Math.random() * 2D - 1.0D);
            } else
            {
                return false;
            }
        } else
        if(event.target == uChoose)
        {
            eastVal = 0.017453292519943295D * Double.valueOf(eastBox.getText().trim()).doubleValue();
            northVal = 0.017453292519943295D * Double.valueOf(northBox.getText().trim()).doubleValue();
            rotVal = 0.017453292519943295D * Double.valueOf(rotBox.getText().trim()).doubleValue();
        } else
        {
            return false;
        }
        repaint();
        return true;
    }

    public boolean mouseDown(Event event, int i, int j)
    {
        endx = i;
        endy = j - 45;
        if(proJection == "Azimuthal Orthographic" || proJection == "Sinusoidal")
        {
            if(2 * endy > endx)
                endx = 2 * endy;
            if(2 * endy < endx)
                endy = endx / 2;
        } else
        if(proJection == "Azimuthal Distance" || proJection == "Azimuthal Equal Area")
        {
            if(endy > endx)
                endx = endy;
            if(endy < endx)
                endy = endx;
        } else
        if(proJection == "Conic Distance (fat)" || proJection == "Heart Equal Area" || proJection == "Bonne")
        {
            if(endy > endx)
                endx = endy;
            if(2 * endy < endx)
                endy = endx / 2;
        } else
        if(proJection == "Conic Distance (thin)" && 2 * endy < endx)
            endy = endx / 2;
        repaint();
        return true;
    }

    public boolean mouseDrag(Event event, int i, int j)
    {
        return mouseDown(event, i, j);
    }

    public int[] newmap(int ai[], int i, int j, int k, int l, double d, 
            double d1, double d2, String s)
    {
        int ai1[] = new int[k * l];
        double d3 = (double)i / (double)k;
        double d4 = (double)j / (double)l;
        double d5 = (double)k / (double)l;
        double d6 = Math.cos(d1);
        double d7 = Math.sin(d1);
        double d8 = Math.cos(d);
        double d9 = Math.sin(d);
        double d10 = Math.cos(d2);
        double d11 = Math.sin(d2);
        double d12 = d10 * d8 + d11 * d7 * d9;
        double d13 = -d11 * d8 + d10 * d7 * d9;
        double d14 = d6 * d9;
        double d15 = d11 * d6;
        double d16 = d10 * d6;
        double d17 = -d7;
        double d18 = d11 * d7 * d8 - d10 * d9;
        double d19 = d10 * d7 * d8 + d11 * d9;
        double d20 = d6 * d8;
        if(s == "Longitude Latitude")
        {
            for(int i1 = l; --i1 >= 0;)
            {
                int k3 = (int)((double)i1 * d4);
                int i4 = i1 * k;
                for(int j5 = k; --j5 >= 0;)
                {
                    int j7 = (int)((double)j5 * d3);
                    ai1[i4 + j5] = ai[obliqueRef(j7, k3, i, j, d12, d13, d14, d15, d16, d17, d18, d19, d20)];
                }

            }

        } else
        if(s == "Mercator")
        {
            double d21 = 3.1415926535897931D / d5;
            for(int j4 = l; --j4 >= 0;)
            {
                double d38 = (2D * (double)j4) / (double)l - 1.0D;
                int j9 = (int)(1.0D * (double)j * ((2D * Math.atan(Math.exp(d38 * d21))) / 3.1415926535897931D));
                int j11 = j4 * k;
                for(int j13 = k; --j13 >= 0;)
                {
                    int i14 = (int)((double)j13 * d3);
                    ai1[j11 + j13] = ai[obliqueRef(i14, j9, i, j, d12, d13, d14, d15, d16, d17, d18, d19, d20)];
                }

            }

        } else
        if(s == "Cylindrical Equal Area")
        {
            for(int j1 = l; --j1 >= 0;)
            {
                double d27 = (2D * (double)j1) / (double)l - 1.0D;
                int k5 = (int)(1.0D * (double)j * (Math.asin(d27) / 3.1415926535897931D + 0.5D));
                int k7 = j1 * k;
                for(int k9 = k; --k9 >= 0;)
                {
                    int k11 = (int)((double)k9 * d3);
                    ai1[k7 + k9] = ai[obliqueRef(k11, k5, i, j, d12, d13, d14, d15, d16, d17, d18, d19, d20)];
                }

            }

        } else
        if(s == "Sinusoidal")
        {
            for(int k1 = l; --k1 >= 0;)
            {
                int l3 = (int)((double)k1 * d4);
                double d35 = (2D * (double)k1) / (double)l - 1.0D;
                double d43 = Math.cos((d35 * 3.1415926535897931D) / 2D);
                int l11 = k1 * k;
                for(int k13 = k; --k13 >= 0;)
                {
                    double d61 = (2D * (double)k13) / (double)k - 1.0D;
                    double d68 = 1.0D + d61 / d43;
                    if(d68 < 2D && d68 > 0.0D)
                    {
                        int j18 = (int)(0.5D * (double)i * d68);
                        ai1[l11 + k13] = ai[obliqueRef(j18, l3, i, j, d12, d13, d14, d15, d16, d17, d18, d19, d20)];
                    } else
                    {
                        ai1[l11 + k13] = 0xff000001;
                    }
                }

            }

        } else
        if(s == "Mollweide")
        {
            for(int l1 = l; --l1 >= 0;)
            {
                double d28 = (2D * (double)l1) / (double)l - 1.0D;
                double d39 = Math.sqrt(1.0D - d28 * d28);
                int l9 = (int)(1.0D * (double)j * (Math.asin((2D * (Math.asin(d28) + d28 * d39)) / 3.1415926535897931D) / 3.1415926535897931D + 0.5D));
                int i12 = l1 * k;
                for(int l13 = k; --l13 >= 0;)
                {
                    double d62 = (2D * (double)l13) / (double)k - 1.0D;
                    double d69 = 1.0D + d62 / d39;
                    if(d69 < 2D && d69 > 0.0D)
                    {
                        int k18 = (int)(0.5D * (double)i * d69);
                        ai1[i12 + l13] = ai[obliqueRef(k18, l9, i, j, d12, d13, d14, d15, d16, d17, d18, d19, d20)];
                    } else
                    {
                        ai1[i12 + l13] = 0xff000001;
                    }
                }

            }

        } else
        if(s == "Baar equal area")
        {
            double d22 = Math.exp(1.0D / (d3 * d3 * d3 * d3));
            for(int k4 = l; --k4 >= 0;)
            {
                double d40 = (2D * (double)k4) / (double)l - 1.0D;
                int i10 = (int)(0.5D * (double)j * (Math.asin(d40 / d22) / Math.asin(1.0D / d22) + 1.0D));
                double d50 = Math.cos((d40 * 3.1415926535897931D) / 2D) / Math.cos((d40 * 3.1415926535897931D) / (2D * d22));
                int j14 = k4 * k;
                for(int k14 = k; --k14 >= 0;)
                {
                    double d70 = (2D * (double)k14) / (double)k - 1.0D;
                    double d74 = 1.0D + d70 / d50;
                    if(d74 < 2D && d74 > 0.0D)
                    {
                        int k19 = (int)(0.5D * (double)i * d74);
                        ai1[j14 + k14] = ai[obliqueRef(k19, i10, i, j, d12, d13, d14, d15, d16, d17, d18, d19, d20)];
                    } else
                    {
                        ai1[j14 + k14] = 0xff000001;
                    }
                }

            }

        } else
        if(s == "Azimuthal Distance")
        {
            for(int i2 = l; --i2 >= 0;)
            {
                double d29 = (2D * (double)i2) / (double)l - 1.0D;
                int l5 = i2 * k;
                for(int l7 = k; --l7 >= 0;)
                {
                    double d46 = (2D * (double)l7) / (double)k - 1.0D;
                    double d53 = Math.sqrt(d46 * d46 + d29 * d29);
                    if(d53 < 1.0D)
                    {
                        int l14 = (int)(0.5D * (double)i * (Math.atan2(d46, d29) / 3.1415926535897931D + 1.0D));
                        int i16 = (int)(1.0D * (double)j * d53);
                        ai1[l5 + l7] = ai[obliqueRef(l14, i16, i, j, d12, -d14, d13, d15, -d17, d16, d18, -d20, d19)];
                    } else
                    {
                        ai1[l5 + l7] = 0xff000001;
                    }
                }

            }

        } else
        if(s == "Azimuthal Equal Area")
        {
            for(int j2 = l; --j2 >= 0;)
            {
                double d30 = (2D * (double)j2) / (double)l - 1.0D;
                int i6 = j2 * k;
                for(int i8 = k; --i8 >= 0;)
                {
                    double d47 = (2D * (double)i8) / (double)k - 1.0D;
                    double d54 = d47 * d47 + d30 * d30;
                    if(d54 < 1.0D)
                    {
                        int i15 = (int)(0.5D * (double)i * (Math.atan2(d47, d30) / 3.1415926535897931D + 1.0D));
                        int j16 = (int)((1.0D * (double)j * Math.acos(1.0D - 2D * d54)) / 3.1415926535897931D);
                        ai1[i6 + i8] = ai[obliqueRef(i15, j16, i, j, d12, -d14, d13, d15, -d17, d16, d18, -d20, d19)];
                    } else
                    {
                        ai1[i6 + i8] = 0xff000001;
                    }
                }

            }

        } else
        if(s == "Azimuthal Orthographic")
        {
            for(int k2 = l; --k2 >= 0;)
            {
                double d31 = (2D * (double)k2) / (double)l - 1.0D;
                int j6 = k2 * k;
                int j8 = (k2 + 1) * k - 1;
                for(int j10 = k; --j10 >= 0;)
                {
                    double d51 = (4D * (double)j10) / (double)k - 1.0D;
                    double d63 = Math.sqrt(d51 * d51 + d31 * d31);
                    if(d63 < 1.0D)
                    {
                        int k16 = (int)(0.5D * (double)i * (Math.atan2(d51, d31) / 3.1415926535897931D + 1.0D));
                        int l17 = (int)((1.0D * (double)j * Math.asin(d63)) / 3.1415926535897931D);
                        ai1[j6 + j10] = ai[obliqueRef(k16, l17, i, j, d12, -d14, d13, d15, -d17, d16, d18, -d20, d19)];
                        ai1[j8 - j10] = ai[obliqueRef(k16, j - l17 - 1, i, j, d12, -d14, d13, d15, -d17, d16, d18, -d20, d19)];
                    } else
                    {
                        ai1[j6 + j10] = 0xff000001;
                    }
                }

            }

        } else
        if(s == "Azimuthal Stereographic")
        {
            for(int l2 = l; --l2 >= 0;)
            {
                double d32 = (2D * (double)l2) / (double)l - 1.0D;
                int k6 = l2 * k;
                for(int k8 = k; --k8 >= 0;)
                {
                    double d48 = (2D * (double)k8 - (double)k) / (double)l;
                    double d55 = Math.sqrt(d48 * d48 + d32 * d32);
                    int j15 = (int)(0.5D * (double)i * (Math.atan2(d48, d32) / 3.1415926535897931D + 1.0D));
                    int l16 = (int)((2D * (double)j * Math.atan(d3 * d55)) / 3.1415926535897931D);
                    ai1[k6 + k8] = ai[obliqueRef(j15, l16, i, j, d12, -d14, d13, d15, -d17, d16, d18, -d20, d19)];
                }

            }

        } else
        if(s == "Azimuthal Gnomonic")
        {
            for(int i3 = l; --i3 >= 0;)
            {
                double d33 = (2D * (double)i3) / (double)l - 1.0D;
                int l6 = i3 * k;
                for(int l8 = k; --l8 >= 0;)
                {
                    double d49 = (2D * (double)l8 - (double)k) / (double)l;
                    double d56 = Math.sqrt(d49 * d49 + d33 * d33);
                    int k15 = (int)(0.5D * (double)i * (Math.atan2(d49, d33) / 3.1415926535897931D + 1.0D));
                    int i17 = (int)((1.0D * (double)j * Math.atan(d3 * d56)) / 3.1415926535897931D);
                    ai1[l6 + l8] = ai[obliqueRef(k15, i17, i, j, d12, -d14, d13, d15, -d17, d16, d18, -d20, d19)];
                }

            }

        } else
        if(s == "Triangle Equal Area")
        {
            for(int j3 = l; --j3 >= 0;)
            {
                double d34 = (1.0D * (double)j3) / (double)l;
                int i7 = (int)((1.0D * (double)j * Math.acos(1.0D - 2D * d34 * d34)) / 3.1415926535897931D);
                int i9 = j3 * k;
                for(int k10 = k; --k10 >= 0;)
                {
                    double d52 = (2D * (double)k10) / (double)k - 1.0D;
                    double d64 = 1.0D + d52 / d34;
                    if(d64 < 2D && d64 > 0.0D)
                    {
                        int j17 = (int)(0.5D * (double)i * d64);
                        ai1[i9 + k10] = ai[obliqueRef(j17, i7, i, j, d12, d13, d14, d15, d16, d17, d18, d19, d20)];
                    } else
                    {
                        ai1[i9 + k10] = 0xff000001;
                    }
                }

            }

        } else
        if(s == "Conic Distance (fat)")
        {
            double d23 = 3.1415926535897931D - Math.acos(2D / d5 - 1.0D);
            for(int l4 = l; --l4 >= 0;)
            {
                double d41 = (2D * (double)(l4 - l)) / (double)k + 1.0D;
                int l10 = l4 * k;
                for(int j12 = k; --j12 >= 0;)
                {
                    double d57 = (2D * (double)j12) / (double)k - 1.0D;
                    double d65 = Math.sqrt(d57 * d57 + d41 * d41);
                    double d71 = 1.0D + Math.atan2(d57, d41) / d23;
                    if(d65 < 1.0D && d71 < 2D && d71 > 0.0D)
                    {
                        int i19 = (int)(0.5D * (double)i * d71);
                        int l19 = (int)(1.0D * (double)j * d65);
                        ai1[l10 + j12] = ai[obliqueRef(i19, l19, i, j, d12, d13, d14, d15, d16, d17, d18, d19, d20)];
                    } else
                    {
                        ai1[l10 + j12] = 0xff000001;
                    }
                }

            }

        } else
        if(s == "Conic Distance (thin)")
        {
            double d24 = Math.asin(0.5D * d5);
            if(k >= 2 * l)
                d24 = 1.5707963267948966D;
            for(int i5 = l; --i5 >= 0;)
            {
                double d42 = (1.0D * (double)i5) / (double)l;
                int i11 = i5 * k;
                for(int k12 = k; --k12 >= 0;)
                {
                    double d58 = (1.0D * (double)k12 - 0.5D * (double)k) / (double)l;
                    double d66 = Math.sqrt(d58 * d58 + d42 * d42);
                    double d72 = 1.0D + Math.atan2(d58, d42) / d24;
                    if(d66 < 1.0D && d72 < 2D && d72 > 0.0D)
                    {
                        int j19 = (int)(0.5D * (double)i * d72);
                        int i20 = (int)(1.0D * (double)j * d66);
                        ai1[i11 + k12] = ai[obliqueRef(j19, i20, i, j, d12, d13, d14, d15, d16, d17, d18, d19, d20)];
                    } else
                    {
                        ai1[i11 + k12] = 0xff000001;
                    }
                }

            }

        } else
        if(s == "Heart Equal Area")
        {
            double d25 = 2D - d5;
            if(d25 <= 0.0D)
                d25 = 9.9999999999999995E-008D;
            double d36 = 0.65000000000000002D;
            double d44 = d36 * d25;
            for(int l12 = l; --l12 >= 0;)
            {
                double d59 = d36 * ((2D * (double)l12) / (double)l - 2D) + 1.0D;
                int l15 = l12 * k;
                for(int k17 = k; --k17 >= 0;)
                {
                    double d73 = (d44 * (2D * (double)k17 - (double)k)) / (double)l;
                    double d75 = Math.sqrt(d73 * d73 + d59 * d59);
                    double d77 = 1.0D + (Math.atan2(d73, d59) * d75) / (Math.sin(3.1415926535897931D * d75) * d25);
                    if(d75 < 1.0D && d77 < 2D && d77 > 0.0D)
                    {
                        int j20 = (int)(0.5D * (double)i * d77);
                        int k20 = (int)(1.0D * (double)j * d75);
                        ai1[l15 + k17] = ai[obliqueRef(j20, k20, i, j, d12, d13, d14, d15, d16, d17, d18, d19, d20)];
                    } else
                    {
                        ai1[l15 + k17] = 0xff000001;
                    }
                }

            }

        } else
        if(s == "Bonne")
        {
            double d26 = 10000000D;
            if(d5 < 2D)
                d26 = 1.0D / (2D - d5) - 1.0D;
            double d37 = 0.65000000000000002D;
            double d45 = d37;
            for(int i13 = l; --i13 >= 0;)
            {
                double d60 = d37 * ((2D * (double)i13) / (double)l - 2D) + 1.0D;
                double d67 = d60 + d26;
                int i18 = i13 * k;
                for(int l18 = k; --l18 >= 0;)
                {
                    double d76 = (d45 * (2D * (double)l18 - (double)k)) / (double)l;
                    double d78 = Math.sqrt(d76 * d76 + d67 * d67) - d26;
                    double d79 = 1.0D + (Math.atan2(d76, d67) * (d78 + d26)) / Math.sin(3.1415926535897931D * d78);
                    if(d78 < 1.0D && d78 > 0.0D && d79 < 2D && d79 > 0.0D)
                    {
                        int l20 = (int)(0.5D * (double)i * d79);
                        int i21 = (int)(1.0D * (double)j * d78);
                        ai1[i18 + l18] = ai[obliqueRef(l20, i21, i, j, d12, d13, d14, d15, d16, d17, d18, d19, d20)];
                    } else
                    {
                        ai1[i18 + l18] = 0xff000001;
                    }
                }

            }

        }
        return ai1;
    }

    public int obliqueRef(double d, double d1, int i, int j, double d2, double d3, double d4, double d5, 
            double d6, double d7, double d8, double d9, double d10)
    {
        double d11 = (1.0D * (double)j) / 3.1415926535897931D;
        double d12 = (1.0D * (double)i) / 3.1415926535897931D;
        double d13 = (1.0D * d1) / d11;
        double d14 = Math.cos(d13);
        double d15 = Math.sin(d13);
        double d16 = (2D * d) / d12;
        double d17 = Math.sin(d16) * d15;
        double d18 = Math.cos(d16) * d15;
        double d19 = d17 * d2 + d14 * d3 + d18 * d4;
        double d20 = d17 * d5 + d14 * d6 + d18 * d7;
        double d21 = d17 * d8 + d14 * d9 + d18 * d10;
        double d22 = 1.0D - Math.atan2(d19, -d21) / 3.1415926535897931D;
        int k = (int)(0.5D * (double)i * d22);
        int l = (int)(1.0D * d11 * Math.acos(d20));
        int i1 = l * i + k;
        if(i1 >= i * j)
            return i * j - 1;
        if(i1 < 0)
            return 0;
        else
            return i1;
    }

    public void destroy()
    {
    }

    protected Image img;
    protected int pixels[];
    protected int startx;
    protected int starty;
    protected int endx;
    protected int endy;
    protected int imgWidth;
    protected int imgHeight;
    protected int imgTotal;
    protected double eastVal;
    protected double northVal;
    protected double rotVal;
    protected String eastText;
    protected String northText;
    protected String rotText;
    protected String imgName;
    protected String proJection;
    protected String projCentre;
    protected Choice centreChoice;
    protected Choice projChoice;
    protected TextField eastBox;
    protected TextField northBox;
    protected TextField rotBox;
    protected Button uChoose;
    static final double pi = 3.1415926535897931D;
    static final double pi180 = 0.017453292519943295D;
    static final int spaceControl = 45;
}
