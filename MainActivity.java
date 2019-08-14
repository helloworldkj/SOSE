package com.example.sose_main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_aid;
            btn_aid=(Button)findViewById(R.id.aid);
        btn_aid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent_aid = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.응급센터.com/?n_media=27758&n_query=%EC%9D%91%EA%B8%89%EC%B2%98%EC%B9%98%EB%B2%95&n_rank=1&n_ad_group=grp-a001-01-000000011711579&n_ad=nad-a001-01-000000067963587&n_keyword_id=nkw-a001-01-000002152731754&n_keyword=%EC%9D%91%EA%B8%89%EC%B2%98%EC%B9%98%EB%B2%95&n_campaign_type=1"));
                    startActivity(intent_aid);

                }});

        Button set;
            set=(Button)findViewById(R.id.set);
        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_set = new Intent(MainActivity.this, setting.class);
                startActivity(intent_set);

            }
        });

    }
}
