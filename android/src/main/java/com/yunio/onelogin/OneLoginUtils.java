package com.yunio.onelogin;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import com.cmic.sso.sdk.AuthRegisterViewConfig;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.geetest.onelogin.OneLoginHelper;
import com.geetest.onelogin.config.OneLoginThemeConfig;
import com.geetest.onelogin.listener.AbstractOneLoginListener;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * OneLogin工具类
 */
public class OneLoginUtils extends ReactContextBaseJavaModule {

  //预取号失败
  private static final String PRE_FAIL = "0";
  //取号失败
  private static final String REQUEST_FAIL = "1";
  //获取手机号接口失败
  private static final String PHONE_FAIL = "2";
  private String secretText = "隐私服务";
  private String userText = "用户条款";
  private String secretUrl = "https://www.baidu.com/";
  private String userUrl = "https://www.baidu.com/";

  /**
   * 后台申请的 oneLogin APPID
   * 谨记：APPID需绑定相关的包名和包签名(这两项信息需要后台申请)
   */
  public static final String CUSTOM_ID = "aad22";

  /**
   * 返回状态为200则表示成功
   */
  public static final int ONE_LOGIN_SUCCESS_STATUS = 200;

  /**
   * 日志 TAG
   */
  public static final String TAG = "OneLogin";


  private Handler backHandler;
  private Handler mainHandler = new Handler(Looper.getMainLooper());
  private Context context;

  public OneLoginUtils(ReactApplicationContext reactContext) {
    super(reactContext);
    this.context = reactContext;
    HandlerThread handlerThread = new HandlerThread("oneLogin-demo");
    handlerThread.start();
    backHandler = new Handler(handlerThread.getLooper());
  }

  /**
   * 初始化 需在 <p>onCreate</p> 方法内使用
   * 在初始化的时候进行预取号操作
   * 由于预取号是耗时操作 也可以放在application的onCreate方法中使用
   */
  @ReactMethod
  public void oneLoginInit(boolean debug) {
    OneLoginHelper.with().init(context);
    OneLoginHelper.with().setLogEnable(debug);
    //        oneLoginPreGetToken(false, promise);
  }

  /**
   * 自定义配置文案 和 url
   */
  @ReactMethod
  public void setAdditionalPrivacyTerms(ReadableMap items) {
    if (items.hasKey("secretText")) {
      this.secretText = items.getString("secretText");
    }
    if (items.hasKey("userText")) {
      this.userText = items.getString("userText");
    }
    if (items.hasKey("secretUrl")) {
      this.secretUrl = items.getString("secretUrl");
    }
    if (items.hasKey("userUrl")) {
      this.userUrl = items.getString("userUrl");
    }
  }
  /**
   * 预取号接口
   * 在初始化的时候进行预取号操作
   * 由于预取号是耗时操作 也可以放在application的onCreate方法中使用
   * <p>
   * 注意:开发者调用过程中，超时时间需设置在5秒左右
   */
  private void oneLoginPreGetToken(String appKey, final boolean isRequestToken, final Promise promise) {
    Log.i(TAG, "preGetToken doing");
    OneLoginHelper.with().preGetToken(appKey, 5000, new AbstractOneLoginListener() {
      @Override
      public void onResult(JSONObject jsonObject) {
        Log.i(TAG, "预取号结果为：" + jsonObject.toString());
        try {
          int status = jsonObject.getInt("status");
          if (status == ONE_LOGIN_SUCCESS_STATUS) {
            if (isRequestToken) {
              oneLoginRequestToken(promise);
            }
          } else {
            promise.reject(PRE_FAIL, jsonObject.toString());
            //                        ToastUtils.toastMessage(context, "预取号失败:" + jsonObject.toString());
          }
        } catch (JSONException e) {
          promise.reject(PRE_FAIL, jsonObject.toString());
          //                    ToastUtils.toastMessage(context, "预取号失败:" + jsonObject.toString());
        }
      }
    });
  }

  /**
   * 配置页面布局(默认竖屏)
   *
   * @return config
   */
  private OneLoginThemeConfig initConfig() {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    int width = (int) (displayMetrics.widthPixels / displayMetrics.density + 0.5f);
    Log.d(TAG, "width : " + width);
    return new OneLoginThemeConfig.Builder()
      .setAuthBGImgPath("gt_one_login_bg")
      .setDialogTheme(true, width, 370, 0, 0, true, false)
      //为了在demo里展示页面实现沉浸式效果，此处第一个参数的默认值为0，第三个参数的默认值为false 。分别改为0xFFFFFFFF和true
      .setStatusBar(0xFFFFFFFF, 0, true)
      .setAuthNavLayout(0xFF3973FF, 61, true, false)
      //                .setAuthNavLayout(0xFFFF0000, 49, false, false)
      .setAuthNavTextView("快捷登录", 0xFF343434, 20, false, "", 0xFF000000, 17)
      .setAuthNavReturnImgView("icon_black_back", 20, 20, false, 10)
      .setLogoImgView("gt_one_login_logo", 209, 45, true, 40, 0, 0)
      .setNumberView(0xFF000000, 28, 40, 0, 0)
      .setSwitchView("切换账号", 0xFF3973FF, 14, true, 249, 0, 0)
      .setLogBtnLayout("login_bg", 315, 48, 135, 0, 0)
      .setLogBtnTextView("一键登录", 0xFFFFFFFF, 17)
      .setLogBtnLoadingView("umcsdk_load_dot_white", 20, 20, 12)
      .setSloganView(0xFF949494, 12, 86, 0, 0)
      .setPrivacyCheckBox("", "", true, 0, 0, 7)
      //                .setPrivacyClauseText("应用自定义服务条款一", "http://a.b.c", "", "", "应用自定义服务条款二", "http://x.y.z")
      .setPrivacyLayout(256, 225, 0, 0, true)
      .setPrivacyClauseView(0xFF949494, 0xFF000000, 12)
      //                .setPrivacyTextView("登录即同意", "和", "、", "并使用本机号码登录")
      .setPrivacyClauseTextStrings("登录代表您已阅读并同意", this.userText, this.userUrl, "和",
        "", this.secretText, this.secretUrl, "以及",
        "", "", "", "")
      .setPrivacyTextGravity(Gravity.CENTER)
      //                .setPrivacyAddFrenchQuotes(true)
      //0.7.0之后新增 设置字体相关
      .setAuthNavTextViewTypeface(Typeface.DEFAULT, Typeface.DEFAULT)
      .setNumberViewTypeface(Typeface.DEFAULT)
      .setSwitchViewTypeface(Typeface.DEFAULT)
      .setLogBtnTextViewTypeface(Typeface.DEFAULT)
      .setSloganViewTypeface(Typeface.DEFAULT)
      .setPrivacyClauseViewTypeface(Typeface.DEFAULT, Typeface.DEFAULT)
      //0.8.0之后新增 设置点击提示toast文字
      .setPrivacyUnCheckedToastText("请同意服务条款")
      .build();
  }

  private void initLoginLine() {
    LayoutInflater inflater1 = LayoutInflater.from(context);
    RelativeLayout relativeLayout = (RelativeLayout) inflater1.inflate(R.layout.relative_item_view, null);
    //        RelativeLayout.LayoutParams layoutParamsOther = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    //        layoutParamsOther.setMargins(0, 0, 0, 0);
    //        relativeLayout.setLayoutParams(layoutParamsOther);
    OneLoginHelper.with().addOneLoginRegisterViewConfig("title_line", new AuthRegisterViewConfig.Builder()
      .setView(relativeLayout)
      .setRootViewId(AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_BODY)
      .build()
    );
  }

  /**
   * 定义的第三方登录设置
   */
  private void initLogin() {
    //        LayoutInflater inflater1 = LayoutInflater.from(context);
    //        RelativeLayout relativeLayout = (RelativeLayout) inflater1.inflate(R.layout.relative_item_view, null);
    //        RelativeLayout.LayoutParams layoutParamsOther = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    //        layoutParamsOther.setMargins(0, dip2px(context, 430), 0, 0);
    //        layoutParamsOther.addRule(RelativeLayout.CENTER_HORIZONTAL);
    //        relativeLayout.setLayoutParams(layoutParamsOther);
    //        ImageView weixin = relativeLayout.findViewById(R.id.weixin);
    //        ImageView qq = relativeLayout.findViewById(R.id.qq);
    //        ImageView weibo = relativeLayout.findViewById(R.id.weibo);
    //        weixin.setOnClickListener(new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                ToastUtils.toastMessage(context, "微信登录");
    //            }
    //        });
    //        qq.setOnClickListener(new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                ToastUtils.toastMessage(context, "qq登录");
    //
    //            }
    //        });
    //        weibo.setOnClickListener(new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                ToastUtils.toastMessage(context, "微博登录");
    //
    //            }
    //        });
    //        OneLoginHelper.with().addOneLoginRegisterViewConfig("title_button", new AuthRegisterViewConfig.Builder()
    //                .setView(relativeLayout)
    //                .setRootViewId(AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_BODY)
    //                .setCustomInterface(new CustomInterface() {
    //                    @Override
    //                    public void onClick(Context context) {
    //                        ToastUtils.toastMessage(context, "动态注册的其他按钮");
    //                    }
    //                })
    //                .build()
    //        );
  }

  /**
   * 点击进行判断是否要进行取号
   * 由于预取号是放在初始化时候的，所以这个方法使用之前需判断预取号是否成功以及预取号accessCode是否超时
   * 比如OneLoginHelper.with().isPreGetTokenSuccess()&&!OneLoginHelper.with().isAccessCodeExpired()
   * 如果预取号失败则需要重新进行预取号
   * 为了防止调用cancel方法将上下文置空所导致的问题，所以先判断初始化是否成功
   * 当初始化失败的时候再重新初始化 即判断!OneLoginHelper.with().isInitSuccess() && context != null
   */
  @ReactMethod
  public void requestToken(String appKey, boolean debug, Promise promise) {
    if (!OneLoginHelper.with().isInitSuccess() && context != null) {
      OneLoginHelper.with().init(context);
      OneLoginHelper.with().setLogEnable(debug);
    }
    if (OneLoginHelper.with().isPreGetTokenSuccess() && !OneLoginHelper.with().isAccessCodeExpired()) {
      oneLoginRequestToken(promise);
    } else {
      //注意，如果提前预取号的话，为了避免还没结果之前再次回调被多次调用，需要判断预取号是否完成
      //这里可以设计成当没完成预取号的话，按钮禁止点击等等。
      //同时开发者对于提前预取号失败的话，也可以直接进行降级策略,比如收发短信等等。
      //      Log.i(TAG, "OneLoginHelper.with().isPreGetTokenComplete()：" + OneLoginHelper.with().isPreGetTokenComplete());
      //      if (!OneLoginHelper.with().isPreGetTokenComplete()) {
      //                ToastUtils.toastMessage(context, "当前预取号还没成功");
      //      } else {
      oneLoginPreGetToken(appKey, true, promise);
      //      }
    }

  }

  /**
   * 取号接口
   * 在这个方法里也可以配置自定义的布局页面
   * 比如    initView() initLogin()
   * 注意:0.8.0之后的版本loading由用户自己控制消失时间
   */
  private void oneLoginRequestToken(final Promise promise) {
    initLoginLine();
    Log.i(TAG, "requestToken doing");
    OneLoginHelper.with().requestToken(initConfig(), new AbstractOneLoginListener() {
      @Override
      public void onResult(final JSONObject jsonObject) {
        Log.i(TAG, "取号结果为：" + jsonObject.toString());
        try {
          int status = jsonObject.getInt("status");
          if (status == ONE_LOGIN_SUCCESS_STATUS) {
            final String process_id = jsonObject.getString("process_id");
            final String token = jsonObject.getString("token");
            /**
             * authcode值只有电信卡才会返回 所以需要判断是否存在 有则进行赋值
             */
            final String authcode = jsonObject.optString("authcode");
            backHandler.post(new Runnable() {
              @Override
              public void run() {
                //                                verify(process_id, token, authcode, promise);
                WritableMap writableMap = new WritableNativeMap();
                writableMap.putString("process_id", process_id);
                writableMap.putString("token", token);
                writableMap.putString("authcode", authcode);
                promise.resolve(writableMap);
              }
            });
          } else {
            //在页面返回失败的情况下 如果不注重实用返回的监听事件，可以对于返回的事件不进行处理
            String errorCode = jsonObject.getString("errorCode");
            if (errorCode.equals("-20301") || errorCode.equals("-20302")) {
              //                            ToastUtils.toastMessage(context, "当前关闭了授权页面");
              return;
            }
            OneLoginHelper.with().dismissAuthActivity();
            promise.reject(REQUEST_FAIL, jsonObject.toString());
            //                        ToastUtils.toastMessage(context, "取号失败:" + jsonObject.toString());
          }
        } catch (JSONException e) {
          OneLoginHelper.with().dismissAuthActivity();
          promise.reject(REQUEST_FAIL, jsonObject.toString());
          //                    ToastUtils.toastMessage(context, "取号失败:" + jsonObject.toString());
        }
      }

      @Override
      public void onPrivacyClick(String s, String s1) {
        //                ToastUtils.toastMessage(context, "当前点击了隐私条款名为：" + s + "---地址为:" + s1);
      }

      @Override
      public void onLoginButtonClick() {
        Log.i(TAG, "当前点击了登录按钮");
      }

      @Override
      public void onAuthActivityCreate(Activity activity) {
        //                ToastUtils.toastMessage(context, "当前弹起授权页面:" + activity.getClass().getSimpleName());
      }

      @Override
      public void onAuthWebActivityCreate(Activity activity) {
        //                ToastUtils.toastMessage(context, "当前弹起授权Web页面:" + activity.getClass().getSimpleName());
      }

      @Override
      public void onPrivacyCheckBoxClick(boolean b) {
        //                ToastUtils.toastMessage(context, "当前点击了CheckBox---" + b);
      }
    });
  }

  @ReactMethod
  public void dismissAuth() {
    OneLoginHelper.with().stopLoading();
    OneLoginHelper.with().dismissAuthActivity();
  }

  /**
   * 手机号校验接口 需要网站主配置相关接口进行获取手机号等操作
   * 在这个阶段，也需要调用OneLoginHelper.with().dismissAuthActivity()来进行授权页的销毁
   * 注意:0.8.0之后的版本loading由用户自己控制消失时间
   */
  private void verify(String id, String token, String authcode, final Promise promise) {
    //        JSONObject jsonObject = new JSONObject();
    //        try {
    //            jsonObject.put("process_id", id);
    //            jsonObject.put("token", token);
    //            jsonObject.put("authcode", authcode);
    //            jsonObject.put("id_2_sign", CUSTOM_ID);
    //        } catch (JSONException e) {
    //            e.printStackTrace();
    //        }
    //        final String result = HttpUtils.requestNetwork(CHECK_PHONE_URL, jsonObject);
    //        Log.i(TAG, "校验结果为:" + result);
    //        mainHandler.post(new Runnable() {
    //            @Override
    //            public void run() {
    //
    //                /**
    //                 * 关闭loading动画
    //                 */
    OneLoginHelper.with().stopLoading();
    //
    //                /**
    //                 * 关闭授权页面
    //                 * sdk内部除了返回等相关事件以外是不关闭授权页面的，需要手动进行关闭
    //                 */
    //                OneLoginHelper.with().dismissAuthActivity();
    //                try {
    //                    JSONObject jsonObject1 = new JSONObject(result);
    //                    int status = jsonObject1.getInt("status");
    //                    if (status == ONE_LOGIN_SUCCESS_STATUS) {
    //                        promise.resolve(result);
    //                        ToastUtils.toastMessage(context, "校验成功:" + result);
    //                    } else {
    //                        promise.reject(PHONE_FAIL, jsonObject.toString());
    //                        ToastUtils.toastMessage(context, "校验失败:" + result);
    //                    }
    //                } catch (JSONException e) {
    //                    promise.reject(PHONE_FAIL, result);
    //                    ToastUtils.toastMessage(context, "校验失败:" + result);
    //                }
    //
    //            }
    //        });

  }

  private static int dip2px(Context context, float dpValue) {
    float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  @NonNull
  @Override
  public String getName() {
    return "OneLogin";
  }
}
