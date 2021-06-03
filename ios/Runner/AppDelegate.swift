import UIKit
import Flutter
import Xendit

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
    let controller : FlutterViewController = window?.rootViewController as! FlutterViewController
    let methodChannel = FlutterMethodChannel(name: "xendit.co/cards",
                                                  binaryMessenger: controller.binaryMessenger)
    methodChannel.setMethodCallHandler({ [self]
          (call: FlutterMethodCall, result: @escaping FlutterResult) -> Void in
          // Note: this method is invoked on the UI thread.
        guard (call.method == "tokenize") else {
            result(FlutterMethodNotImplemented)
                return
        }
        let args = call.arguments as! [String]
        print(args)
        let card = CardData.init()
        card.amount = 10000
        card.cardNumber = args[0]
        card.cardExpMonth = args[1]
        card.cardExpYear = args[2]
        card.cardCvn = args[3]
        self.tokenize(card: card, controller: controller, result: result)
        
    })
    GeneratedPluginRegistrant.register(with: self)
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
    
    private func tokenize(card: CardData, controller: UIViewController, result: @escaping FlutterResult) {
        Xendit.publishableKey = ""
        Xendit.createToken(fromViewController: controller, cardData: card) { (token, error) in
            let encoder = JSONEncoder()
            encoder.keyEncodingStrategy = .convertToSnakeCase
            let jsonData = try? encoder.encode(token);
            let jsonString = String(data: jsonData!, encoding: .utf8)
            result(jsonString)
        }
    }
}
