//package com.anter.ToDo;
//
//import android.os.Bundle;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.app.AppCompatDelegate;
//
//
//import android.os.Bundle;
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentTransaction;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//public class SettingsActivity extends AppCompatActivity {
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_settings);
//        getWindow().getDecorView().getRootView().setLayoutDirection(MainActivity.userPreferences.getLayoutDirectionStatus());
////
////        // Initialize your views
////        TextView textViewTitle = findViewById(R.id.textViewTitle);
////        LinearLayout buttonNotifications = findViewById(R.id.buttonNotifications);
////        LinearLayout buttonPreferences = findViewById(R.id.buttonPreferences);
////        // ... Repeat similar initialization for other LinearLayouts
////
////        // Set onClickListeners for each button LinearLayout
////        buttonNotifications.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                // Handle click on Notifications button
////                openFragment(new NotificationsFragment());
////            }
////        });
////
////        buttonPreferences.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                // Handle click on Preferences button
////                openFragment(new PreferencesFragment());
////            }
////        });
////    }
////
////    private void openFragment(Fragment fragment) {
////        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
////        transaction.replace(R.id.fragmentContainer, fragment);
////        transaction.addToBackStack(null);
////        transaction.commit();
//    }
//}
/*
<!--&lt;!&ndash; res/layout/activity_settings.xml &ndash;&gt;-->
<!--<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"-->
<!--    xmlns:app="http://schemas.android.com/apk/res-auto"-->
<!--    xmlns:tools="http://schemas.android.com/tools"-->
<!--    android:layout_width="match_parent"-->
<!--    android:layout_height="match_parent"-->
<!--    android:paddingLeft="16dp"-->
<!--    android:paddingTop="16dp"-->
<!--    android:paddingRight="16dp"-->
<!--    android:paddingBottom="16dp"-->
<!--    tools:context=".SettingsActivity">-->

<!--    <TextView-->
<!--        android:id="@+id/textViewTitle"-->
<!--        android:layout_width="wrap_content"-->
<!--        android:layout_height="wrap_content"-->
<!--        android:text="@string/settings"-->
<!--        android:textSize="32sp"-->
<!--        android:textStyle="bold"-->
<!--        android:layout_marginBottom="@dimen/my_default_margin"-->
<!--        />-->

<!--    <LinearLayout-->
<!--        android:layout_width="match_parent"-->
<!--        android:layout_height="wrap_content"-->
<!--        android:layout_below="@id/textViewTitle"-->
<!--        android:orientation="vertical">-->

<!--        <LinearLayout-->
<!--            android:layout_width="match_parent"-->
<!--            android:layout_height="wrap_content"-->
<!--            android:layout_marginBottom="@dimen/my_default_margin"-->
<!--            android:gravity="center_vertical"-->
<!--            android:orientation="horizontal">-->

<!--            <ImageView-->
<!--                android:layout_width="@dimen/my_default_button_size"-->
<!--                android:layout_height="@dimen/my_default_button_size"-->
<!--                android:layout_marginEnd="@dimen/my_default_margin"-->
<!--                android:src="@drawable/ic_notifications"-->
<!--                app:tint="?attr/colorPrimary" />-->

<!--            <TextView-->
<!--                android:id="@+id/buttonNotifications"-->
<!--                android:layout_width="match_parent"-->
<!--                android:layout_height="wrap_content"-->
<!--                android:text="@string/notifications"-->
<!--                android:textSize="@dimen/my_default_category2_size" />-->
<!--        </LinearLayout>-->

<!--        <LinearLayout-->
<!--            android:layout_width="match_parent"-->
<!--            android:layout_height="wrap_content"-->
<!--            android:layout_marginBottom="@dimen/my_default_margin"-->
<!--            android:gravity="center_vertical"-->
<!--            android:orientation="horizontal">-->

<!--            <ImageView-->
<!--                android:layout_width="@dimen/my_default_button_size"-->
<!--                android:layout_height="@dimen/my_default_button_size"-->
<!--                android:layout_marginEnd="@dimen/my_default_margin"-->
<!--                android:src="@drawable/ic_preferences"-->
<!--                app:tint="?attr/colorPrimary" />-->

<!--            <TextView-->
<!--                android:id="@+id/buttonPreferences"-->
<!--                android:layout_width="match_parent"-->
<!--                android:layout_height="wrap_content"-->
<!--                android:text="@string/preferences"-->
<!--                android:textSize="@dimen/my_default_category2_size" />-->
<!--        </LinearLayout>-->

<!--        <LinearLayout-->
<!--            android:layout_width="match_parent"-->
<!--            android:layout_height="wrap_content"-->
<!--            android:layout_marginBottom="@dimen/my_default_margin"-->
<!--            android:gravity="center_vertical"-->
<!--            android:orientation="horizontal">-->

<!--            <ImageView-->
<!--                android:layout_width="@dimen/my_default_button_size"-->
<!--                android:layout_height="@dimen/my_default_button_size"-->
<!--                android:layout_marginEnd="@dimen/my_default_margin"-->
<!--                android:src="@drawable/ic_contact"-->
<!--                app:tint="?attr/colorPrimary" />-->

<!--            <TextView-->
<!--                android:id="@+id/buttonContact"-->
<!--                android:layout_width="match_parent"-->
<!--                android:layout_height="wrap_content"-->
<!--                android:text="@string/contact"-->
<!--                android:textSize="@dimen/my_default_category2_size" />-->
<!--        </LinearLayout>-->

<!--        <LinearLayout-->
<!--            android:layout_width="match_parent"-->
<!--            android:layout_height="wrap_content"-->
<!--            android:layout_marginBottom="@dimen/my_default_margin"-->
<!--            android:gravity="center_vertical"-->
<!--            android:orientation="horizontal">-->

<!--            <ImageView-->
<!--                android:layout_width="@dimen/my_default_button_size"-->
<!--                android:layout_height="@dimen/my_default_button_size"-->
<!--                android:layout_marginEnd="@dimen/my_default_margin"-->
<!--                android:src="@drawable/ic_contribute"-->
<!--                app:tint="?attr/colorPrimary" />-->

<!--            <TextView-->
<!--                android:id="@+id/buttonContribute"-->
<!--                android:layout_width="match_parent"-->
<!--                android:layout_height="wrap_content"-->
<!--                android:text="@string/contribute"-->
<!--                android:textSize="@dimen/my_default_category2_size" />-->
<!--        </LinearLayout>-->

<!--        <LinearLayout-->
<!--            android:layout_width="match_parent"-->
<!--            android:layout_height="wrap_content"-->
<!--            android:layout_marginBottom="@dimen/my_default_margin"-->
<!--            android:gravity="center_vertical"-->
<!--            android:orientation="horizontal">-->

<!--            <ImageView-->
<!--                android:layout_width="@dimen/my_default_button_size"-->
<!--                android:layout_height="@dimen/my_default_button_size"-->
<!--                android:layout_marginEnd="@dimen/my_default_margin"-->
<!--                android:src="@drawable/ic_about"-->
<!--                app:tint="?attr/colorPrimary" />-->

<!--            <TextView-->
<!--                android:id="@+id/buttonAbout"-->
<!--                android:layout_width="match_parent"-->
<!--                android:layout_height="wrap_content"-->
<!--                android:text="@string/about"-->
<!--                android:textSize="@dimen/my_default_category2_size" />-->
<!--        </LinearLayout>-->
<!--        <LinearLayout-->
<!--            android:layout_width="match_parent"-->
<!--            android:layout_height="wrap_content"-->
<!--            android:layout_marginBottom="@dimen/my_default_margin"-->
<!--            android:gravity="center_vertical"-->
<!--            android:orientation="horizontal">-->

<!--            <ImageView-->
<!--                android:layout_width="@dimen/my_default_button_size"-->
<!--                android:layout_height="@dimen/my_default_button_size"-->
<!--                android:layout_marginEnd="@dimen/my_default_margin"-->
<!--                android:src="@drawable/ic_language"-->
<!--                app:tint="?attr/colorPrimary" />-->

<!--            <TextView-->
<!--                android:id="@+id/language"-->
<!--                android:layout_width="match_parent"-->
<!--                android:layout_height="wrap_content"-->
<!--                android:text="@string/language"-->
<!--                android:textSize="@dimen/my_default_category2_size" />-->
<!--        </LinearLayout>-->
<!--    </LinearLayout>-->


<!--    &lt;!&ndash; Repeat similar structure for other buttons with different icons &ndash;&gt;-->

<!--</RelativeLayout>-->

*/

