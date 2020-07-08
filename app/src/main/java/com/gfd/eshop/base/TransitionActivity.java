package com.gfd.eshop.base;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.gfd.eshop.R;
//过度动画
public abstract class TransitionActivity extends AppCompatActivity {


    //菜单按钮
    @Override public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //开始
    @Override public void startActivity(Intent intent) {
        super.startActivity(intent);
        setTransitionAnimation(true);
    }

    @Override public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        setTransitionAnimation(true);
    }

    @Override public void finish() {
        super.finish();
        setTransitionAnimation(false);
    }

    public void finishWithDefaultTransition() {
        super.finish();
    }

    private void setTransitionAnimation(boolean newActivityIn) {
        if (newActivityIn) {
            // 新页面从右边进入
            overridePendingTransition(R.anim.push_right_in,
                    R.anim.push_right_out);
        } else {
            // 上一个页面从左边进入
            overridePendingTransition(R.anim.push_left_in,
                    R.anim.push_left_out);
        }

    }
}
