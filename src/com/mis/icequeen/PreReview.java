package com.mis.icequeen;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class PreReview extends Activity {
	
	private ArrayList<Integer> showlist;
	private int[] cptrange;
	private TextView tvSelectedChapter;
	private TextView tempTV;
	private RatingBar ratingBar;
	private LinearLayout pendingVocLinearLayout;
	private Button btnConfirmReview;
	private Uri total;
	private Cursor test;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pre_review);
        Bundle extras = getIntent().getExtras();
        // 取出選擇的章節
        cptrange = extras.getIntArray("selected");
       
        showlist = new ArrayList<Integer>();
        tvSelectedChapter = (TextView) findViewById(R.id.tvSelectedChapter);
        RatingBarListener ratingBarListener = new RatingBarListener(); 
    	ratingBar = (RatingBar) findViewById(R.id.ratingBar);
    	ratingBar.setOnRatingBarChangeListener(ratingBarListener);
    	pendingVocLinearLayout = (LinearLayout) findViewById(R.id.pendingVocLinearLayout);
    	
    	ButtonListener buttonListener = new ButtonListener();
    	btnConfirmReview = (Button) findViewById(R.id.btnConfirmReview);
    	btnConfirmReview.setOnClickListener(buttonListener);
    	
        refreshPendingVoc( (int) ratingBar.getRating() );
        
        tvSelectedChapter.append("\n");
        for (int i = 0; i < cptrange.length; i++) {
    		if (cptrange[i] == 1) {
    			tvSelectedChapter.append(" Chapter "+(i+1)+",");
    		}
    	}
        tvSelectedChapter.setText(tvSelectedChapter.getText().toString().substring(0, tvSelectedChapter.getText().length()-1));
        
    }
    
    
    /**
     * 依照星等重新整理待複習單字
     * @param rating
     */
    private void refreshPendingVoc(int rating) {
    	
    	switch (rating) {
    	case 0:
    		tempTV = new TextView(this);
    		tempTV.setText("請選擇星星數量！");
    		pendingVocLinearLayout.removeAllViews();
    		pendingVocLinearLayout.addView(tempTV);
    		break;
    	case 1:
    		tempTV = new TextView(this);
    		tempTV.setText("一星等的單字：\n");
    		pendingVocLinearLayout.removeAllViews();
    		pendingVocLinearLayout.addView(tempTV);
    		break;
    	case 2:
    		tempTV = new TextView(this);
    		tempTV.setText("二星等的單字：\n");
    		pendingVocLinearLayout.removeAllViews();
    		pendingVocLinearLayout.addView(tempTV);
    		break;
    	case 3:
    		tempTV = new TextView(this);
    		tempTV.setText("三星等的單字：\n");
    		pendingVocLinearLayout.removeAllViews();
    		pendingVocLinearLayout.addView(tempTV);
    		break;
    	default:
    		break;
    	}
    	
    }
    
    /**
     * RatingBar 監聽 class
     */
    private class RatingBarListener implements OnRatingBarChangeListener {
    	// 在星等改變的時候呼叫 refreshPendingVoc
		public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
			refreshPendingVoc( (int) rating );
			
    		//query by R&C
			showlist.clear();
    		 for (int i = 0; i < cptrange.length; i++) {
    				if (cptrange[i] == 1) {
    					getIntent().setData(Uri.parse("content://com.mis.icequeen.testprovider/getVOCbyRC:"+(int) rating+":"+(i+1)));
    					total = getIntent().getData();
    					test = managedQuery(total, null, null, null, null);
    					System.out.println("voc count: "+test.getCount());
    					test.moveToFirst();
    					for (int j = 0; j < test.getCount(); j++) {
    						showlist.add(test.getInt(0));
    						tempTV.append("\n"+test.getString(1));
    						if (!test.isLast())
    							test.moveToNext();
    					}
    					
    					System.out.println("showsize: "+showlist.size());
    					test.close();
    				}
    			}
    		 
    		 if (showlist.size() == 0)
    			 tempTV.setText("查無單字！");
    		
		}
    }
    
    /**
     * 確認送出
     */
    private class ButtonListener implements OnClickListener {
		public void onClick(View arg0) {
			if(showlist.size()==0)
			{
				Toast.makeText(arg0.getContext(), "查無單字", Toast.LENGTH_SHORT).show();
			}else{
			Intent it = new Intent();
			Intent its = new Intent();
			its.setClass(PreReview.this, PreReview.class);
			it.setClass(PreReview.this, Review.class);
			it.putExtra("init", true);
			it.putExtra("index", 0);
			it.putExtra("selected", cptrange);
			its.putExtra("selected", cptrange);
			it.putIntegerArrayListExtra("showlist", showlist);
			it.putExtra("Rate",(int) ratingBar.getRating());
			finish();
			startActivity(its);
			startActivity(it);
			
			}
			
			
			
		}
    }

}
