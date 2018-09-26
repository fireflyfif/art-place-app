# Art Place App
## Capstone project, Part of the Android Developer Nanodegree Program by Udacity

![text](https://github.com/fireflyfif/art-place-app/blob/master/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png)

## App Description
Art Place is an app for art lovers, artists or people who just enjoy seeing a piece of artwork. Browse through famous artworks and see their location and other details. Find out who are their artists and see more details about them too.

- The App fetches data from the Artsy API and displays an endless scroll with Artworks. 
- The App allows saving favorite items into a local database, using the [Room Persistence Library](https://developer.android.com/topic/libraries/architecture/room). 
- The App displays a reasonable subset of the data, thanks to the [Paging Library](https://developer.android.com/topic/libraries/architecture/paging/) that requests a small portion of data at a time and provides pagination. That way the app consumes less network bandwidth and fewer system resources.

## Configurations
In order to run this app please add your own token from the [Artsy API](https://developers.artsy.net/). 
Place your TOKEN to `build.gradle` 
```gradle
def apiToken = project.hasProperty('token') ? apiToken : (System.getenv('TOKEN') ?: "\"YOUR_TOKEN\"")
```

## Libraries
- [Support Libraries](https://developer.android.com/topic/libraries/support-library/features#v7-palette)
- [Retrofit2](https://github.com/square/retrofit)
- [Picasso](https://github.com/square/picasso)
- [Architecture Components](https://developer.android.com/topic/libraries/architecture/)
- [Butterknife](https://github.com/JakeWharton/butterknife)
- [OkHttp](https://github.com/square/okhttp)
- [Room Persistence Library](https://developer.android.com/topic/libraries/architecture/room)
- [Paging Library](https://developer.android.com/topic/libraries/architecture/paging/)
- [Google Play Services - Ads](https://developers.google.com/android/reference/com/google/android/gms/ads/package-summary)
- [Google Play Services - Analytics](https://developers.google.com/android/reference/com/google/android/gms/analytics/package-summary)
- [Palette](https://developer.android.com/training/material/palette-colors)
- [Fresco](https://github.com/facebook/fresco)


## Screenshots

![text](https://github.com/fireflyfif/art-place-app/blob/master/art/design_new_01.png)


![text](https://github.com/fireflyfif/art-place-app/blob/master/art/design_new_02_.png)


# License
```
This project was submitted by Iva Ivanova as part of the Nanodegree at Udacity.

According to Udacity Honor Code we agree that we will not plagiarize 
(a form of cheating) the work of others.
Plagiarism at Udacity can range from submitting a project you didn’t create to 
copying code into a program without citation. Any action in which you misleadingly 
claim an idea or piece of work as your own when it is not constitutes plagiarism.
Read more here: https://udacity.zendesk.com/hc/en-us/articles/360001451091-What-is-plagiarism-

MIT License

Copyright (c) 2018 Iva Ivanova

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
