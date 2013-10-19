package com.nabsys.common.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.SocketException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import com.nabsys.common.label.NLabel;


public class FtpClient {
	
	private FTPClient ftpClient = null;
	public static final int BINARY = FTP.BINARY_FILE_TYPE;
	public static final int ASCII = FTP.ASCII_FILE_TYPE;
	
	public FtpClient(String address, int port, String username, String password) throws SocketException, IOException, NoSuchAlgorithmException
	{
		ftpClient = new FTPClient();
		InetAddress host = InetAddress.getByName(address);
		
		ftpClient.setControlEncoding("euc-kr");
		ftpClient.connect(host, port);
		
		int reply = ftpClient.getReplyCode();

		if(!FTPReply.isPositiveCompletion(reply))
		{
			ftpClient.disconnect();
			throw new IOException(NLabel.get(0x0003) + " : " + host.getHostAddress());
		}
		
		if(!ftpClient.login(username, password))
		{
			ftpClient.disconnect();
			throw new IOException(NLabel.get(0x0063) + " : " + host.getHostAddress());
		}
	}
	
	public void download(String remoteFileName, String localFileName) throws IOException
	{
		File localFile = new File(localFileName);
		OutputStream outputStream = new FileOutputStream(localFile);
		
		boolean result = false;
		try {
			result = ftpClient.retrieveFile(remoteFileName, outputStream);
			
			if(!result) throw new IOException(NLabel.get(0x0089));
		} finally {
			outputStream.close();
		}
	}
	
	public void uploadOverwrite(String remoteFileName, String localFileName) throws IOException
	{
		File localFile = new File(localFileName);
		InputStream inputStream = new FileInputStream(localFile);
		
		boolean result = false;
		try {
			result = ftpClient.storeFile(remoteFileName, inputStream);
			
			if(!result) throw new IOException(NLabel.get(0x008A));
		} finally {
			inputStream.close();
		}
	}
	
	public void upload(String remoteFileName, String localFileName) throws IOException
	{
		File localFile = new File(localFileName);
		InputStream inputStream = new FileInputStream(localFile);
		
		boolean result = false;
		try {
			result = ftpClient.appendFile(remoteFileName, inputStream);
			
			if(!result) throw new IOException(NLabel.get(0x008A));
		} finally {
			inputStream.close();
		}
	}
	
	public void rename(String oldFileName, String newFileName) throws IOException
	{
		boolean result = false;
		try {
			result = ftpClient.rename(oldFileName, newFileName);
			
			if(!result) throw new IOException(NLabel.get(0x008B));
		} finally {
		}
	}
	
	public void delete(String fileName) throws IOException
	{
		boolean result = false;
		try {
			result = ftpClient.deleteFile(fileName);
			
			if(!result) throw new IOException(NLabel.get(0x008C));
		} finally {
		}
	}
	
	public void makeDirectory(String path) throws IOException
	{
		boolean result = false;
		try {
			result = ftpClient.makeDirectory(path);
			
			if(!result) throw new IOException(NLabel.get(0x008D));
		} finally {
		}
	}
	
	public void setFileType(int fileType) throws IOException
	{
		ftpClient.setFileType(fileType);
	}
	
	public void close() throws IOException
	{
		if(ftpClient != null)
		{
			if(ftpClient.isConnected())
			{
				try{
					ftpClient.logout();
				}catch(Exception e){
				}
				
				ftpClient.disconnect();
			}
		}
	}
}
