<img src="app/src/main/res/drawable-xxxhdpi/ic_launcher.png" align="right" />

# Pendulum Studio

<a href='https://play.google.com/store/apps/details?id=com.vlvolad.pendulumstudio'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' height=60px/></a>


This repository contains the source code of the [Pendulum Studio app for Android](https://play.google.com/store/apps/details?id=com.vlvolad.pendulumstudio)





It is designed to be used in Android Studio with Gradle.

## Short description of the app 

The app simulates the motion of various pendulum systems in real-time and renders it on the screen of an Android device. 
The Euler-Lagrange equations of motion for a chosen system are solved numerically by applying the Runge-Kutta-Fehlberg method at each time step.
The visualization is performed using OpenGL.

#### List of systems:
- Mathematical pendulum (2D)
- Pendulum wave effect (3D)
- Spherical pendulum (3D)
- Spring pendulum (2D)
- Spring pendulum (3D)
- Double pendulum (2D)
- Double spherical pendulum (3D)
- Spring-mathematical pendulum (2D)
- Spring-spherical pendulum (3D)

#### Some features:
- Option to use accelerometer of the device as the input of gravity force
- Using fingers to interactively change positions of pendulums (for 2D systems only)
- Damping force to simulate friction
- Regulation of all system parmeters
- Customize pendulum colors (using the [ColorPickerView](https://github.com/danielnilsson9/color-picker-view) library by Daniel Nillson)
- Live wallpaper

## License
This software is licensed under the Apache License 2.0. See the [LICENSE](LICENSE) file for more information.

*Copyright (C) 2015-2018  Volodymyr Vovchenko*

