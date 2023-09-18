package in.nitish.invoker;

import org.assertj.core.api.UrlAssert;

import in.nitish.pojo.EmailOTP;

public interface NotificaionServiceInvoker {

	String invokeEmail(String url,EmailOTP emailOTP);
}
