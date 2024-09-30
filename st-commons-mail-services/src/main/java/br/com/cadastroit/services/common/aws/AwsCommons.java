package br.com.cadastroit.services.common.aws;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AwsCommons {

	private Properties properties = new Properties();
	private boolean enviroment	  = false;
	
	public static class Builder{
		private Properties properties;
		private boolean enviroment;
		private String FILE = "/opt/csfconfig/version.properties";
		
		public AwsCommons build() {
			AwsCommons awsCommons = new AwsCommons();
			awsCommons.setProperties(this.properties);
			awsCommons.setEnviroment(this.enviroment);
			return awsCommons;
		}

		public Builder withProperties() {
			String userS3  = System.getenv("USER_S3");
			this.enviroment= userS3 != null;
			if(userS3 == null && this.properties == null) {
				this.properties = new Properties();
				if(System.getProperty("os.name").toLowerCase().contains("windows"))FILE = "C:\\ComplianceFiscal\\csfconfig\\version.properties";
				try (FileInputStream fis = new FileInputStream(FILE)){
					this.properties.load(fis);
				}catch (IOException ex) {
					log.error("Error on Builder with properties = "+ex.getMessage());;
				}
			}
			return this;
		}
	}
	
	public boolean isEnviroment() {
		return enviroment;
	}

	public void setEnviroment(boolean enviroment) {
		this.enviroment = enviroment;
	}

	private void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getPasswordS3() {
		return this.isEnviroment() ? System.getenv("PASS_S3") : this.properties.getProperty("pass_s3");
	}
	
	public String getUserS3() {
		return this.isEnviroment() ? System.getenv("USER_S3") : this.properties.getProperty("user_s3");
	}
	
}
