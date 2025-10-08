package io.renren.modules.utils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Splitter;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
@Slf4j
public class ReCapthUtils {

	 public static String reCapth() throws Exception {
    	 String respData = null;
 		try {
 			String url = "https://2captcha.com/in.php?key=82d7c3c7dfca21006510df3c6993b807&method=userrecaptcha&googlekey=6Lcq80spAAAAADGCu_fvSx3EG46UubsLeaXczBat&pageurl=https://qna3.ai/vote";
 			OkHttpClient client =  new OkHttpClient.Builder()
        			 .callTimeout(20L, TimeUnit.SECONDS)
        			 .connectTimeout(20L, TimeUnit.SECONDS)
                    .build();
 			Request request = new Request.Builder().url(url).get().build();
 			Call call = client.newCall(request);
 			Response resp = call.execute();
 			 respData = resp.body().string();
 			String id =  Splitter.on("|").splitToList(respData).get(1);
 			log.info("In 2captcha - > {} -> {} ",respData,id);
 			int i = 0;
 			while(i<6) {
 				respData = resp(id);
 				if(!respData.equals("CAPCHA_NOT_READY")) {
 					log.info("打码验证完成..");
 					respData =  Splitter.on("|").splitToList(respData).get(1);
 					break;
 				 }
 				log.info("5s 后重试..");
 				TimeUnit.SECONDS.sleep(5);
 				i++;
 			}
 			 
 		} catch (IOException e) {
 			e.printStackTrace();
 		}
 		return respData;
 		
 		//03AFcWeA59p5_tDXO2H9nJKCMPj9n9VPkBBsmWXnnmvWZXqEl5Ir12Fr58MTsymMgjy6rRMUx-d28O0cHXz5LIBomPvNgQAQMvE-RxVBYhwNGUujLK1xMc3sUMiDhePownTOD6cel8SsUR2XHvPS9cRqtHpH-5lPOBJ5_34irBSH1M-j7SFFX4UfrHfEFAghd7n7tHoeklNqYzsJckWz0IhBklhXkIzLANwHYv2CL5FU25VlElF9MD27Fo8ZhBqRW4sSmjjctSAdDXo4QndvcPtKJvEYKqBuoJTEPwvJKrotZw1zGU9NHe8l9Q4uv4Fuf2DEigSXjq5VJ7bTY-RMj0cxLS8tIh5xqrDNpFoIkY5DRBK-lJKPYUeEBsvbCJ6JYw_gXDSyCneAHe5Zw79O4cNMz-CmZvR3QJMXIl5Be3phvq7h5Lr_LNa0Q0UsqMQIh2IayoweLCTlPz2XmwVXVT10UowzytlBZdXDHuYFJZqLxOFQ6tR-Qff_6x2N7QKFWromP1RYEq6DYIIE_tcLytXotqFMKrrx_-21OVzm44cIgFIU8Gy2Lnh0mz1IH2B8hAeZQit2l3TeV_b1nf1rT6k2AYjAVmE5CKZAm_VXb6U3mFOPgZKvF1Am2dgfgKKyCXtRcYhIgOARe6sdTCLAQmBjesvYgvCzXSOyIBvfQedMZd4cl_m_M6121o2hSCu0d4Sq9lTz9dh4pJB6PsB3REybxKUPZZRhjRjbzNlOLln3P0Vu44dHrybI2kR6_nx_J_MLB-n9s4C2lDzGgUZNoPbOjVtZ5ld6Dk-ELSKFa4Ekp-C7TWJwoVpMff-fjTIlfE4SOxQu3UL31FUqLdkOR4riA-6btpiD9_Dp18xqw1hHslEvNaEnUSK3x4lKgUau0mj_tTj4H8mc9yqfeSsKmoTT96o0J5Zh_rP1vei35ruV8s_-t_j7xnBCtwQ6QGSpLAPaAXYcWbWMnsy26CAVGVY0VOwe3X8wlw3Dl4KBXbUevaxzo2jtH61g2p5xhChot1NoAQdgWN4UA-3KxQ0YzY3cQ5GjhwFVjelX1tJOIFFv2FigZAfjT_e1NUrOqvk5X8WNfRb4-o9QW4Tn4MNwWxrWwnL15SC8mQH2vWgdtl3un3DAO2kx7hYyQv9GZlou7dayCGMUc2EwrsWfhDfp8wxVPh1OgchOjsY52h9NfCJKYQ-gpBWAmIkiHZuuCwhFsIInGC5jVey7Xw16AJcJ-3sew-fQKT17sv_2NvvltNsNDzAVtT06sMklaJpXdnkPVUugIlkLPhvIYWiMG_PshUfk9fLYfKawLEYiazcdKLd4SgFXNZjq69m5YZA8yqPgTBP9UYL4gkHTo9_tflIvBR2Rrn33pNvSpTKLVoNjC9xcxgLJJllve1Xd_jgd7vFc_cbR1kVXwEXtpCll8N9isViuJ5AeIB1Hdh29p2d4w1HcFDe10kavW_fnJco4zzdoczT-HipfC61K6UVyc1cjxBNftNJgUWtFbRi4Drxx--0mpElHr5oHPYqKV4WjL_8zo7A_aOeWj-RU-QG5wT9sbhola4yMo5D1-iQizyKtAcgBifS2e85O2SKqYwhug0jeCA3LDguosjWT8_YzM8z-TkP0sjyrssessanKnyIPKrpAMRs1hjB9_bw_YeE8njWXYGQBqrUkYHcSeqQHMoiLKdaWqbCuUXzhtPNi-NgPco2Eo3JE4usSLQs3mcjqD8ZkCF0Z1WHllspCyruNOknoE7YKWFOy_R5rokaS_xx1kbybObE6ZWIX2X7-pBvd_7JgHYketI4aQb-x3j9tNhu_d_A86DoCVIeOQ8-YuIQfYFDlFET3tmOY_sl2MHPXgkgUSUHSVZqnXkl5JbIg6PsWhFxB8RboTi8xESREPM3XEte8Wej8kuJHAIpBebVRrHiDUJco0KXaRAfF0UJD7ByI_BdypHeHY6-en2KQ
 		//03AFcWeA4-551nlA6f4msAZvfBSaa6xtLwQwIJAfNCVlqaOu1So9YsMNmoInJZYr6q6112zQrs6_rAtWkpbWz7peIdPTcsWFycV9Dm-Z2lK79q-WAZQyU07MHcTpDyQWjs5CW2iCrKA9UlbmQDDXa2cMwML5WYxXT1eRpnERAZfLgY3t20iegF6DFDQ1sNVrJslpWuF2RbW8hy4EW2Dh9A0l1ySjQJfb9hvbaRXmjkYjU0ycLcJyzETLTquBT_3MjsAFEGel1EhB6JdmKfGoyirhyycE2qIACZ3O4OsBA-x3QLV-6_QsmmO9Dh27YQlDIomMlE7yp6f-zM_JHDQW51BvL6cVzyb2Z9G_JFrv7crFI_G0SstpX1bCoPZ-4cosz92zWtCY3DyKNzZQCG0zL4iPI2-3XWSS9cNVSDUkZ9Hd5vvjfqcv0cunEjfRUf7rKDVTD0Nxj0DFnLG7PydVeDQhuGr0M9_WnVomG_-UgRGAoxE1vexp-MwENHuSWt4jX4TEoICp2rKNNs7zY_UI56dgY-1ZqryJ6GaycySvyrdzY3Due4q0UBUm1-wnU5HRCqsJDF-jBcO-BmChx-xqTm2ymMTWeY-ayUPwVJ2QfxycjTb9RKd0h8eoy_p7KChzbRsd2bKVNOfGvFhoV6EzcXicRnGQqgmW3l3Imcx-yT_j-N2ioZq_jIdNSvIMG5xZMpfA1KlD-UpUxRlBr9tnxWIBdt6euVycDxQzuk0FZkaD0vPzh5NYeJ95zZlPu1BqtEO4uuXtB2xCTDJ51piDN_h2WteGNdkCZaOx81I6fDQjOCTLn5hIbk_3yUS4b7AaAiTIbk2ip4zGnOYsjtU-9Z0CufoXLMaz-G9gH2hyeTlAxoJN6aCrXelqnY7zV2GUerfqSZo1VojYYZCGG49EG0JyjMZm4UBBboyMAWGVFZTnFMIQlaipJ08imDe0wEBhuCfuAND_BhviVuHFSpu7JbpV_L9IOwN0xkzQoVM-VwTlJNwoLbZAEGl-avrNBVhlzdK_sNNU9kTAeS5idgq-I83qgKcBGONKck21rGialnYMnFylclM935XlEtngbKO6qXDLqq7PtCSpmmifSiXOYWi4o4Lt3qUYwVxI79YHWHYmqUaxkwX-riAoXE5MiYVS-Q-A-ZjgiUhxRZTM_6J7it1EcYiG2Qn9yYE0BKd95CKJzaaFuQtd7i1c9bM9IVHBccZKTHAP8EZMTaHcdvVTbhJonXTGnnQLkaINrFd2Iczosg24OY3ZPHHDX_859q8bdBXepV34cYAdvdaBzn5Q1Of-TgX_x3issSyIrh3IrfrUBravpQ_Mh7jM6n8YtxCR0TkrmEmfAEyNFfJpeUUUZENJfhbb_QuzerSdMh90Px-cVfowC3C9n3Ww6NZoyCfrgyX2EWqrjKPFtV1I4kDFPDTHw0--eo_3NecC4oI4gSWcNRYwpdAWwppcAUcsOnPkOvURmdcOKE9SyLWHc1NgZclLMYT46vRlzxjmEvlQDlKx6GCbkL35LAmMIcStS_LxnFOoe-C14u6u4iFU1sk-P5YOhlNUCKkiK_WvsOPWR9_QNm-0PAZQwcD6zADu0d4rMHDX5K-s2U6USwe2ZSWwUkVcJTqPqBF2Sw_du6BLNtKPCZISRFijYAu-EGY6mmlK98Sgh6XU3NsvEDErxRyE28s0czbutDiVElm_jNJyuMlmI9WtRVkAO35YMxBC120pNCAivM39OZA2jlrPMiCXleUTo2nc54EnCMgkbIJTN8Ngt7twozSKZvf7q8gXQJi0uqWdbUkUIUz51wmoZXveAlVAMUFhbuUSs9d_dAljCsq_SEpMDpS4z3EHQki9m8HTYCBtx375b7usDcW40v1ysZs7SmQWZB850New
 	}
 	
 	public static String resp(String id) throws Exception {
 		OkHttpClient client =  new OkHttpClient.Builder()
       			 .callTimeout(20L, TimeUnit.SECONDS)
       			 .connectTimeout(20L, TimeUnit.SECONDS)
                   .build();
 		String respUrl = "https://2captcha.com/res.php?key=82d7c3c7dfca21006510df3c6993b807&action=get&id="+id;
 		Request  request = new Request.Builder().url(respUrl).get().build();
 		Call call = client.newCall(request);
 		Response resp = call.execute();
 		String respData = resp.body().string();
 		log.info("Resp 2captcha - > {} ",respData);
 		 return respData;
 	}
     
}
