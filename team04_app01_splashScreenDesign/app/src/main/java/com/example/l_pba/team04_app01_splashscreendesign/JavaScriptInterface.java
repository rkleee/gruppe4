/**
 * @author group04
 * @version 1.0
 * JacascriptInterface, to allow communication between java and javascript
 */
package com.example.l_pba.team04_app01_splashscreendesign;

/**
 * Necessary Imports for Interface
 */
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * JacascriptInterface for Communication
 */
public class JavaScriptInterface {
    Context mContext;

    /** Instantiate the interface and set the context */
    JavaScriptInterface(Context c) {
        mContext = c;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

}
