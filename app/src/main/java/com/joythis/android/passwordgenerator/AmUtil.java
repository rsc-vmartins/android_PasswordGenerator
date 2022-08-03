package com.joythis.android.passwordgenerator;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

public class AmUtil {
    private Activity mA;

    public AmUtil(Activity pA){
        this.mA = pA;
    }//AmUtil

    public void fb  (String pStrMsg){
        Toast t = Toast.makeText(
        this.mA,
            pStrMsg,
            Toast.LENGTH_SHORT
        );

        t.setGravity(Gravity.CENTER, 0, 0);

        t.show();
    }//fb

    //tools related to randomness
    public static int randomInt(int pMin, int pMax){
        Random r = new Random();
        int iAmplitude = pMax-pMin+1;
        int iJump = r.nextInt(iAmplitude);
        int iDestination = pMin + iJump;
        return iDestination;
    }//randomInt

    public static char random_az(){
        int code_a = (int)'a';
        int code_z = (int)'z';
        int codeRandom = randomInt(code_a, code_z);
        char letterRandom = (char)codeRandom;
        return letterRandom;
    }//random_az

    public static char random_AZ(){
        int code_A = (int)'A';
        int code_Z = (int)'Z';
        int codeRandom = randomInt(code_A, code_Z);
        char letterRandom = (char)codeRandom;
        return letterRandom;
    }//random_AZ

    public static char random_09(){
        int code_0 = (int)'0';
        int code_9 = (int)'9';
        int codeRandom = randomInt(code_0, code_9);
        char letterRandom = (char)codeRandom;
        return letterRandom;
    }//random_09

    public static char randomSymbol(char pS1, char pS2){
        int code_s1 = (int)pS1;
        int code_s2 = (int)pS2;
        int codeRandom = randomInt(code_s1, code_s2);
        char letterRandom = (char)codeRandom;
        return letterRandom;
    }//randomSymbol

    public static char rAZ(){return randomSymbol('A', 'Z');}
    public static char raz(){return randomSymbol('a', 'z');}
    public static char r09(){return randomSymbol('0', '9');}

    /*
    ["*", "?", ":"]
     */
    public static char randomSpecialSymbol(
        ArrayList<String> pAlAcceptableSpecialSymbols
    ){
        int iHowMany = pAlAcceptableSpecialSymbols.size();
        int iRandomAddress = randomInt(0, iHowMany-1);
        String strRandomSymbol =
                pAlAcceptableSpecialSymbols.get(iRandomAddress);
        return strRandomSymbol.charAt(0);
    }//randomSpecialSymbol

    public static char randomSpecialSymbol(
        String[] paAcceptableSymbols
    ){
        int iHowMany = paAcceptableSymbols.length;
        int iRandomAddress = randomInt(0, iHowMany-1);
        String strRandomSymbol =
            paAcceptableSymbols[iRandomAddress];
        return strRandomSymbol.charAt(0);
    }//randomSpecialSymbol

    public static String genPassword(
        int pPassLength,
        boolean pb_az,
        boolean pb_AZ,
        boolean pb_09,
        String[] paSS //array of special symbols
    ){
        String strPassword = "";
        boolean bPasswordReady = false;
        char cRandomSymbol;
        boolean bAllTypesRejected =
            !pb_az && !pb_AZ && !pb_09 && paSS.length==0;
        if (!bAllTypesRejected){
            while(!bPasswordReady){
                int iType = randomInt(1, 4);
                cRandomSymbol = 'x';
                switch(iType){
                    case 1: //az
                        cRandomSymbol = random_az();
                        break;
                    case 2: //AZ
                        cRandomSymbol = random_AZ();
                        break;
                    case 3: //09
                        cRandomSymbol = random_09();
                        break;
                    case 4: //SS
                        if (paSS.length>0) {
                            int index = randomInt(0, paSS.length - 1);
                            cRandomSymbol = paSS[index].charAt(0);
                        }//if
                        break;
                }//switch
                //cRandomSymbol
                if (pb_az && iType==1) strPassword+=cRandomSymbol;
                if (pb_AZ && iType==2) strPassword+=cRandomSymbol;
                if (pb_09 && iType==3) strPassword+=cRandomSymbol;
                if (paSS.length>0 && iType==4) strPassword+=cRandomSymbol;

                if (strPassword.length()==pPassLength)
                    bPasswordReady=true;
            }//while
        }//if not everything rejected
        else{
            return "empty password";
        }

        return strPassword;
    }//genPassword

    public boolean writeToFileInPIS(
        String pStrFileName,
        String pStrContent,
        int piMode
    ){
        try{
            FileOutputStream fos = this.mA.openFileOutput(
                    pStrFileName,
                    piMode
            );
            OutputStreamWriter osw =
                new OutputStreamWriter(
                    fos,
                    StandardCharsets.UTF_8
                );
            osw.write(pStrContent);
            osw.close();
            fos.close();
            return true;
        }//try
        catch(Exception e){
            Log.e("@AmUtil", e.toString());
            return false;
        }//catch
    }//writeToFileInPIS

    public String readFromFileInPIS(String pStrFileName){
        String strAll = "";
        try{
            FileInputStream fis = this.mA.openFileInput(pStrFileName);
            InputStreamReader isr = new InputStreamReader(
                fis,
                StandardCharsets.UTF_8
            );
            int i; char c;
            while((i=isr.read())!=-1){
                c = (char)i;
                strAll += c;
            }//while
            isr.close();
            fis.close();
            return strAll;
        }//try
        catch(Exception e){
            Log.e("@AmUtil", e.toString());
            return ""; //empty content
        }
    }//readFromFileInPIS

    public boolean resetFileInPIS(
        String pStrFileName
    ){
        try{
            FileOutputStream fos = this.mA.openFileOutput(
                pStrFileName,
                this.mA.MODE_PRIVATE
            );
            OutputStreamWriter osw = new OutputStreamWriter(
                fos,
                StandardCharsets.UTF_8
            );
            osw.write("");
            osw.close();
            fos.close();
            return true;
        }//try
        catch(Exception e){
            return false;
        }//catch
    }//resetFileInPIS
}//AmUtil