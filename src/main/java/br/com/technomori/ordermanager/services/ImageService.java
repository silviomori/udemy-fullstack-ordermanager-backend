package br.com.technomori.ordermanager.services;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.technomori.ordermanager.services.exceptions.FileException;

@Service

public class ImageService {
	
	public BufferedImage getJpgImageFromFile( MultipartFile uploadedFile ) {
		String ext = FilenameUtils.getExtension( uploadedFile.getOriginalFilename() );
		
		if( !"png".equals(ext) && !"jpg".equals(ext) ) {
			throw new FileException("File format not supported: "+ext+". Supported file formats: PNG and JPG.");
		}
		
		try {
			BufferedImage buffImg = ImageIO.read( uploadedFile.getInputStream() );
			if( "png".equals(ext) ) {
				buffImg = pngToJpg(buffImg);
			}
			return buffImg;
		} catch (IOException e) {
			throw new FileException("Error while loading file.");
		}	
	}

	public BufferedImage pngToJpg(BufferedImage bufferedPng) {
		BufferedImage bufferedJpg = new BufferedImage(
				bufferedPng.getWidth(),
				bufferedPng.getHeight(),
				BufferedImage.TYPE_INT_RGB);

		bufferedJpg.createGraphics().drawImage(bufferedPng, 0, 0, Color.WHITE, null);

		return bufferedJpg;		
	}
	
	public InputStream getInputStrem( BufferedImage buffImg, String extension ) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write( buffImg, extension, baos );
			return new ByteArrayInputStream(baos.toByteArray());
		} catch( IOException e ) {
			throw new FileException("Error while loading file.");
		}
	}
	
	public BufferedImage cropSquare(BufferedImage srcImg) {
		int size = (srcImg.getHeight() <= srcImg.getWidth()) ? srcImg.getHeight() : srcImg.getWidth();
		
		return Scalr.crop(
				srcImg, 
				(srcImg.getWidth()/2) - (size/2),
				(srcImg.getHeight()/2) - (size/2), 
				size,
				size);
	}
	
	public BufferedImage resize( BufferedImage srcImg, int size ) {
		return Scalr.resize(srcImg, Scalr.Method.ULTRA_QUALITY, size);
	}

}
