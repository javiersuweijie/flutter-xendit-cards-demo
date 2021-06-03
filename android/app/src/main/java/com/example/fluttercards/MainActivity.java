package com.example.fluttercards;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.xendit.Models.Card;
import com.xendit.Models.Token;
import com.xendit.Models.XenditError;
import com.xendit.TokenCallback;
import com.xendit.Xendit;

import java.util.List;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "xendit.co/cards";
    private static Xendit xendit;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        xendit = new Xendit(getApplicationContext(), "", this);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            if (call.method.equals("tokenize")) {
                                try {
                                    List<String> args = call.arguments();
                                    Card card = new Card(args.get(0),
                                            args.get(1),
                                            args.get(2),
                                            args.get(3));
                                    tokenize(card, result);
                                } catch (Throwable e) {
                                    result.success(e.getMessage());
                                }
                            } else {
                                result.notImplemented();
                            }
                        }
                );
    }

    private void tokenize(Card card, MethodChannel.Result result) {
        Gson g = new Gson();
        final TokenCallback callback = new TokenCallback() {
            @Override
            public void onSuccess(Token token) {
                String json = g.toJson(token);
                result.success(json);
            }

            @Override
            public void onError(XenditError xenditError) {
                String json = g.toJson(xenditError);
                result.success(json);

            }
        };
        xendit.createSingleUseToken(card, 10000, true, callback);
    }

}
