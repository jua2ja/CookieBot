'''
it works i swer
'''
from PIL import ImageGrab
from PIL import ImageOps
from PIL import Image
import cv2
from numpy import *
import numpy as np
import numpy
import os
import time,math
from ctypes import windll
user32 = windll.user32
user32.SetProcessDPIAware()
c = Image.open("C:\\Users\\neelb\\Documents\\GitHub\\CookieBotapcs\\data\\example\\cookies\\CutCookie.png")
COOKIECOLOR = (201, 159, 111)#25
CHIPCOLOR = (107,79,68)# 50 works like a freaking dreem
BUYTILEYES = (143, 140, 132) # 20
def flt(targimg,centroid, threshold,reee = False):
    # targimg is image to look on, centroid is RGB center, thresh is euclid distance for sphere, and reee is true if u want to return.
    img = Image.new("RGB",(targimg.size[0],targimg.size[1]),"black")
    pixels = img.load()
    for i in range(0,targimg.size[0]):
        for j in range(0,targimg.size[1]):
            if(dist(targimg.getpixel((i,j)), centroid) < threshold):
                pixels[i,j] = (255,255,255)
    img.show()
    if (reee):
        print "Returning Image.."
        return img

def r(mp):
    cv2.imshow("notlit arod",mp)
    cv2.waitKey(0)
    cv2.destroyAllWindows()
def dist(a,b):
    return math.sqrt(abs(a[0]-b[0])**2 +abs(a[1]-b[1])**2+abs(a[2]-b[2])**2) 
def getPixelatmouse():
    im = ImageGrab.grab()
    return im.getpixel(getcords())
def mousePos(cord):
    win32api.SetCursorPos(cord)

def leftClick():
    win32api.mouse_event(win32con.MOUSEEVENTF_LEFTDOWN,0,0)
    time.sleep(.1)
    win32api.mouse_event(win32con.MOUSEEVENTF_LEFTUP,0,0)
 
def getcords():
    return win32api.GetCursorPos()
def scancookie():
    returnimage = flt(ImageGrab.grab(),CHIPCOLOR,50,reee=True)
    cimg = numpy.array(returnimage)
    gray = cv2.cvtColor(cimg,cv2.COLOR_RGB2GRAY)
    circles = cv2.HoughCircles(gray,cv2.HOUGH_GRADIENT,1,20,param1=30,param2=100,maxRadius = 500)
    circles = np.uint16(np.around(circles))
    for i in circles[0,:]:
        cimg = cv2.circle(cimg,(i[0],i[1]),i[2],(0,255,0),2)
        cimg = cv2.circle(cimg,(i[0],i[1]),2,(0,0,255),3)
    return cimg
def scanAvailible():
    returnimage = flt(ImageGrab.grab(),BUYTILEYES,20,reee=True)
    cimg = numpy.array(returnimage)
    gray = cv2.cvtColor(cimg,cv2.COLOR_RGB2GRAY)
    circles = cv2.HoughCircles(gray,cv2.HOUGH_GRADIENT,1,20,param1=30,param2=30,maxRadius = 400)
    circles = np.uint16(np.around(circles))
    for i in circles[0,:]:
        # draw the outer circle
        if returnimage.getpixel((int(i[0]),int(i[1]))) != (0,0,0):
            cimg = cv2.circle(cimg,(i[0],i[1]),i[2],(255,0,0),2)
            # draw the center of the circle
            cimg = cv2.circle(cimg,(i[0],i[1]),2,(0,0,255),3)
    return cimg
