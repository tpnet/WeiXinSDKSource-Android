package com.tencent.mm.opensdk.diffdev.a;

import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Environment;
import android.util.Base64;

import com.tencent.mm.opensdk.diffdev.OAuthErrCode;
import com.tencent.mm.opensdk.diffdev.OAuthListener;
import com.tencent.mm.opensdk.utils.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class d extends AsyncTask {
    private static final boolean h;
    private static final String i = (Environment.getExternalStorageDirectory().getAbsolutePath() + "/tencent/MicroMsg/oauth_qrcode.png");
    private static String j;
    private String appId;
    private String k;
    private String l;
    private OAuthListener m;
    private f n;
    private String scope;
    private String signature;

    static class a {
        public OAuthErrCode o;
        public String p;
        public String q;
        public String r;
        public int s;
        public String t;
        public byte[] u;

        private a() {
        }

        public static a a(byte[] bArr) {
            a aVar = new a();
            if (bArr == null || bArr.length == 0) {
                Log.e("MicroMsg.SDK.GetQRCodeResult", "parse fail, buf is null");
                aVar.o = OAuthErrCode.WechatAuth_Err_NetworkErr;
            } else {
                try {
                    try {
                        JSONObject jSONObject = new JSONObject(new String(bArr, "utf-8"));
                        int i = jSONObject.getInt("errcode");
                        if (i != 0) {
                            Log.e("MicroMsg.SDK.GetQRCodeResult", String.format("resp errcode = %d", new Object[]{Integer.valueOf(i)}));
                            aVar.o = OAuthErrCode.WechatAuth_Err_NormalErr;
                            aVar.s = i;
                            aVar.t = jSONObject.optString("errmsg");
                        } else {
                            String string = jSONObject.getJSONObject("qrcode").getString("qrcodebase64");
                            if (string == null || string.length() == 0) {
                                Log.e("MicroMsg.SDK.GetQRCodeResult", "parse fail, qrcodeBase64 is null");
                                aVar.o = OAuthErrCode.WechatAuth_Err_JsonDecodeErr;
                            } else {
                                byte[] decode = Base64.decode(string, 0);
                                if (decode == null || decode.length == 0) {
                                    Log.e("MicroMsg.SDK.GetQRCodeResult", "parse fail, qrcodeBuf is null");
                                    aVar.o = OAuthErrCode.WechatAuth_Err_JsonDecodeErr;
                                } else if (d.h) {
                                    File file = new File(d.i);
                                    file.mkdirs();
                                    if (file.exists()) {
                                        file.delete();
                                    }
                                    if (a(d.i, decode)) {
                                        aVar.o = OAuthErrCode.WechatAuth_Err_OK;
                                        aVar.r = d.i;
                                        aVar.p = jSONObject.getString("uuid");
                                        aVar.q = jSONObject.getString("appname");
                                        Log.d("MicroMsg.SDK.GetQRCodeResult", String.format("parse succ, save in external storage, uuid = %s, appname = %s, imgPath = %s", new Object[]{aVar.p, aVar.q, aVar.r}));
                                    } else {
                                        Log.e("MicroMsg.SDK.GetQRCodeResult", String.format("writeToFile fail, qrcodeBuf length = %d", new Object[]{Integer.valueOf(decode.length)}));
                                        aVar.o = OAuthErrCode.WechatAuth_Err_NormalErr;
                                    }
                                } else {
                                    aVar.o = OAuthErrCode.WechatAuth_Err_OK;
                                    aVar.u = decode;
                                    aVar.p = jSONObject.getString("uuid");
                                    aVar.q = jSONObject.getString("appname");
                                    Log.d("MicroMsg.SDK.GetQRCodeResult", String.format("parse succ, save in memory, uuid = %s, appname = %s, imgBufLength = %d", new Object[]{aVar.p, aVar.q, Integer.valueOf(aVar.u.length)}));
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e("MicroMsg.SDK.GetQRCodeResult", String.format("parse json fail, ex = %s", new Object[]{e.getMessage()}));
                        aVar.o = OAuthErrCode.WechatAuth_Err_NormalErr;
                    }
                } catch (Exception e2) {
                    Log.e("MicroMsg.SDK.GetQRCodeResult", String.format("parse fail, build String fail, ex = %s", new Object[]{e2.getMessage()}));
                    aVar.o = OAuthErrCode.WechatAuth_Err_NormalErr;
                }
            }
            return aVar;
        }

        private static boolean a(String str, byte[] bArr) {
            FileOutputStream fileOutputStream;
            Exception e;
            Throwable th;
            try {
                fileOutputStream = new FileOutputStream(str);
                try {
                    fileOutputStream.write(bArr);
                    fileOutputStream.flush();
                    try {
                        fileOutputStream.close();
                    } catch (IOException e2) {
                        Log.e("MicroMsg.SDK.GetQRCodeResult", "fout.close() exception:" + e2.getMessage());
                    }
                    Log.d("MicroMsg.SDK.GetQRCodeResult", "writeToFile ok!");
                    return true;
                } catch (Exception e3) {
                    e = e3;
                    try {
                        Log.w("MicroMsg.SDK.GetQRCodeResult", "write to file error, exception:" + e.getMessage());
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException e22) {
                                Log.e("MicroMsg.SDK.GetQRCodeResult", "fout.close() exception:" + e22.getMessage());
                            }
                        }
                        return false;
                    } catch (Throwable th2) {
                        th = th2;
                        if (fileOutputStream != null) {
                            try {
                                fileOutputStream.close();
                            } catch (IOException e4) {
                                Log.e("MicroMsg.SDK.GetQRCodeResult", "fout.close() exception:" + e4.getMessage());
                            }
                        }
                        throw th;
                    }
                }
            } catch (Exception e5) {
                e = e5;
                fileOutputStream = null;
                Log.w("MicroMsg.SDK.GetQRCodeResult", "write to file error, exception:" + e.getMessage());

                return false;
            } catch (Throwable th3) {
                th = th3;
                fileOutputStream = null;

                return false;

            }
        }
    }

    static {
        boolean z = Environment.getExternalStorageState().equals("mounted") && new File(Environment.getExternalStorageDirectory().getAbsolutePath()).canWrite();
        h = z;
        j = null;
        j = "http://open.weixin.qq.com/connect/sdk/qrconnect?appid=%s&noncestr=%s&timestamp=%s&scope=%s&signature=%s";
    }

    public d(String str, String str2, String str3, String str4, String str5, OAuthListener oAuthListener) {
        this.appId = str;
        this.scope = str2;
        this.k = str3;
        this.l = str4;
        this.signature = str5;
        this.m = oAuthListener;
    }

    public final boolean a() {
        Log.i("MicroMsg.SDK.GetQRCodeTask", "cancelTask");
        return this.n == null ? cancel(true) : this.n.cancel(true);
    }

    protected final /* synthetic */ Object doInBackground(Object[] objArr) {
        Log.i("MicroMsg.SDK.GetQRCodeTask", "external storage available = " + h);
        String format = String.format(j, new Object[]{this.appId, this.k, this.l, this.scope, this.signature});
        long currentTimeMillis = System.currentTimeMillis();
        byte[] b = e.a(format, -1);
        Log.d("MicroMsg.SDK.GetQRCodeTask", String.format("doInBackground, url = %s, time consumed = %d(ms)", new Object[]{format, Long.valueOf(System.currentTimeMillis() - currentTimeMillis)}));
        return a.a(b);
    }

    protected final /* synthetic */ void onPostExecute(Object obj) {
        a aVar = (a) obj;
        if (aVar.o == OAuthErrCode.WechatAuth_Err_OK) {
            Log.d("MicroMsg.SDK.GetQRCodeTask", "onPostExecute, get qrcode success");
            this.m.onAuthGotQrcode(aVar.r, aVar.u);
            this.n = new f(aVar.p, this.m);
            f fVar = this.n;
            if (VERSION.SDK_INT >= 11) {
                fVar.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                return;
            } else {
                fVar.execute( );
                return;
            }
        }
        Log.e("MicroMsg.SDK.GetQRCodeTask", String.format("onPostExecute, get qrcode fail, OAuthErrCode = %s", new Object[]{aVar.o}));
        this.m.onAuthFinish(aVar.o, null);
    }
}
