package com.payment.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.payment.util.EncryptUtil;
import com.payment.util.PaymentDBDriver;

/**
 * Handles requests for the application home page.
 */
@Controller
public class MainController {
	
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	

	/**
	 * mainPage
	 * @param locale
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String mainPage(Locale locale, Model model) {
		logger.info("payment API Started at {}.", locale);
		
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);
		
		String formattedDate = dateFormat.format(date);

		//db connection
		PaymentDBDriver.main(null);
		
//		logger.info("tt::{}.", formattedDate);
//		
//		String t = "3029333320399282";
//		
//		String encT = "";
//		String decT = "";
//		try {
//			encT = enc.encrypt(t);
//			decT = enc.decrypt(encT);
//		} catch (NoSuchAlgorithmException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (GeneralSecurityException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		logger.info("enc str:::::{}", encT);
//		logger.info("dec str:::::{}", decT);
//		
//		


		model.addAttribute("serverTime", formattedDate );
		
		return "home";
	}

	

}
