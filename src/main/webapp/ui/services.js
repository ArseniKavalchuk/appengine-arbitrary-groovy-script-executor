(function(angular) {
    'use strict';
    angular.module('App')

    .factory('UserService', ['$q', '$http', 'AceScriptEditor', 'ConfigHttp', 'ObjectFactory',
             function($q, $http, AceScriptEditor, ConfigHttp, ObjectFactory) {

        return {
            listRecentScripts: function() {
                var deferred = $q.defer();
                $http(ConfigHttp.configHttp({
                    command: 'listRecentScripts'
                }))
                .success( function(data, status, headers, config) {
                    deferred.resolve(data);
                })
                .error( function(data, status, headers, config) {
                    deferred.reject(data);
                });
                return deferred.promise;
            },
            getLastEditedScript: function() {
                var deferred = $q.defer();
                $http(ConfigHttp.configHttp({
                    command: 'getLastEditedScript'
                }))
                .success( function(data, status, headers, config) {
                    deferred.resolve(data);
                })
                .error( function(data, status, headers, config) {
                    deferred.reject(data);
                });
                return deferred.promise;
            },
            executeScript: function(runDeferredViaTaskQueue) {
                
                // TODO : implement runDeferredViaTaskQueue
                var deferred = $q.defer();
                $http(ConfigHttp.configHttp({
                    command: 'executeScriptDirect',
                    scriptContent: AceScriptEditor.getValue(),
                    runDeferred: runDeferredViaTaskQueue
                })).success( function(data, status, headers, config) {
                    console.info(data);
                    deferred.resolve(data);
                }).error( function(data, status, headers, config) {
                    var errorType;
                    if (status === 488) {
                        errorType = 'COMPILATION_ERROR';
                        // TODO: editor highlight the line!
                    } else {
                        errorType = 'GENERIC_ERROR';
                    }
                    console.error(errorType, status, data);
                    deferred.reject(data);
                });
                return deferred.promise;
            }
        };
    }])
    .factory('ScriptService', ['$q', '$http', 'AceScriptEditor', 'ConfigHttp', 'ObjectFactory',
                function($q, $http, AceScriptEditor, ConfigHttp, ObjectFactory) {
        
        return {
            newScript: function() {
                var scriptName = 'Script_' + Date.now() + '.groovy';
                return ObjectFactory.createUserScript(
                    scriptName,
                    '',
                    '// New script ' + scriptName + '\n');
            },
            loadScript: function(userScriptObject) {
                var deferred = $q.defer();
                $http(ConfigHttp.configHttp({
                    command: 'loadScript',
                    scriptName: userScriptObject.scriptName
                }))
                .success( function(data, status, headers, config) {
                    deferred.resolve(data);
                })
                .error( function(data, status, headers, config) {
                    deferred.reject(data);
                });
                return deferred.promise;
            },
            saveScript: function(userScriptObject) {
                var deferred = $q.defer();
                $http(ConfigHttp.configHttp({
                    command: 'saveScript',
                    scriptName: userScriptObject.scriptName,
                    scriptContent: userScriptObject.scriptContent
                }))
                .success( function(data, status, headers, config) {
                    deferred.resolve(data);
                })
                .error( function(data, status, headers, config) {
                    deferred.reject(data);
                });
                return deferred.promise;
            },
            listScripts: function() {
                var deferred = $q.defer();
                $http(ConfigHttp.configHttp({
                    command: 'listScripts',
                    page: 0,
                    count: 50
                }))
                .success( function(data, status, headers, config) {
                    deferred.resolve(data);
                })
                .error( function(data, status, headers, config) {
                    deferred.reject(data);
                });
                return deferred.promise;
            }
        };
    }])
    .factory('ConfigHttp', function() {
        return {
            configHttp: function(bindData) {
                return {
                    method: 'POST',
                    url: '/commandHandler',
                    data: bindData,
                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                    transformRequest: function(obj) {
                        var p, str = [];
                        for (p in obj)
                        str.push(encodeURIComponent(p) + "=" + encodeURIComponent(obj[p]));
                        return str.join("&");
                    }
                };
            }
        };
    })
    .factory('ObjectFactory', function() {

        // TODO : make immutable
        function UserScript(scriptName, scriptEtag, scriptContent) {
            this.scriptName = scriptName;
            this.scriptEtag = scriptEtag;
            this.scriptContent = scriptContent;
        }

        var sampleUserScript = new UserScript(
            'sample1.groovy',
            'a9993e364706816aba3e25717850c26c9cd0d89d',
            "import com.google.appengine.api.datastore.*\n\n" +
            "def ds = DatastoreServiceFactory.getDatastoreService()\n\n" +
            "def entity = new Entity('HELLO_WORLD')\n" +
            "entity.setProperty('creationDate', System.currentTimeMillis())\n" +
            "entity.setUnindexedProperty('text', 'HELLO WORLD')\n\n" +
            "ds.put(entity)\n");

        return {
            createUserScript: function(scriptName, scriptLink, scriptContent) {
                return new UserScript(scriptName, scriptLink, scriptContent);
            },
            createSampleUserScript: function() {
                return sampleUserScript;
            }
        }
    });
})(angular);
