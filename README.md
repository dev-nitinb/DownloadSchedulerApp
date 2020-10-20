# DownloadSchedulerApp
Android Application which downloads file from url in background.

Max 5 files downloadable URLs can be added in a list. User can set the timer using time picker so that user can schedule a download time to download files even though the application is not in the background. 
Their is a custom Notification view where user can see the download progress like  1/5...2/5...3/5 downloaded. Once download is complete it will play a sound and when a user clicks on Download complete Notification dialog it should show the downloaded item list in the app itself.

#### Concepts Used
1. Dynamic permission request
2. Dynamically add view
3. Date Timer Picker
4. Room Db
5. Retrofit Download File
6. Store Download File in memory
7. Timer Service
8. Notification

#### Concept reference resources:
1. [Android Dynamic Views](https://www.youtube.com/watch?v=EJrmgJT2NnI)
2. [Android Room Db Codelab](https://codelabs.developers.google.com/codelabs/android-room-with-a-view)
3. [Using Room Database | Android Jetpack](https://medium.com/mindorks/using-room-database-android-jetpack-675a89a0e942)
4. [Retrofit - Download Files from Server](https://futurestud.io/tutorials/retrofit-2-how-to-download-files-from-server)
5. [Notification](https://developer.android.com/guide/topics/ui/notifiers/notifications)
