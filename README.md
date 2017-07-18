# Splist

## Overview
Splist is an Android application for shared shopping lists. Ideal for recurring expenses between the same people, such as roommates or family, Splist saves time by having one person buy all the items from a compiled list. Other members simply pay the buyer back -- and with modern digital payment options such as Venmo, the process has been made simpler.

## Design and Prototypes
Prior to development, I made some medium fidelity designs using Adobe Experience Design.
![image](https://user-images.githubusercontent.com/21299137/27813930-27b033dc-602e-11e7-9043-17646681147a.png) ![image](https://user-images.githubusercontent.com/21299137/27813940-37cf4e1a-602e-11e7-83a1-92076b6f9a68.png) ![image](https://user-images.githubusercontent.com/21299137/27814054-e0374c60-602e-11e7-92da-02ca02eddc7a.png)

## Technologies and Libraries
Below I outline some of the main technologies that were used for this project.

### OCR 
One of the prime aspects of this application will be its ability to scan and parse user-uploaded receipts. After uploading, the app would read the name, items, and total from the receipt and transform the information into a digital format (as shown in the third screenshot). After parsing, a key feature is a user's ability to tap on their items and see their personal total be updated in real-time. In addition to the novel concept of having shared shopping lists, the OCR technology would make this application further stand out among the rest. [This](www.ocrsdk.com) is the SDK that I used.

### Firebase
Firebase Authentication, Storage, and Database were used for this application. The database was used to store user information, lists, and groups, while storage was used to keep all the user-uploaded images.

### Glide/FirebaseUI
Glide and FirebaseUI were used for easy image uploading.

### Klinker Chips Library
This library was used to implement the material chips for adding members to a group.

