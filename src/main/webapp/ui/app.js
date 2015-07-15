(function(angular){
    'use strict';
    angular
    .module('App', ['ngMaterial'])
    .config(['$provide', '$mdThemingProvider', function($provide, $mdThemingProvider) {
        $mdThemingProvider.theme('default').primaryPalette('cyan');
        $provide.factory('AceScriptEditor', function(){
            var editor = window.ScriptEditor;
            return editor;
        });
    }]);
})(angular);

(function(window, ace){
    var e = window.ScriptEditor = ace.edit("editor");
    e.setTheme("ace/theme/monokai");
    e.getSession().setMode("ace/mode/groovy");
    e.$blockScrolling = Infinity;
})(window, ace);

/*
(function(){
    CodeMirror.fromTextArea(document.querySelector('#editor'), {
        lineNumbers: true,
        mode: 'groovy',
        theme: 'eclipse',
        indentUnit: 4,
        styleActiveLine: true,
      });
})();
*/

