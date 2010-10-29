package com.lxy.lbsc;

import java.util.ArrayList;

import android.content.Context; 
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


class QuestionlistAdapter extends ArrayAdapter<QuestionModel> {
	Context mContext; 
	ArrayList<QuestionModel> mList; 
	
	QuestionlistAdapter(Context context, ArrayList<QuestionModel> list) {
		super(context, R.layout.questionrow, list);
		mContext = context; 
		mList = list; 
	}		
	
	@Override
	public boolean isEnabled(int position) {
		boolean enabled = true; 			
		QuestionModel model=mList.get(position); //getModel(position);
		if(model.question_id==0){
			enabled = false; 
		}
		return enabled; 
	}		
	@Override
	public boolean areAllItemsEnabled(){
		return false; 
	}		
	public View getView(int position, View convertView, ViewGroup parent) {
		View row=convertView;
		QuestionModel model=mList.get(position); //getModel(position);
		boolean isHeader = false; 
		if(model.question_id==0){
			isHeader = true; 
		}			
		if (row==null) {				
			LayoutInflater inflater=LayoutInflater.from(mContext);
			row=inflater.inflate(R.layout.questionrow, parent, false);
			row.setTag(R.id.icon_place2, row.findViewById(R.id.icon_place2));
			row.setTag(R.id.question_name, row.findViewById(R.id.question_name));
			row.setTag(R.id.question_number_of_answers, row.findViewById(R.id.question_number_of_answers));
			row.setTag(R.id.question_number_of_votes, row.findViewById(R.id.question_number_of_votes));
		}
		ImageView img = (ImageView)row.getTag(R.id.icon_place2); 
		TextView label1=(TextView)row.getTag(R.id.question_name);
		TextView label2=(TextView)row.getTag(R.id.question_number_of_answers);	
		TextView label3=(TextView)row.getTag(R.id.question_number_of_votes);	
		Log.i(TAG, "Ok in get/set name label. Position="+position+"Name="+model.toString());
		if(isHeader){
			img.setVisibility(View.VISIBLE);
			label1.setText(model.place);
			label1.setTextColor(0xFF5D1670); 
			label2.setVisibility(View.INVISIBLE); 
			label3.setVisibility(View.INVISIBLE);				 
		}else{
			label2.setText(String.format(mContext.getResources().getString(R.string.answers_count), model.answered_count));
			label3.setText(String.format(mContext.getResources().getString(R.string.votes_count), model.vote_count));
			img.setVisibility(View.INVISIBLE);
			label1.setText(model.question);
			label2.setVisibility(View.VISIBLE); 
			label3.setVisibility(View.VISIBLE);
			if (model.answered_count==0){
				label2.setTextColor(0xFFFFFFFF);
				label2.setBackgroundColor(0xFF5D1670); 
			}
		}
		return(row);			
	}
	
	private static final String TAG = "LBSC";
}
