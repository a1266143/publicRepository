package com.example.testproject.architecture_components.data_binding.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.testproject.R;
import com.example.testproject.architecture_components.data_binding.bean.User;
import com.example.testproject.databinding.ActivityDataBindingBinding;

public class DataBindingActivity extends AppCompatActivity {

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.e("xiaojun","onSaveInstanceState(),当前lifecycle State="+
                getLifecycle().getCurrentState());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("xiaojun","onStop()");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityDataBindingBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_data_binding);
        binding.setName("姓名");
        User user = new User("晓军","李",27);
        binding.setUser(user);
        getLifecycle().addObserver(new MyObserver());
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.setUser(new User("秋月","庞",25));
            }
        },2000);
    }

    public class MyObserver implements LifecycleObserver{

        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        public void onCreate(){
            Log.e("xiaojun","on_create");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        public void onStart(){
            Log.e("xiaojun","on_start");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
        public void onStop(){
            Log.e("xiaojun","on_stop");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onDestroy(){
            Log.e("xiaojun","on_destroy");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        public void onResume(){
            Log.e("xiaojun","on_resume");
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        public void onPause(){
            Log.e("xiaojun","on_pause");
        }

    }
}
