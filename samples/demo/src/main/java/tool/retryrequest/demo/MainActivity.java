package tool.retryrequest.demo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {

    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button btn1 = (Button) findViewById(R.id.btn1);
        final TextView btn1ret = (TextView) findViewById(R.id.btn1_ret);
        final Button btn2 = (Button) findViewById(R.id.btn2);
        final TextView btn2ret = (TextView) findViewById(R.id.btn2_ret);

        if (mHandler == null) {
            HandlerThread ht = new HandlerThread("phonestate-single-thread") {
                {
                    start();
                }
            };
            mHandler = new Handler(ht.getLooper());
        }

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
