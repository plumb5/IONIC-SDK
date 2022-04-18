  
var exec = require('cordova/exec');

var plumb5 = function() {}

plumb5.prototype.setup = function(arg0) {
    console.log("inside setup token");
    exec(null, null, 'Plumb5', 'setup', arg0);
}

plumb5.prototype.deviceRegistration = function(arg0) {
    console.log("inside deviceRegistration token",arg0);
    exec(null, null, 'Plumb5', 'deviceRegistration', arg0);
}

plumb5.prototype.setUserDetails = function(arg0) {
    console.log("inside setUserDetails token",arg0);
    exec(null,null, 'Plumb5', 'setUserDetails', arg0);
}

plumb5.prototype.tracking = function(arg0) {
    console.log("inside tracking token");
    exec(null,null, 'Plumb5', 'tracking', arg0);
}

plumb5.prototype.notificationSubscribe = function(arg0,success, fail) {
    console.log("inside notificationSubscribe token",arg0);
    exec(success, fail, 'Plumb5', 'notificationSubscribe', arg0);
}
plumb5.prototype.eventPost = function(arg0) {
    console.log("inside eventPost token",arg0);
    exec(null,null, 'Plumb5', 'eventPost', arg0);
}
//plumb5
plumb5.prototype.pushResponsePost = function(arg0) {
    console.log("inside pushResponsePost token",arg0);
    exec(null,null, 'Plumb5', 'pushResponsePost', arg0);
}

module.exports = {

    init: function() {
        console.log("In Init")
        return new plumb5();
    },

    plumb5: plumb5
}