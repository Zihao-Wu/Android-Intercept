package com.wzh.androidintercept.common;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.wzh.androidintercept.R;
import com.wzh.androidintercept.databinding.LayoutCommonDialogBinding;


/**
 * @author faqi.tao
 * @time 2019/10/18
 */
public class CommonDialog extends Dialog {

    private CommonDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    public static class Builder {
        private TextView titleTv;
        private TextView contentTv;//内容
        private TextView cancelBtn;//取消按钮
        private TextView confirmBtn;//确认按钮

        private LayoutCommonDialogBinding binding;
        private CommonDialog commonDialog;
        private View.OnClickListener mConfirmClickListener;
        private View.OnClickListener mCancelClickListener;

        public Builder(Context context) {
            commonDialog = new CommonDialog(context, R.style.Theme_AppCompat_Dialog);
            binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_common_dialog, null, false);
            titleTv = binding.titleTv;
            contentTv = binding.contentTv;
            cancelBtn = binding.cancelTv;
            confirmBtn = binding.confirmTv;

            commonDialog.addContentView(binding.getRoot(), new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        public Builder setTitleTv(String str) {
            if (!TextUtils.isEmpty(str)) {
                titleTv.setVisibility(View.VISIBLE);
                titleTv.setText(str);
            }
            return this;
        }

        public Builder setContent(CharSequence str) {
            contentTv.setText(str);
            return this;
        }

        /**
         * 设置Content文字颜色
         *
         * @param colorsId 资源文件下颜色值
         */
        public Builder setContentTextColor(int colorsId) {
            contentTv.setTextColor(contentTv.getContext().getResources().getColor(colorsId));
            return this;
        }

        public Builder setCancel(String str) {
            cancelBtn.setText(str);
            return this;
        }

        /**
         * 设置取消按钮的颜色值
         *
         * @param colorsId 资源文件下颜色值
         */
        public Builder setCancelTextColor(int colorsId) {
            cancelBtn.setTextColor(cancelBtn.getContext().getResources().getColor(colorsId));
            return this;
        }

        public Builder setCancel(String str, View.OnClickListener onCancelClickListener) {
            cancelBtn.setText(str);
            mCancelClickListener = onCancelClickListener;
            return this;
        }

        /**
         * 设置确认按钮的颜色值
         *
         * @param colorsId 资源文件下颜色值
         */
        public Builder setConfirmTextColor(int colorsId) {
            confirmBtn.setTextColor(confirmBtn.getContext().getResources().getColor(colorsId));
            return this;
        }

        public Builder setConfirm(String str) {
            confirmBtn.setText(str);
            return this;
        }

        public Builder setConfirm(String str, View.OnClickListener onConfirmClickListener) {
            confirmBtn.setText(str);
            mConfirmClickListener = onConfirmClickListener;
            return this;
        }

        public CommonDialog create() {
            confirmBtn.setOnClickListener(v -> {
                commonDialog.dismiss();
                if (mConfirmClickListener != null) {
                    mConfirmClickListener.onClick(v);
                }
            });

            cancelBtn.setOnClickListener(v -> {
                commonDialog.dismiss();
                if (mCancelClickListener != null) {
                    mCancelClickListener.onClick(v);
                }
            });

            commonDialog.getWindow().getDecorView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int width = commonDialog.getWindow().getDecorView().getMeasuredWidth();
                    commonDialog.getWindow().setLayout((int) (width * 0.8), WindowManager.LayoutParams.WRAP_CONTENT);
                    commonDialog.getWindow().getDecorView().getViewTreeObserver().removeOnPreDrawListener(this);
                    return false;
                }
            });

            commonDialog.setCancelable(true);
            commonDialog.setCanceledOnTouchOutside(true);
            commonDialog.getWindow().setBackgroundDrawableResource(R.drawable.white_radius_14);
            commonDialog.setContentView(binding.getRoot());
            return commonDialog;
        }
    }
}