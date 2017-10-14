# Retrofit_Get_Post
Retrofit GET DATA and POST DATA
<br>
[![Ansible Role](https://img.shields.io/badge/Developer-Soussidev-yellow.svg)]()
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)]()
[![](https://jitpack.io/v/datalink747/Retrofit_Get_Post.svg)](https://jitpack.io/#datalink747/Retrofit_Get_Post)

<a href='https://ko-fi.com/A243447K' target='_blank'><img height='36' style='border:0px;height:36px;' src='https://az743702.vo.msecnd.net/cdn/kofi4.png?v=0' border='0' alt='Buy Me a Coffee at ko-fi.com' /></a>

# Include:
[![Ansible Role](https://img.shields.io/badge/Rx-Volley-ff2c94.svg?style=flat-square)](https://github.com/datalink747/Rx_java2_soussidev/blob/master/app/src/main/java/com/soussidev/kotlin/rx_java2_soussidev/RxSharedpref_fragment.java)

# Add dependencie to your project :

```gradle
repositories {
    jcenter()
    maven { url "https://jitpack.io" }
}

dependencies {
    compile 'com.github.datalink747:Retrofit_Get_Post:1.0.1'
}
```

 Code :
> RxVolley
```java
 public void rxvolley(String name,String prenom,String cin,String img)
    {
        HttpParams params = new HttpParams();

        params.put("NomUser", name);
        params.put("PrenomUser", prenom);
        params.put("CinUser", cin);
        params.put("ImgUser", img);

        RxVolley.post(BASE_URL_GET_USER, params,
                (transferredBytes, totalSize) -> {
                    Log.d(Constants.TAG,transferredBytes + "==" + totalSize);
                    Log.d(Constants.TAG,"=====looper" + (Thread.currentThread() == Looper.getMainLooper
                            ().getThread()));
                }, new HttpCallback() {
                    @Override
                    public void onSuccess(String t) {
                        Log.d(Constants.TAG,t);
                        editName.setText("");
                        editPrenom.setText("");
                        editCin.setText("");
                    }
                });


    }


```


# SDK Required
+ Target sdk:<br>
[![API](https://img.shields.io/badge/API-26%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=23)
+ Min sdk:<br>
[![API](https://img.shields.io/badge/API-16%2B-orange.svg?style=flat)](https://android-arsenal.com/api?level=19)

# Social Media
<table style="border:0px;">
   <tr>
      <td>
<a href="https://www.linkedin.com/in/soussimohamed/">
<img src="picture/linkedin.png" height="100" width="100" alt="Soussi Mohamed">
</a>
      </td>
      <td>
         <a href="https://twitter.com/soussimohamed7/">
<img src="picture/Twitter.png" height="60" width="60" alt="Soussi Mohamed">
</a>
     </td>
        <td>
         <a href="https://plus.google.com/u/0/+SoussiMohamed747">
<img src="picture/googleplus.png" height="60" width="60" alt="Soussi Mohamed">
</a>
     </td>
  </tr> 
</table> 

# Licence
```
Copyright 2017 Soussidev, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
