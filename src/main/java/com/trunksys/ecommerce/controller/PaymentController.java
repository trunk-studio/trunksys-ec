package com.trunksys.ecommerce.controller;

import AllPay.Payment.Integration.*;
import com.trunksys.ecommerce.controller.Greeting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Value("${allpay.serviceURL}")
    private String serviceURL;

    @Value("${allpay.hashKey}")
    private String hashKey;

    @Value("${allpay.hashIV}")
    private String hashIV;

    @Value("${allpay.merchantID}")
    private String merchantID;

    @RequestMapping(value = "allpay", method=RequestMethod.GET)
    public @ResponseBody
    String allpay(@RequestParam(value="name", required=false, defaultValue="Stranger") String name) {

        StringBuilder szHtml = new StringBuilder();

        List<String> enErrors = new ArrayList<String>();

        try {
            AllInOne oPay = new AllInOne();

            oPay.ServiceMethod = HttpMethod.HttpPOST;
            oPay.ServiceURL = serviceURL;
            oPay.HashKey = hashKey;
            oPay.HashIV = hashIV;
            oPay.MerchantID = merchantID;
            
            /* 基本參數 */
            oPay.Send.ReturnURL = "http://localhost:9000/allpay/return";
            oPay.Send.ClientBackURL = "http://localhost:9000/allpay/clientBack";
            oPay.Send.OrderResultURL = "http://localhost:9000/allpay/orderResult";
            oPay.Send.MerchantTradeNo = "201500123";
            oPay.Send.MerchantTradeDate = new Date();
            oPay.Send.TotalAmount = new Decimal("100");
            oPay.Send.TradeDesc = "您該筆訂單的描述";
            oPay.Send.ChoosePayment = PaymentMethod.ALL;
            oPay.Send.Remark = "您要填寫的其他備註";
            oPay.Send.ChooseSubPayment = PaymentMethodItem.None;
            oPay.Send.NeedExtraPaidInfo = ExtraPaymentInfo.No;
            oPay.Send.DeviceSource = DeviceType.PC;
            oPay.Send.IgnorePayment = "Alipay#Tenpay";

            // 加入選購商品資料。
            Item a1 = new Item();
            a1.Name = "產品A";
            a1.Price = new Decimal("50");
            a1.Currency = "TWD";
            a1.Quantity = 1;
            a1.URL = "http://localhost:9000/product/a001";
            oPay.Send.Items.add(a1);

            Item a2 = new Item();
            a2.Name = "產品B";
            a2.Price = new Decimal("50");
            a2.Currency = "TWD";
            a2.Quantity = 1;
            a2.URL = "http://localhost:9000/product/a002";
            oPay.Send.Items.add(a2);

            /* 產生產生訂單 Html Code 的方法 */
            enErrors.addAll(oPay.CheckOutString(szHtml));

            System.out.println(szHtml);

            for (String s : enErrors) {
                System.out.println(s);
            }
        }
        catch (Exception ex) {
            // 例外錯誤處理。
            enErrors.add(ex.getMessage());
        }
        finally {
            // 顯示錯誤訊息。
            if (enErrors.size() > 0)
                System.out.print(enErrors);
        }

        return szHtml.toString();
    }
}
