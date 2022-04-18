
@objc(Plumb5) class Plumb5 : CDVPlugin {
  @objc(setup:) // Declare your function name.
  func setup(command: CDVInvokedUrlCommand) { 

 
    var pluginResult = CDVPluginResult (status: CDVCommandStatus_ERROR, messageAs: "The Plugin Failed");
    
        pluginResult = CDVPluginResult(status: CDVCommandStatus_OK, messageAs: "The plugin succeeded");

    self.commandDelegate!.send(pluginResult, callbackId: command.callbackId);
  }
}