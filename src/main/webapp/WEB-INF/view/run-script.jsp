<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@page trimDirectiveWhitespaces="true" %>
<!DOCTYPE html>
<html lang="en" ng-app="App" ng-controller="ScriptEditorController" ng-cloak>
<head>
  <meta charset="UTF-8">
  <link rel="stylesheet" href="//ajax.googleapis.com/ajax/libs/angular_material/0.10.0/angular-material.min.css">
  <link rel="stylesheet" href="//fonts.googleapis.com/css?family=RobotoDraft:300,400,500,700,400italic">
  <link rel="stylesheet" href="//fonts.googleapis.com/icon?family=Material+Icons">
  <link rel="stylesheet" href="/ui/css/custom.css">
  <!-- 
  <link rel="stylesheet" href="/ui/cm/lib/codemirror.css">
  <link rel="stylesheet" href="/ui/cm/theme/eclipse.css">
   -->
  <meta name="viewport" content="initial-scale=1" />
  
<title>Script Editor &gt; {{ model.userScript.scriptName }}</title>
</head>
<body layout="row">

    <md-sidenav class="site-sidenav md-sidenav-left md-whiteframe-z2" md-component-id="left" md-is-locked-open="$mdMedia('gt-md')">
        <md-toolbar layout-padding layout-align="center start">
            <span>Script Editor</span>
        </md-toolbar>
        <md-content layout="column">
            <div class="left-side-menu" layout="column">
                <ul>
                    <li><md-button aria-label="New script" class="md-button" ng-click="actions.newScript()">New</md-button></li>
                    <li><md-button aria-label="Open script" class="md-button" ng-click="actions.openScript()">Open</md-button></li>
                    <li><md-button aria-label="Save script" class="md-button" ng-click="actions.saveScript(true)">Save</md-button></li>
                    <li><md-button aria-label="Run script" class="md-button" ng-click="actions.runScript()" ng-disabled="model.executingScript">Run</md-button></li>
                    <li><md-button aria-label="View latest results" class="md-button" ng-click="actions.viewLatestResults()">View Results</md-button></li>
                </ul>
            </div>
            <md-divider></md-divider>
            <md-toolbar layout-padding layout-align="center start">
                <span>Recent Files</span>
            </md-toolbar>
            <div class="left-side-menu-links" layout="column">
                <ul>
                    <li ng-repeat="item in model.recentFiles">
                        <md-button class="md-button" ng-click="actions.openScript(item)">
                            <span>{{item}}</span>
                            <!-- <md-tooltip>{{fileLink.scriptLink}}</md-tooltip> -->
                        </md-button>
                    </li>
                </ul>
            </div>
        </md-content>
    </md-sidenav>

  <div layout="column" flex>
    <md-toolbar>
      <div class="md-toolbar-tools">
        <md-button ng-click="toggleLeftMenu()" class="md-icon-button" hide-gt-md>
            <md-icon class="material-icons" md-font-icon="menu" aria-label="menu">menu</md-icon>
        </md-button>
        <!-- <span hide-gt-md>Script Editor &gt; </span> -->
        <span class="md-subhead">{{ model.userScript.scriptName }}</span>
        <span class="md-subhead" ng-show="model.userScript.dirty"><sup>*</sup></span>
        <!-- fill up the space between left and right area -->
        <span flex></span>
        <span class="md-body-1">
            <strong>${requestScope.user.email}</strong>
            <md-tooltip>You're logged in as ${requestScope.user.email}</md-tooltip>
        </span>
        <md-button class="md-icon-button" href="${requestScope.logoutUrl}">
            <md-tooltip>Logout</md-tooltip>
            <md-icon class="material-icons" md-font-icon="account_circle" aria-label="account_circle">account_circle</md-icon>
        </md-button>
      </div>
    </md-toolbar>
    <div layout="row" flex class="ace-monokai ace_dark" style="padding-top: 1rem">
      <div id="editor" layout="row" flex></div>
    </div>
  </div>
  
  <script type="text/ng-template" id="/ui/templates/resultDialog.tpl.html">
    <md-dialog aria-label="Results Dialog" flex="50">
        <md-dialog-content>
            <h2>Latest execution results</h2>
            <pre>{{scriptResult}}</pre>
        </md-dialog-content>
        <div class="md-actions">
            <md-button ng-click="closeDialog()" class="md-primary">Close</md-button>
        </div>
    </md-dialog>
  </script>
  <script type="text/ng-template" id="/ui/templates/runDialog.tpl.html">
    <md-dialog aria-label="Script Run Dialog" flex="50">
        <md-dialog-content>
            <h2>Are you sure to run the script?</h2>
            <md-checkbox ng-model="runDeferred" aria-label="Run deffered via Task Queue">
                Run deffered via Task Queue
            </md-checkbox>
        </md-dialog-content>
        <div class="md-actions">
            <md-button ng-click="yes()" class="md-primary">Yes</md-button>
            <md-button ng-click="no()" class="md-primary">No</md-button>
        </div>
    </md-dialog>
  </script>
  <script type="text/ng-template" id="/ui/templates/saveDialog.tpl.html">
    <md-dialog aria-label="Script Save Dialog" flex="50">
        <md-dialog-content>
            <h2>Save script as</h2>
            <md-input-container md-no-float>
                <label>Script name</label>
                <input ng-model="userScript.scriptName">
            </md-input-container>
        </md-dialog-content>
        <div class="md-actions">
            <md-button ng-click="yes()" class="md-primary" ng-disabled="!isScriptNameValid()">Yes</md-button>
            <md-button ng-click="no()" class="md-primary">No</md-button>
        </div>
    </md-dialog>
  </script>
  <script type="text/ng-template" id="/ui/templates/openDialog.tpl.html">
    <md-dialog aria-label="Open Script Dialog" flex="50">
        <md-dialog-content>
            <h2>Open script: {{selectedItem.scriptLink}}</h2>
            <div ng-show="error">{{error}}</div>
            <div ng-show="items && items.length > 0">
                <md-list-item ng-repeat="item in items" ng-click="selectItem(item)">
                    <p>{{item.scriptName}}</p>
                    <md-checkbox class="md-secondary"
                         ng-checked="isItemSelected(item)"
                         ng-click="selectItem(item)" 
                         aria-label="Toggle {{item.scriptName}}"></md-checkbox>
                </md-list-item>
                
            </div>
        </md-dialog-content>
        <div class="md-actions">
            <md-button ng-click="yes()" class="md-primary">Yes</md-button>
            <md-button ng-click="no()" class="md-primary">No</md-button>
        </div>
    </md-dialog>
  </script>
  
  <!-- Angular Material Dependencies -->
  <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.3.15/angular.min.js"></script>
  <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.3.15/angular-animate.min.js"></script>
  <script src="//ajax.googleapis.com/ajax/libs/angularjs/1.3.15/angular-aria.min.js"></script>
  <script src="//ajax.googleapis.com/ajax/libs/angular_material/0.10.0/angular-material.min.js"></script>
  <script src="//cdn.jsdelivr.net/ace/1.1.9/noconflict/ace.js"></script>
  <script src="//cdn.jsdelivr.net/ace/1.1.9/noconflict/mode-groovy.js"></script>
  <!-- 
  <script src="/ui/cm/lib/codemirror.js"></script>
  <script src="/ui/cm/mode/groovy/groovy.js"></script>
  <script src="/ui/cm/addon/edit/matchbrackets.js"></script>
  -->
  <script src="/ui/app.js"></script>
  <script src="/ui/services.js"></script>
  <script src="/ui/controllers.js"></script>
</body>
</html>