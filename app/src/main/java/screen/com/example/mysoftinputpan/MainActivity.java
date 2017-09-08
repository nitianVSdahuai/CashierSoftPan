package screen.com.example.mysoftinputpan;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import screen.com.example.mysoftinputpan.View.KeyBoardViewUtil;
import screen.com.example.mysoftinputpan.dialog.TestAdjustDialog;


public class MainActivity extends AppCompatActivity {

    private LinearLayout root_view;
    private EditText normal_ed;
    private EditText special_ed;

    KeyBoardViewUtil keyBoardViewUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        root_view = (LinearLayout) findViewById(R.id.root_view);
        normal_ed = (EditText) findViewById(R.id.normal_ed);
        special_ed = (EditText) findViewById(R.id.special_ed);
        initMoveKeyBoard();
    }

    private void initMoveKeyBoard() {
        keyBoardViewUtil = new KeyBoardViewUtil(this, root_view);
        keyBoardViewUtil.showKeyBoardLayout(normal_ed, KeyBoardViewUtil.KEY_OF_NUM_TYPE_ABC);
    }

    public void blank(View view) {
        keyBoardViewUtil.showKeyBoardLayout(normal_ed, KeyBoardViewUtil.KEY_OF_NUM_TYPE_BLANK);
    }

    public void point(View view) {
        keyBoardViewUtil.showKeyBoardLayout(normal_ed, KeyBoardViewUtil.KEY_OF_NUM_TYPE_POINT);
    }

    public void num2abc(View view) {
        keyBoardViewUtil.showKeyBoardLayout(normal_ed, KeyBoardViewUtil.KEY_OF_NUM_TYPE_ABC);
    }

    public void delete(View view) {
        keyBoardViewUtil.showKeyBoardLayout(normal_ed, KeyBoardViewUtil.KEY_OF_NUM_TYPE_DELETE);
    }

    public void abc(View view) {
        keyBoardViewUtil.showKeyBoardLayout(normal_ed, KeyBoardViewUtil.KEY_OF_ABC);
    }

    public void showDialog(View view) {
        TestAdjustDialog testAdjustDialog = new TestAdjustDialog(this);
        testAdjustDialog.show();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (keyBoardViewUtil.isShow) {
                keyBoardViewUtil.hideAllKeyBoard();
            } else {
                return super.onKeyDown(keyCode, event);
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
}
