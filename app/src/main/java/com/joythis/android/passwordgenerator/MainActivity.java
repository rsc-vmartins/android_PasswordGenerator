package com.joythis.android.passwordgenerator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    //data members
    CheckBox mCbAZ, mCbaz, mCbDigits;
    EditText mEtSS; //for the Special Symbols
    SeekBar mSbLength; //for the password length / size
    Button mBtnGenPass;
    ListView mLvPasswords;
    ArrayList<String> mAlPasswords;
    ArrayAdapter<String> mAd;

    Context mContext;
    AmUtil mUtil;

    public final static String PASSWORDS_FILE = "PASS3.TXT";

    void writePasswordsToPIS(){
        String strAllPasswords="";
        for (String p : mAlPasswords)
            strAllPasswords=p+"\n"+strAllPasswords;
        this.mUtil.writeToFileInPIS(
            PASSWORDS_FILE,
            strAllPasswords,
            MODE_PRIVATE
        );
    }//writePasswordsToPIS

    void readPasswordsFromPIS(){
        String strPasswords =
            this.mUtil.readFromFileInPIS(PASSWORDS_FILE);
        if (!strPasswords.isEmpty()){
            String[] ps = strPasswords.split("\n");
            mAlPasswords.clear();
            for (String p : ps){
                mAlPasswords.add(0, p);
            }//for
            mAd.notifyDataSetChanged();
        }//if
    }//readPasswordsFromPIS

    SeekBar.OnSeekBarChangeListener mSbHandler =
        new SeekBar.OnSeekBarChangeListener() {
            //auto-called whenever the progress value changes
            //idea: put a new TextView in the layout to display this value
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //TODO
            }

            //auto-called when the "progress drag" starts
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            //auto-called when the user frees the progress handler
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int iProgress = mSbLength.getProgress();
                String strProgress = String.valueOf(iProgress);
                String strMsg =
                    "Length "+strProgress+" picked.";
                mUtil.fb(strMsg);
            }
        };//mSbHandler

    View.OnClickListener mClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String[] aParts={};
            switch(v.getId()){
                case R.id.idBtnGenPass:
                    String strUser = mEtSS.getText().toString().trim();
                    if (!strUser.isEmpty()){
                       aParts = strUser.split(" ");
                    }//if
                    String strPassword = AmUtil.genPassword(
                        mSbLength.getProgress(),
                        mCbaz.isChecked(),
                        mCbAZ.isChecked(),
                        mCbDigits.isChecked(),
                        aParts
                    );
                    mAlPasswords.add(0, strPassword);
                    mAd.notifyDataSetChanged();

                    //new pass, new write
                    writePasswordsToPIS();
                    break;
            }//switch
        }//onClick
    };//mClickHandler

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rl_pass_gen_v1);

        init(savedInstanceState);
    }//onCreate

    void init (Bundle pB){
        //bindings
        mContext = this;
        //mContext = MainActivity.this;
        mUtil = new AmUtil(this);

        mCbaz = findViewById(R.id.idCbaz);
        mCbAZ = findViewById(R.id.idCbAZ);
        mCbDigits = findViewById(R.id.idCbDigits);
        mEtSS = findViewById(R.id.idEtSS);
        mSbLength = findViewById(R.id.idSbLength);
        mBtnGenPass = findViewById(R.id.idBtnGenPass);
        mLvPasswords = findViewById(R.id.idLvPasswords);

        if (mLvPasswords!=null){
            //data source + adapter
            mAlPasswords = new ArrayList<>();
            mAd = new ArrayAdapter<>(
                mContext,
                android.R.layout.simple_list_item_1,
                mAlPasswords
            );
            mLvPasswords.setAdapter(mAd);

            readPasswordsFromPIS();
        }//if (ListView pattern)

        //behaviors
        mBtnGenPass.setOnClickListener(mClickHandler);
        //testing();
        mSbLength.setOnSeekBarChangeListener(mSbHandler);


    }//init

    void testing(){
        String strTest_az = "";
        String strTest_AZ = "";
        String strTest_Digits = "";
        String strTest_SS = "";
        //" exemplo ".trim() --> "exemplo"
        String strUserSS = mEtSS.getText().toString().trim();
        //"A B C".split(" ") ---> ["A", "B", "C"]
        String[] aUserSS = strUserSS.split(" ");
        for(int idx=0; idx<30; idx++) {
            strTest_az += AmUtil.random_az() + " ";
            strTest_AZ += AmUtil.random_AZ() + " ";
            strTest_Digits += AmUtil.random_09() + " ";
            strTest_SS += AmUtil.randomSpecialSymbol(
                aUserSS
            ) + " ";
        }//for
        mAlPasswords.add(strTest_az);
        mAlPasswords.add(strTest_AZ);
        mAlPasswords.add(strTest_Digits);
        mAlPasswords.add(strTest_SS);

        mAd.notifyDataSetChanged();
    }//testing

    /*
    @Override
    protected void onSaveInstanceState(
        @NonNull Bundle outState
    ) {
        super.onSaveInstanceState(outState);

        if (outState!=null){
            //ask what is to be preserved
            //the passwords (the data that are to be displayed in the ListView)
            //mAlPasswords //this is data member to be saved
            outState.putStringArrayList(
                "passwords",
                mAlPasswords
            );
            outState.putInt("year", 2021);
        }//if
    }//onSaveInstanceState

    @Override
    protected void onRestoreInstanceState(
        @NonNull Bundle savedInstanceState
    ){
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState!=null){
            boolean bKeyPasswordsExists =
                savedInstanceState.containsKey("passwords");
            boolean bKeyYearExists =
                savedInstanceState.containsKey("year");
            if (bKeyYearExists){
                int iRecoveredYear =
                    savedInstanceState.getInt("year");
                //comment on release-version
                mUtil.fb("Recoved year "+
                    String.valueOf(iRecoveredYear)
                );
            }
            if (bKeyPasswordsExists){
                ArrayList<String> alRec =
                //the reference / the address CHANGES
                //BAD IDEA => will break Adapters
                //mAlPasswords =
                    savedInstanceState.getStringArrayList(
                        "passwords"
                    );

                if (alRec!=null && alRec.size()>0){
                    //keeping the address
                    mAlPasswords.clear(); //cleans the content
                    for (String s : alRec){
                        mAlPasswords.add(s);
                    }//for
                    mAd.notifyDataSetChanged();
                }//if
            }//if the necessary keys are available
        }//if
    }//onRestoreInstanceState

     */

    /*
    #3
    binds the menu resource (XML) to the Activity (Java)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater minf = this.getMenuInflater();
        if (minf!=null){
            minf.inflate(
                R.menu.my_menu,
                menu
            );
        }//if

        return super.onCreateOptionsMenu(menu);
    }//onCreateOptionsMenu

    //#4
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.idMiResetFile:
                this.mUtil.fb("Resetting passwords");
                boolean bOK =
                    this.mUtil.resetFileInPIS(PASSWORDS_FILE);
                if (bOK){
                    mAlPasswords.clear();
                    mAd.notifyDataSetChanged();
                }
        }//switch
        return super.onOptionsItemSelected(item);
    }//onOptionsItemSelected
}//MainActivity