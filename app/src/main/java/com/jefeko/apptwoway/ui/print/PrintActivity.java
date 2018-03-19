package com.jefeko.apptwoway.ui.print;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bxl.BXLConst;
import com.bxl.config.editor.BXLConfigLoader;
import com.jefeko.apptwoway.R;
import com.jefeko.apptwoway.ui.common.BaseActivity;
import com.jefeko.apptwoway.utils.LogUtils;

import java.util.ArrayList;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import jpos.JposConst;
import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;
import jpos.config.JposEntry;
import jpos.events.ErrorEvent;
import jpos.events.ErrorListener;
import jpos.events.OutputCompleteEvent;
import jpos.events.OutputCompleteListener;
import jpos.events.StatusUpdateEvent;
import jpos.events.StatusUpdateListener;

public class PrintActivity extends BaseActivity implements View.OnClickListener, StatusUpdateListener, OutputCompleteListener, ErrorListener {
    private static final int REQUEST_CODE_BLUETOOTH = 1;

    private static final String DEVICE_ADDRESS_START = " (";
    private static final String DEVICE_ADDRESS_END = ")";

    private final ArrayList<CharSequence> bondedDevices = new ArrayList<>();

    private BXLConfigLoader bxlConfigLoader;
    private POSPrinter posPrinter;
    private String logicalName;

    @BindView(R.id.print_btn) Button print_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_print);
        ButterKnife.bind(this);
        mContext = this;

        setToolbar();


        setBondedDevices();

        bxlConfigLoader = new BXLConfigLoader(PrintActivity.this);
        try {
            bxlConfigLoader.openFile();
        } catch (Exception e) {
            e.printStackTrace();
            bxlConfigLoader.newFile();
        }
        posPrinter = new POSPrinter(PrintActivity.this);

        posPrinter.addErrorListener(PrintActivity.this);
        posPrinter.addOutputCompleteListener(PrintActivity.this);
        posPrinter.addStatusUpdateListener(PrintActivity.this);



        print_btn.setOnClickListener(this);


        String device = bondedDevices.get(0).toString();

        String name = device.substring(0, device.indexOf(DEVICE_ADDRESS_START));

        String address = device.substring(device.indexOf(DEVICE_ADDRESS_START)+ DEVICE_ADDRESS_START.length(),device.indexOf(DEVICE_ADDRESS_END));

        try {
            for (Object entry : bxlConfigLoader.getEntries()) {
                JposEntry jposEntry = (JposEntry) entry;
                bxlConfigLoader.removeEntry(jposEntry.getLogicalName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            logicalName = setProductName(name);
            bxlConfigLoader.addEntry(logicalName, BXLConfigLoader.DEVICE_CATEGORY_POS_PRINTER, logicalName, BXLConfigLoader.DEVICE_BUS_BLUETOOTH, address);

            bxlConfigLoader.saveFile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.print_btn:
                print();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            posPrinter.close();
        } catch (JposException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_BLUETOOTH) {
            setBondedDevices();
        }
    }

    private String setProductName(String name){
        String productName = BXLConfigLoader.PRODUCT_NAME_SPP_R200II;

        if((name.indexOf("SPP-R200II")>=0)){
            if(name.length() > 10){
                if(name.substring(10, 11).equals("I")){
                    productName = BXLConfigLoader.PRODUCT_NAME_SPP_R200III;
                }
            }
        }else if((name.indexOf("SPP-R210")>=0)){
            productName = BXLConfigLoader.PRODUCT_NAME_SPP_R210;
        }else if((name.indexOf("SPP-R310")>=0)){
            productName = BXLConfigLoader.PRODUCT_NAME_SPP_R310;
        }else if((name.indexOf("SPP-R300")>=0)){
            productName = BXLConfigLoader.PRODUCT_NAME_SPP_R300;
        }else if((name.indexOf("SPP-R400")>=0)){
            productName = BXLConfigLoader.PRODUCT_NAME_SPP_R400;
        }

        return productName;
    }

    private void setBondedDevices() {
        logicalName = null;
        bondedDevices.clear();

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        Set<BluetoothDevice> bondedDeviceSet = bluetoothAdapter
                .getBondedDevices();

        for (BluetoothDevice device : bondedDeviceSet) {
            bondedDevices.add(device.getName() + DEVICE_ADDRESS_START + device.getAddress() + DEVICE_ADDRESS_END);
        }
    }

    private void print() {

        LogUtils.d("============================");

        try {
            posPrinter.open(logicalName);
            posPrinter.claim(0);
            posPrinter.setDeviceEnabled(true);

            posPrinter.setCharacterEncoding(BXLConst.CS_KS5601);
            String ESC = new String(new byte[]{0x1b, 0x7c});
            String LF = "\n";
            String jump1 = "    ";
            String jump2 = "             ";

            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT,  ESC + "!uC" + ESC + "cA" + ESC + "4C" + ESC + "bC" + "[영수증]"+ LF);
            //posPrinter.printBarCode(POSPrinterConst.PTR_S_RECEIPT, "263036991401;4tk", POSPrinterConst.PTR_BCS_QRCODE, 8, 8, POSPrinterConst.PTR_BC_CENTER, POSPrinterConst.PTR_BC_TEXT_BELOW);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  +  LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[매장명] " + "넷포스(테스트용)"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[사업자] " + "305-32-73321"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[주소] " + "대전광역시 유성구 장대로74번길 17(장대동) 1층"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[대표자] " + "김유수  [TEL] 042-623-9068"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[매출일] " + "2018-03-07 09:29:23"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[판매원] " + "김유수"+ LF);
//            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[영수증] " + "20180307-01-0002"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "================================"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "  상 품 명    단가   수량    금액"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "--------------------------------"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + " 소주"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + " 000015"+jump1+"3,500"+jump1+"1"+jump1+"3,000"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "--------------------------------"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "합 계 금 액"+jump2+"27,500"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "--------------------------------"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "받 을 금 액"+jump2+"27,500"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "받 은 금 액"+jump2+"27,500"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "--------------------------------"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + ""+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "카       드"+jump2+"27,500"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "현       금"+jump2+"27,500"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "--------------------------------"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT,  ESC + "!uC" + ESC + "cA" + ESC + "bC" + "[구매자정보]"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[매장명] " + "넷포스(테스트용)"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[사업자] " + "305-32-73321"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[주소] " + "대전광역시 유성구 장대로74번길 17(장대동) 1층"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, ESC + "N" + ESC + "lA" + ESC + "bC"  + "[대표자] " + "김유수  [TEL] 042-623-9068"+ LF);
            posPrinter.printNormal(POSPrinterConst.PTR_S_RECEIPT, "\n");
        } catch (JposException e) {
            e.printStackTrace();
            Toast.makeText(PrintActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally {
            try {
                posPrinter.close();
            } catch (JposException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void outputCompleteOccurred(final OutputCompleteEvent e) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(PrintActivity.this, "complete print", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void errorOccurred(final ErrorEvent arg0) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                Toast.makeText(PrintActivity.this, "Error status : " + getERMessage(arg0.getErrorCodeExtended()), Toast.LENGTH_SHORT).show();

                if(getERMessage(arg0.getErrorCodeExtended()).equals("Power off")){
                    try
                    {
                        posPrinter.close();
                    }
                    catch(JposException e)
                    {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    // port-close
                }else if(getERMessage(arg0.getErrorCodeExtended()).equals("Cover open")){
                    //re-print
                }else if(getERMessage(arg0.getErrorCodeExtended()).equals("Paper empty")){
                    //re-print
                }


            }
        });
    }

    @Override
    public void statusUpdateOccurred(final StatusUpdateEvent arg0) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(PrintActivity.this, "printer status : " + getSUEMessage(arg0.getStatus()), Toast.LENGTH_SHORT).show();

                if(getSUEMessage(arg0.getStatus()).equals("Power off")){
                    Toast.makeText(PrintActivity.this, "check the printer - Power off", Toast.LENGTH_SHORT).show();
                }else if(getSUEMessage(arg0.getStatus()).equals("Cover Open")){
                    //display message
                    Toast.makeText(PrintActivity.this, "check the printer - Cover Open", Toast.LENGTH_SHORT).show();
                }else if(getSUEMessage(arg0.getStatus()).equals("Cover OK")){
                    //re-print
                }else if(getSUEMessage(arg0.getStatus()).equals("Receipt Paper Empty")){
                    //display message
                    Toast.makeText(PrintActivity.this, "check the printer - Receipt Paper Empty", Toast.LENGTH_SHORT).show();
                }else if(getSUEMessage(arg0.getStatus()).equals("Receipt Paper OK")){
                    //re-print
                }
            }
        });
    }

    private static String getERMessage(int status){
        switch(status){
            case POSPrinterConst.JPOS_EPTR_COVER_OPEN:
                return "Cover open";

            case POSPrinterConst.JPOS_EPTR_REC_EMPTY:
                return "Paper empty";

            case JposConst.JPOS_SUE_POWER_OFF_OFFLINE:
                return "Power off";

            default:
                return "Unknown";
        }
    }


    private static String getSUEMessage(int status){
        switch(status){
            case JposConst.JPOS_SUE_POWER_ONLINE:
                return "Power on";

            case JposConst.JPOS_SUE_POWER_OFF_OFFLINE:
                return "Power off";

            case POSPrinterConst.PTR_SUE_COVER_OPEN:
                return "Cover Open";

            case POSPrinterConst.PTR_SUE_COVER_OK:
                return "Cover OK";

            case POSPrinterConst.PTR_SUE_REC_EMPTY:
                return "Receipt Paper Empty";

            case POSPrinterConst.PTR_SUE_REC_NEAREMPTY:
                return "Receipt Paper Near Empty";

            case POSPrinterConst.PTR_SUE_REC_PAPEROK:
                return "Receipt Paper OK";

            case POSPrinterConst.PTR_SUE_IDLE:
                return "Printer Idle";

            default:
                return "Unknown";
        }
    }

}
