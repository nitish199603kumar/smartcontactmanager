package in.nitish.pojo;

import lombok.Data;

@Data
public class EmailOTP {

	private String recipient;
	private  String msgBody;
	private String subject;

}
