# CaPL Android app

![Android Studio](https://img.shields.io/badge/Android_Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![OpenCV](https://img.shields.io/badge/opencv-%23white.svg?style=for-the-badge&logo=opencv&logoColor=white)
[![Open Source? Yes!](https://badgen.net/badge/Open%20Source%20%3F/Yes%21/blue?icon=github)](https://github.com/Naereen/badges/)

[comment]: <> (Badges taken from: https://github.com/Ileriayo/markdown-badges and https://github.com/alexandresanlim/Badges4-README.md-Profile)

 > Author: Anthony Guinchard <br>
 > Institution: EPFL <br>
 > App based on the [micro:bit Blue app from Martin Woolley](https://github.com/microbit-foundation/microbit-blue)
 
## Table of contents
* [1. Description](#1-description)
* [2. Resources](#2-resources)
* [3. Installation](#3-installation)
* [4. Version history](#4-version-history)
* [5. Contributing](#5-contributing)

<!-- toc -->

## 1. Description

Research conducted in recent years showed that the use of tangible platforms for teaching the basics of programming to children can improve their engagement, interest and collaboration. An important drawback of these robotics educational platforms, however, is that they might be expensive and a lot of schools are not able to afford them. In 2019, EPFL developed two accessible tangible programming language systems: Thymio TPL and Thymio PaPL.

As part of my first semester project of my Master in Robotics at EPFL in Spring 2020, I took part in the development of a new low-cost and accessible tangible programming language based on cardboard. This project was hence called Cardboard-based Programming Language (CaPL). Based on the [micro:bit Blue Android app made by Martin Woolley](https://github.com/microbit-foundation/microbit-blue) (a senior British developer and Bluetooth expert), this application is the cornerstone of the CaPL project. Its main purpose is to enable communication between the cardboard-based robot, called Cardbot, and the CaPL tangible programming tiles. In addition, the CaPL app provides also different kinds of feedback regarding the execution of the code by the Cardbot. It is composed of several activities among which the most interesting are:

- The "Free Game"
- The "Geography Game"
- The "Easter Egg"

More details regarding the functionalities of each of these activities are given in their respective "info" menu inside the app.
The work presented here provides a base for future Android activities around the Cardbot and other micro:bit-based robots. Moreover, the Android activities can be easily adapted to various age groups by, for instance, changing the displacement costs of the robot in the "Geography Game", by including new rules or by implementing more advanced MCQs. 

<p align="center">
	<img src="title_with_geography_activity.png" height="600">
</p>

## 2. Resources

* The key elements of the project are presented in [this YouTube video](https://www.youtube.com/watch?v=SX8Q3Wv9Q3Q).

* [Short article in French](https://www.roteco.ch/fr/nouveautes/programmation-dun-robot-avec-des-blocs-en-carton-et-une-application-android/) from the Roteco website summarizing the whole CaPL project.

* Paper written as a result of research related to this project, distance learning and maker-based approaches: [Accessible Maker-Based Approaches to Educational Robotics in Online Learning](https://ieeexplore.ieee.org/document/9471866) (note that in this paper the "Cardbot" was renamed "CreroBot").

* [Recording of my final semester project presentation](https://youtu.be/kO5qkA_hCcY).

## 3. Installation

Clone this repository with following Terminal command and import `capl-app` into Android Studio:

```
git clone https://github.com/Antho1426/capl-project.git
```

## 4. Version history
* 0.1
    * Initial release

## 5. Contributing

Pull Requests are not being accepted at this time. If you want however to use some part of this code for your personal project, you are free to use it. Be just sure to mention the fact that the original code comes from the [micro:bit Blue app from Martin Woolley](https://github.com/microbit-foundation/microbit-blue). Thank you.