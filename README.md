# IONIC-SDK
App analytics SDK for IONIC based applications


# Plumb5 IONIC SDK


# Software Requirements



*   Android:
    *   Minimum Version :16
    *   Maximum Version :30
    *   Gradle: Greater than or equal to 3.5
    *   Google-Service: 4.2.0
*   Ios
*   Cordova
    *   Requirement : greater than 3
*   Ionic
    *   Requirement : greater than or equal to 4


# Prerequisites

	



*   Variable
    *   PLUMB5_ACCOUNT_ID
    *   PLUMB5_API_KEY
    *   PLUMB5_BASE_URL
*   File
    *   Google-services.json
    *   ic_p5_logo.png(16px X 16px or 14px X 14px)
*   SDK
    *   Download: [https://github.com/plumb5/IONIC-SDK](https://github.com/plumb5/IONIC-SDK)


## Pre installation


### **FCM config**

Add your google-services.json to the root of your cordova project before you add the plugin.


### **Add**



*   **Edit config.xml** : 
    *   Under platform “Android”
        *   add this line 
```xml
<resource-file src="google-services.json" target="app/google-services.json" />
```



    *   Under platform “Ios”


## **Starting the installation**

From root of application


### **Add plugin**



*   Using Cordova: 

    ```java
    cordova plugin add PATH/Git URL --variable 
    ```
    <code>PLUMB5_ACCOUNT_ID="<strong>YOUR PLUMB5 ID</strong>" --variable</code>  
    <code> PLUMB5_API_KEY="<strong>YOUR PLUMB5 KEY</strong>" --variable</code>  
    <code> PLUMB5_BASE_URL="<strong>YOUR PLUMB5 URL</strong>"</code>  


*   Using Ionic: 
    ```java
    `ionic` `cordova plugin add` **PATH/Git URL --variable 
    ```
    <code>PLUMB5_ACCOUNT_ID="<strong>YOUR PLUMB5 ID</strong>" --variable</code>  
    <code> PLUMB5_API_KEY="<strong>YOUR PLUMB5 KEY</strong>" --variable</code>  
    <code> PLUMB5_BASE_URL="<strong>YOUR PLUMB5 URL</strong>"</code>  



### **Remove plugin**



*   Using Cordova:

    ```smalltak
    cordova plugin rm cordova-plugin-plumb5
    ```


    *   Please ignore the error after removing the plugin
*   Using Ionic:

    ```smalltalk
    ionic cordova plugin rm cordova-plugin-plumb5
    ```


    *   Please ignore the error after removing the plugin


## **For Ionic**

Add  <strong>app.component.ts</strong>


```smalltalk
import { Component } from '@angular/core';

import { Platform } from '@ionic/angular';
import { Router, NavigationStart, ActivatedRoute} from '@angular/router';
declare var cordova: any;
var p5 = cordova.plugins.Plumb5.init();


export class AppComponent {
  constructor(
    private platform: Platform,
    public router: Router,
    private pageParameter:ActivatedRoute

  ) {
    this.initializeApp();
    p5.setup();

//Plumb5 lifecycle and in-app
this.router.events.forEach((event) => { 

if (event instanceof NavigationStart) {

p5.tracking([{ ScreenName: event.url, PageParameter: "" }]); p5.pushResponsePost([{ ScreenName: event.url, PageParameter: "" }]); 
} 
});

  }

initializeApp() {
    this.platform.ready().then(() => {
//page navigation listener
        document.addEventListener('onPushNotification', (e: any) => {
        let routeUrl = e.routeUrl
        this.router.navigate([routeUrl]);
      });
  
     });
  }

```



### **Example**



*   **User details** 

    ```smalltalk
     var userDetailsJson =[
            {
              "Name": "demo",
              "EmailId": "demo@demo.com",
              "PhoneNumber": "987654321",
              "LeadType": 1,
              "Gender": "Male",
              "Age": "2020-01-27T06:12:01.051Z",
              "AgeRange": "10-89",
              "MaritalStatus": "Married",
              "Education": "MCA",
              "Occupation": "SE",
              "Interests": "Eating",
              "Location": "Bangalore" 
    //Add extra parameters
    "key":"value"
            }
          ]
    p5.setUserDetails(userDetailsJson);
    ```


*   **Event** 
    *   **Html:**

    ```html
    <ion-button color="primary" (click)="btn1()">Event Post</ion-button>
    ```


    *   **Component.ts**
      ```smalltalk
         btn1(){
              let eventDetailsJson =[
                {
                  "Type": "Button",
                  "Name": "Event Post",
                  "Value": 1    
                }
              ]
            p5.eventPost(eventDetailsJson);
            }
      ```


		

      
