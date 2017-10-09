package com.tencent.mm.opensdk.diffdev.a;

import android.os.AsyncTask;

import com.tencent.mm.opensdk.diffdev.OAuthErrCode;
import com.tencent.mm.opensdk.diffdev.OAuthListener;
import com.tencent.mm.opensdk.utils.Log;

import org.json.JSONObject;

final class f extends AsyncTask  {
    private OAuthListener m;
    private String p;
    private String url;
    private int v;

    static class a {
        public OAuthErrCode o;
        public String w;
        public int x;

        a() {}

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public static com.tencent.mm.opensdk.diffdev.a.f.a b(byte[] arg12) {
            String v1_1;
            long v8 = 6979321856L;
            int v7 = 52;

            a v0 = new a();
            if(arg12 != null && arg12.length != 0) {
                try {
                    v1_1 = new String(arg12, "utf-8");
                }
                catch(Exception v1) {
                    Log.e("MicroMsg.SDK.NoopingResult", String.format("parse fail, build String fail, ex = %s", v1.getMessage()));
                    v0.o = OAuthErrCode.WechatAuth_Err_NormalErr;

                    return v0;
                }

                try {
                    JSONObject v2 = new JSONObject(v1_1);
                    v0.x = v2.getInt("wx_errcode");
                    Log.d("MicroMsg.SDK.NoopingResult", String.format("nooping uuidStatusCode = %d", Integer.valueOf(v0.x)));
                    switch(v0.x) {
                        case 402: {
                            v0.o = OAuthErrCode.WechatAuth_Err_Timeout;

                        }
                        case 403: {
                            v0.o = OAuthErrCode.WechatAuth_Err_Cancel;
                            v0.o = OAuthErrCode.WechatAuth_Err_NormalErr;
                            return v0;
                        }
                        case 404: {
                            v0.o = OAuthErrCode.WechatAuth_Err_OK;

                        }
                        case 405: {
                            v0.o = OAuthErrCode.WechatAuth_Err_OK;
                            v0.w = v2.getString("wx_code");

                        }
                        case 408: {
                            v0.o = OAuthErrCode.WechatAuth_Err_OK;

                        }
                        case 500: {
                            v0.o = OAuthErrCode.WechatAuth_Err_NormalErr;

                        }
                        default: {
                            v0.o = OAuthErrCode.WechatAuth_Err_NormalErr;

                        }
                    }
                }
                catch(Exception v1) {
                    v1.printStackTrace();
                }

            }
            else {
                Log.e("MicroMsg.SDK.NoopingResult", "parse fail, buf is null");
                v0.o = OAuthErrCode.WechatAuth_Err_NetworkErr;

            }
            return v0;
        }
    }

    public f(String str, OAuthListener oAuthListener) {
        this.p = str;
        this.m = oAuthListener;
        this.url = String.format("https://long.open.weixin.qq.com/connect/l/qrconnect?f=json&uuid=%s", new Object[]{str});
    }

    protected final /* synthetic */ Object doInBackground(Object[] objArr) {
        if (this.p == null || this.p.length() == 0) {
            Log.e("MicroMsg.SDK.NoopingTask", "run fail, uuid is null");
            a aVar = new a();
            aVar.o = OAuthErrCode.WechatAuth_Err_NormalErr;
            return aVar;
        }
        while (!isCancelled()) {
            String str = this.url + (this.v == 0 ? "" : "&last=" + this.v);
            long currentTimeMillis = System.currentTimeMillis();
            byte[] b = e.a(str, 60000);
            long currentTimeMillis2 = System.currentTimeMillis();
            a aVar = a.b(b);
            Log.d("MicroMsg.SDK.NoopingTask", String.format("nooping, url = %s, errCode = %s, uuidStatusCode = %d, time consumed = %d(ms)", new Object[]{str, aVar.o.toString(), Integer.valueOf(aVar.x), Long.valueOf(currentTimeMillis2 - currentTimeMillis)}));
            if (aVar.o == OAuthErrCode.WechatAuth_Err_OK) {
                this.v = aVar.x;
                if (aVar.x == g.UUID_SCANED.getCode()) {
                    this.m.onQrcodeScanned();
                } else if (aVar.x != g.UUID_KEEP_CONNECT.getCode() && aVar.x == g.UUID_CONFIRM.getCode()) {
                    if (aVar.w != null && aVar.w.length() != 0) {
                        return aVar;
                    }
                    Log.e("MicroMsg.SDK.NoopingTask", "nooping fail, confirm with an empty code!!!");
                    aVar.o = OAuthErrCode.WechatAuth_Err_NormalErr;
                    return aVar;
                }
            }
            Log.e("MicroMsg.SDK.NoopingTask", String.format("nooping fail, errCode = %s, uuidStatusCode = %d", new Object[]{aVar.o.toString(), Integer.valueOf(aVar.x)}));
            return aVar;
        }
        Log.i("MicroMsg.SDK.NoopingTask", "IDiffDevOAuth.stopAuth / detach invoked");
        a aVar = new a();
        aVar.o = OAuthErrCode.WechatAuth_Err_Auth_Stopped;
        return aVar;
    }

    protected final /* synthetic */ void onPostExecute(Object obj) {
        a aVar = (a) obj;
        this.m.onAuthFinish(aVar.o, aVar.w);
    }
}
