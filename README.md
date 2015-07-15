# Groovy Script Editor end Executor on Google App Engine #

This App Engine application allows to **write and execute arbitrary Groovy scripts on Google App Engine**.
All App Engine API which is avaliable on the classpath can be executed. The same relates to the libraries that are on classpath as well.

More features:
* App Engine authentication & authorization
* Stores your scripts on Google Cloud Storage (configurable bucket)
* Remembers your last edited script
* Remembers each execution of the script either success or failed per User
* You may embed this editor in your Spring Framework project and make available your Beans to the script via _applicationContext_ object bound to the execution environment

Possible advantages:
* Allows to modify existing Entities in the Datastore in a batch without re-deploy of an application.
* Allows to search Entities (using a loop) by unindexed properties
* Allows to process files on Google Cloud Storage
* Much more...

## Screenshots ##

![Alt text](/screen1.png?raw=true "Groovy Script Editor end Executor on Google App Engine")
![Alt text](/screen2.png?raw=true "Groovy Script Editor end Executor on Google App Engine")

## Run On Dev Server ##

```
$ gradlew appengineRun
```

## Deploy On Google App Engine ##

* Create new project or use existing. Follow [Google guides](https://cloud.google.com/appengine/docs).
* Create new Google Cloud Storage bucked in [Google Developers Console](https://console.developers.google.com)
* Update the bucket name in _app.properties_ ```script.storage.bucket=my-server-scripts```
* Deploy with you projectId, you may choose another module name and version for App Engine
```
$ gradlew appengineUpdate -PappName=PROJECT_ID -PappModule=default -PappVersion=1
```

## Licensing ##

The MIT License (MIT)

Copyright (c) 2015 Arseni Kavalchuk

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
