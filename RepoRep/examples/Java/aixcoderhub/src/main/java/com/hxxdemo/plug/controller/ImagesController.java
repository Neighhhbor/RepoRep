package com.hxxdemo.plug.controller;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hxxdemo.plug.service.InvitationCodeService;

@Controller
public class ImagesController {
	@Autowired
	private InvitationCodeService invitationCodeService;
	private  String  srcImgPath = System.getProperty("user.dir")+"/../webapps/ROOT/static/images/inviteImg.jpg"; //源图片地址
	@RequestMapping("invitationCode.jpg")
	public void captcha(HttpServletResponse response,String uuid,HttpServletRequest request)throws IOException {
		if (uuid == null ) {
			this.blackWaterMark(srcImgPath, response);
		}
		//通过token获取邀请码
		String code =""; 
			try {
				code = invitationCodeService.getInvitationCodeByUserUuid(uuid);
			} catch (Exception e) {
				this.blackWaterMark(srcImgPath, response);
			}
		if (code == null ) {
			this.blackWaterMark(srcImgPath, response);
		}else {
			response.setHeader("Cache-Control", "no-store, no-cache");
			response.setContentType("image/jpeg");
			
			Font font = new Font("微软雅黑", 50, 40);                     //水印字体
			
			String waterMarkContent=code;  //邀请码
			Color color=new Color(255,255,255,255);                               //水印图片色彩以及透明度
			this.addWaterMark(srcImgPath, waterMarkContent, color,font, response);
		}
		
        
	}
	
	public void addWaterMark(String srcImgPath,  String waterMarkContent,Color markContentColor,Font font,HttpServletResponse response) {

        try {
            // 读取原图片信息
            File srcImgFile = new File(srcImgPath);//得到文件
            Image srcImg = ImageIO.read(srcImgFile);//文件转化为图片
            int srcImgWidth = srcImg.getWidth(null);//获取图片的宽
            int srcImgHeight = srcImg.getHeight(null);//获取图片的高
            // 加水印
            BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = bufImg.createGraphics();
            g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
            g.setColor(markContentColor); //根据图片的背景设置水印颜色
            g.setFont(font);              //设置字体
//            double rate=3.58;
//            System.out.println(rate*g.getFontMetrics().stringWidth(waterMarkContent)+"====");
//            int x=(int)(srcImgWidth/2-rate*g.getFontMetrics().stringWidth(waterMarkContent)/2)+31;
//            MyDrawString(waterMarkContent, x, 425, rate, g);
//            int x = srcImgWidth - 2*getWatermarkLength(waterMarkContent, g);  
//            int y = srcImgHeight - 2*getWatermarkLength(waterMarkContent, g); 
            //设置水印的坐标
            int x1 = 119;
            int x2 = 198;
            int x3 = 282;
            int x4 = 363;
            int y = 425;
            
            g.drawString(waterMarkContent.substring(0, 1), x1, y);  //画出水印
            g.drawString(waterMarkContent.substring(1, 2), x2, y);  //画出水印
            g.drawString(waterMarkContent.substring(2, 3), x3, y);  //画出水印
            g.drawString(waterMarkContent.substring(3, 4), x4, y);  //画出水印
            
            g.dispose();  
            ServletOutputStream out = response.getOutputStream();
            ImageIO.write(bufImg, "jpg", out);
//            // 输出图片  
//            FileOutputStream outImgStream = new FileOutputStream(tarImgPath);  
//            ImageIO.write(bufImg, "jpg", outImgStream);
//            System.out.println("添加水印完成");  
//            outImgStream.flush();  
//            outImgStream.close();  

        } catch (Exception e) {
            // TODO: handle exception
        }
    }
	public void blackWaterMark(String srcImgPath,HttpServletResponse response) {
		
		try {
			// 读取原图片信息
			File srcImgFile = new File(srcImgPath);//得到文件
			Image srcImg = ImageIO.read(srcImgFile);//文件转化为图片
			int srcImgWidth = srcImg.getWidth(null);//获取图片的宽
			int srcImgHeight = srcImg.getHeight(null);//获取图片的高
			// 加水印
			BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bufImg.createGraphics();
            g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
			ServletOutputStream out = response.getOutputStream();
			ImageIO.write(bufImg, "jpg", out);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public int getWatermarkLength(String waterMarkContent, Graphics2D g) {  
        return g.getFontMetrics(g.getFont()).charsWidth(waterMarkContent.toCharArray(), 0, waterMarkContent.length());  
    } 
	public static void MyDrawString(String str,int x,int y,double rate,Graphics2D g ){
		String tempStr=new String();
		int orgStringWight=g.getFontMetrics().stringWidth(str);
		int orgStringLength=str.length();
		int tempx=x;
		int tempy=y;
		while(str.length()>0)
		{
			tempStr=str.substring(0, 1);
			str=str.substring(1, str.length());
			g.drawString(tempStr, tempx, tempy);
			tempx=(int)(tempx+(double)orgStringWight/(double)orgStringLength*rate);
		}
	}

}
