# Journal App

An Android Application to maintain photos and writing some thoughts about photos like we do on any social media app.

## Requirements
- Android Studio
- JDK(Java Development Key)

## Permissions

**Add Permissions inside Manifests**

- uses-permission android:name="android.permission.INTERNET"
- uses-permission android:name="android.permission.CAMERA"

## Tech Stack

**Design:** XML(Extensible Markup Language)

**Backend:** Java

**Firebase:** Firebase Authentication, Firebase Storage, Firebase FireStoreDB.

## Dependencies
Add below dependencies under (Gradle Script) inside Module Sections.

**These four Dependencies for Firebase services**
- implementation platform('com.google.firebase:firebase-bom:30.4.1')
-  implementation 'com.google.firebase:firebase-firestore'
- implementation 'com.google.firebase:firebase-auth'
-  implementation 'com.google.firebase:firebase-storage'

**These two Dependencies for setting Image**
- implementation 'com.github.bumptech.glide:glide:4.13.2'
- annotationProcessor 'com.github.bumptech.glide:compiler:4.13.2'

## Screenshots

<p align="center">
    <img src="https://github.com/prog-cy/Journal-App/blob/master/screen1.jpeg" width = "200" height = "400" 
    margin = "10">
    <img src="https://github.com/prog-cy/Journal-App/blob/master/screen2.jpeg" width = "200" height = "400"
    margin = "10">
    <img src="https://github.com/prog-cy/Journal-App/blob/master/screen3.jpeg" width = "200" height = "400"
    margin = "10">    
    <img src="https://github.com/prog-cy/Journal-App/blob/master/screen4.jpeg" width = "200" height = "400"
    margin = "10"> 
     <img src="https://github.com/prog-cy/Journal-App/blob/master/screen5.jpeg" width = "200" height = "400"
    margin = "10">
     <img src="https://github.com/prog-cy/Journal-App/blob/master/screen6.jpeg" width = "200" height = "400"
    margin = "10">
     <img src="https://github.com/prog-cy/Journal-App/blob/master/screen7.jpeg" width = "200" height = "400"
    margin = "10">
     <img src="https://github.com/prog-cy/Journal-App/blob/master/screen8.jpeg" width = "200" height = "400"
    margin = "10">   
      
</p>