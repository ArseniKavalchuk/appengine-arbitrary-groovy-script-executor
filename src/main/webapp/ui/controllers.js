(function(angular){
    'use strict';
    angular
    .module('App')
    .controller('ScriptEditorController', 
        ['$scope', '$mdSidenav', '$mdDialog', '$mdToast', 'AceScriptEditor', 'UserService', 'ScriptService',
        function($scope, $mdSidenav, $mdDialog, $mdToast, AceScriptEditor, UserService, ScriptService) {
            var self = this;
            
            $scope.toggleLeftMenu = function() {
                $mdSidenav('left').toggle();
            };
            $scope.model = {};
            $scope.model.userScript = {
                scriptName: '',
                scriptEtag: '',
                scriptContent: '',
                dirty: true
            };
            
            function configToast(content) {
                return $mdToast.simple().hideDelay(30000).action('CLOSE').position('top right').content(content);
            };
            
            function configToastQuick(content) {
                return $mdToast.simple().hideDelay(6000).action('CLOSE').position('top right').content(content);
            };
            // move to services?
            function showResultsDialog(ev) {
                $mdDialog.show({
                    parent: angular.element(document.body),
                    templateUrl: '/ui/templates/resultDialog.tpl.html',
                    locals: {
                        scriptResult: $scope.model.scriptResult
                    },
                    controller: function($scope, $mdDialog, scriptResult) {
                        $scope.scriptResult = scriptResult;
                        $scope.closeDialog = function() {
                            $mdDialog.hide();
                        }
                    }
                });
            };
            function showAlertDialog(content) {
                $mdDialog.show($mdDialog.alert()
                    .parent(angular.element(document.body))
                    .title('Alert!')
                    .content(content)
                    .ariaLabel('Alert!')
                    .ok('Got it!'));
            };
                               
            function showRunDialog() {
                return $mdDialog.show({
                    parent: angular.element(document.body),
                    templateUrl: '/ui/templates/runDialog.tpl.html',
                    controller: function($scope, $mdDialog) {
                        $scope.runDeferred = false;
                        $scope.no = function() {
                            $mdDialog.cancel();
                        };
                        $scope.yes = function() {
                            $mdDialog.hide($scope.runDeferred);
                        }
                    }
                });
            };
            
            function showSaveDialog() {
                return $mdDialog.show({
                    parent: angular.element(document.body),
                    templateUrl: '/ui/templates/saveDialog.tpl.html',
                    locals: {
                        userScript: $scope.model.userScript
                    },
                    controller: function($scope, $mdDialog, userScript) {
                        $scope.userScript = userScript;
                        $scope.isScriptNameValid = function() {
                            return (
                                typeof userScript.scriptName !== 'undefined' && userScript.scriptName.trim().length > 0
                            );
                        }
                        $scope.no = function() {
                            $mdDialog.cancel();
                        };
                        $scope.yes = function() {
                            $mdDialog.hide($scope.userScript.scriptName);
                        }
                    }
                });
            };
            
            function loadRecentFiles() {
                UserService.listRecentScripts().then(
                    function(itemsList) {
                        $scope.model.recentFiles = itemsList;
                    },
                    function(err) {
                        console.error(err);
                    }
                );
            };
            loadRecentFiles();
            
            function saveScript() {
                $scope.model.userScript.scriptContent = AceScriptEditor.getValue();
                ScriptService.saveScript($scope.model.userScript)
                    .then(
                        function(userScriptObject) {
                            $mdToast.show(configToastQuick('Script has been saved.'));
                            $scope.model.userScript.dirty = false;
                            loadRecentFiles();
                        },
                        function(err) {
                            console.error(err);
                            $mdToast.show(configToastQuick('Error while saving script. See console log.'));
                        }
                    );
            };
            
            function showOpenDialog() {
                return $mdDialog.show({
                    parent: angular.element(document.body),
                    templateUrl: '/ui/templates/openDialog.tpl.html',
                    locals: {
                        scriptService: ScriptService,
                    },
                    controller: function($scope, $mdDialog, scriptService) {
                        scriptService.listScripts().then(
                            function(items) {
                                $scope.items = items;
                            },
                            function(err) {
                                $scope.error = err;
                            }
                        );
                        $scope.selectedItem = null;
                        $scope.selectItem = function(item) {
                            $scope.selectedItem = item;
                        };
                        $scope.isItemSelected = function(item) {
                            return $scope.selectedItem === item;
                        };
                        $scope.no = function() {
                            $mdDialog.cancel($scope.error);
                        };
                        $scope.yes = function() {
                            $mdDialog.hide($scope.selectedItem);
                        }
                    }
                });
            };
            
            AceScriptEditor.on('edit', function(e) {
                $scope.model.userScript.dirty = true;
                $scope.$apply();
            });
            
            AceScriptEditor.commands.addCommand({
                name: 'saveCommand',
                bindKey: {win: 'Ctrl-S',  mac: 'Command-S'},
                exec: function(editor) {
                    if (typeof $scope.model.userScript.scriptName !== 'undefined' && $scope.model.userScript.scriptName.trim().length > 0) {
                        saveScript();
                    } else {
                        // show dialog in there is no name
                        $scope.actions.saveScript(true);
                    }
                },
                readOnly: true // false if this command should not apply in readOnly mode
            });

            UserService.getLastEditedScript().then(
                function(userScript) {
                    $scope.model.userScript = userScript;
                    AceScriptEditor.setValue(userScript.scriptContent);
                },
                function(err) {
                    console.error(err);
                }
            );
            
            $scope.actions = {
                openScript: function(scriptName) {
                    if (scriptName) {
                        console.info('Open script', scriptName);
                        ScriptService.loadScript({scriptName: scriptName}).then(
                            function(userScript) {
                                $scope.model.userScript.scriptContent = userScript.scriptContent;
                                AceScriptEditor.setValue(userScript.scriptContent);
                            }
                        );
                    } else {
                        // open dialog
                        showOpenDialog().then(
                            function(userScript) {
                                if (userScript) {
                                    $scope.model.userScript = userScript;
                                    ScriptService.loadScript(userScript).then(
                                        function(userScript) {
                                            $scope.model.userScript.scriptContent = userScript.scriptContent;
                                            AceScriptEditor.setValue(userScript.scriptContent);
                                        }
                                    );
                                }
                            }
                        );
                    }
                },
                saveScript: function(dialog, scriptName) {
                    if (dialog) {
                        showSaveDialog().then(
                            function(dialogScriptName) {
                                saveScript();
                            });
                    } else {
                        saveScript();
                    }
                },
                newScript: function() {
                    //$scope.actions.saveScript();
                    $scope.model.userScript = ScriptService.newScript();
                    AceScriptEditor.setValue($scope.model.userScript.scriptContent);
                },
                runScript: function() {
                    
                    if (typeof $scope.model.userScript.dirty === 'undefined' || $scope.model.userScript.dirty === true) {
                        showAlertDialog('You must save the script before execution');
                        return;
                    }
                    
                    if ($scope.model.executingScript) {
                        showAlertDialog('Please wait until the previous script has finished.');
                        return;
                    }
                    
                    showRunDialog().then(
                        function(runDeferred) {
                            $scope.model.executingScript = true;
                            UserService.executeScript(runDeferred).then(
                                function(data) {
                                    $mdToast.show(configToast('Script was executed successfully.'));
                                    $scope.model.scriptResult = data;
                                },
                                function(data) {
                                    $mdToast.show(configToast('Script has failed!'));
                                    showAlertDialog(data);
                                    $scope.model.scriptResult = data;
                                }
                            )['finally'](function() {
                                $scope.model.executingScript = false;
                            });
                            $mdToast.show(configToast('Script has been launched. Please wait for result.'));
                        });
                },
                viewLatestResults: function() {
                    showResultsDialog();
                }
            };
        }]);
})(angular);
