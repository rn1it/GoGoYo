# GoGoYo 狗狗友
一款以寵物為主題的交友App

功能介紹
---
1. 首頁: 呈現所有使用者的動態，幫助使用者交流


2. 好友功能: 推薦好友卡、呈現使用者好友關係、發訊息聯絡好友


3. 散步功能: 選擇寵物散步後，系統會自動記錄散步的路線，使用者也可以在散步過程中找到其他散步中的使用者


4. 統計功能: 呈現使用者的歷史散步紀錄以及所有散步的數據統計


5. 個人功能: 編輯使用者與寵物的資訊



框架與工具
---
1. 使用架構：MVVM、Singleton
1. 設計模式：Singleton、Adapter、Observer
2. 實作功能：ViewPager (切換功能頁面、切換寵物)、RecyclerView (瀑布式呈現動態)、MPAndroidChart (實作客製化圓餅圖)、QR CODE Scanner (掃描 QR CODE 加好友)、Google map API (呈現散步地圖)、Weather API (獲得使用者當前位置的天氣資訊)、登入 (Google Login 結合 Firebase Authorization)
3. Jetpack：ViewModel、LiveData、Lifecycle、Data Binding、Navigation
4. 分析工具：Firebase Crashlytics、Firebase Ayalytics
5. 測試工具：JUnit

開發環境
---
* Android Studio 4.0
* Android SDK 28
* Gradle 6.1.1+

版本更新
---
* 1.0：2020/12/24
* 1.1：2021/01/05

聯絡資訊
---
Gary Hsu
gary13900103@gmail.com
