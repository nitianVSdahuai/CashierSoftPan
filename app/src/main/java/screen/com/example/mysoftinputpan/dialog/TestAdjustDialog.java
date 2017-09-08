package screen.com.example.mysoftinputpan.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.zhy.autolayout.utils.AutoUtils;

import screen.com.example.mysoftinputpan.R;
import screen.com.example.mysoftinputpan.View.KeyBoardViewUtil;

/**
 * Created by Arrow on 2017/8/7.
 */

public class TestAdjustDialog extends Dialog {

    private Context mContext;
    private KeyBoardViewUtil keyBoardViewUtil;

    public TestAdjustDialog(@NonNull Context context) {
        super(context, R.style.AdjustDialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        initMoveKeyBoard();
    }

    private void initMoveKeyBoard() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.dialog_adjust, null);
        LinearLayout root_view = (LinearLayout) view.findViewById(R.id.ll_rootView);
        EditText et_test = (EditText) view.findViewById(R.id.et_test);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height = AutoUtils.getPercentHeightSize(1000);
        dialogWindow.setAttributes(lp);

        setContentView(view);
        keyBoardViewUtil = new KeyBoardViewUtil(mContext, root_view);
        keyBoardViewUtil.showKeyBoardLayout(et_test, KeyBoardViewUtil.KEY_OF_NUM_TYPE_ABC);
    }
}
