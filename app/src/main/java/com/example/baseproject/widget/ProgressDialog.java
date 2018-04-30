package com.example.baseproject.widget;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.baseproject.R;

import java.text.NumberFormat;

/**
 * 自定义ProgressDialog
 * @author Administrator
 *
 */
public class ProgressDialog extends android.app.Dialog {
	
	private NumberFormat mProgressPercentFormat;
    private ProgressBar mProgress;
    private TextView mMessageView;
    private Context context;
    private CharSequence mMessage="";
    public ProgressDialog(Context context) {
        super(context);
        this.context=context;
        initFormats();
    }

    public ProgressDialog(Context context, int theme) {
        super(context, theme);
        this.context=context;
        initFormats();
    }

    private void initFormats() {
    	
        mProgressPercentFormat = NumberFormat.getPercentInstance();
        mProgressPercentFormat.setMaximumFractionDigits(0);
    }
    
    public static ProgressDialog show(Context context, CharSequence title,
                                      CharSequence message) {
        return show(context, title, message, false);
    }

    public static ProgressDialog show(Context context, CharSequence title,
                                      CharSequence message, boolean indeterminate) {
        return show(context, title, message, indeterminate, false, null);
    }

    public static ProgressDialog show(Context context, CharSequence title,
                                      CharSequence message, boolean indeterminate, boolean cancelable) {
        return show(context, title, message, indeterminate, cancelable, null);
    }

    public static ProgressDialog show(Context context, CharSequence title,
                                      CharSequence message, boolean indeterminate,
                                      boolean cancelable, OnCancelListener cancelListener) {
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(indeterminate);
        dialog.setCancelable(cancelable);
        dialog.setOnCancelListener(cancelListener);
        dialog.show();
        return dialog;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	View view = View.inflate(context, R.layout.progress_dialog, null);
    	mProgress = view.findViewById(R.id.progressBar);
    	mMessageView = view.findViewById(R.id.message);
    	setContentView(view);
    	if (mMessage!=null) {
			setMessage(mMessage);
		}
        super.onCreate(savedInstanceState);
    }
    public void setIndeterminate(boolean indeterminate) {
        if (mProgress != null) {
            mProgress.setIndeterminate(indeterminate);
        }
    }
    
    public void setMessage(CharSequence message) {
        if (mProgress != null) {
        	mMessageView.setText(message);
        }  else {
            mMessage = message;
        }
    }
    
}
