package ajbc.doodle.calendar.services.threads;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.concurrent.Callable;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.JsonProcessingException;

import ajbc.doodle.calendar.Application;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.webpush.PushMessage;
import ajbc.doodle.calendar.entities.webpush.PushMessageConfig;
import ajbc.doodle.calendar.entities.webpush.Subscription;
import ajbc.doodle.calendar.services.NotificationService;

public class NotificationTask implements Callable<Notification> {
	
//	@Autowired
//	private NotificationService notificationService;
	
	private Notification notification;
	private Subscription subscription;
	private PushMessageConfig config;
	
	public NotificationTask(Notification notification, Subscription subscription, PushMessageConfig config){
		this.notification = notification;
		this.subscription = subscription;
		this.config = config;
	}
	
	@Override
	public Notification call() throws Exception {
		
		System.out.println("entered!");
		
		PushMessage msg = new PushMessage("message: ", notification.toString());
		
		sendPushMessageToUser(subscription, msg);

		return notification;
	}

	
	public void sendPushMessageToUser(Subscription subscription, PushMessage message) throws JsonProcessingException {
		try {

			byte[] result = config.getCryptoService().encrypt(config.getObjectMapper().writeValueAsString(message),
					subscription.getKeys().getP256dh(), subscription.getKeys().getAuth(), 0);

			boolean remove = sendPushMessage(subscription, result);

		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException
				| IllegalStateException | InvalidKeySpecException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {
			Application.logger.error("send encrypted message", e);
		}

	}
	
	
	/**
	 * @return true if the subscription is no longer valid and can be removed, false
	 *         if everything is okay
	 */
	private boolean sendPushMessage(Subscription subscription, byte[] body) {

		String origin = null;
		try {
			URL url = new URL(subscription.getEndpoint());
			origin = url.getProtocol() + "://" + url.getHost();
		} catch (MalformedURLException e) {
			Application.logger.error("create origin", e);
			return true;
		}

		Date today = new Date();
		Date expires = new Date(today.getTime() + 12 * 60 * 60 * 1000);

		// mailto is the address Push services will reach out if there is a severe
		// problem with the push message deliveries.
		String token = JWT.create().withAudience(origin).withExpiresAt(expires)
				.withSubject("mailto:example@example.com").sign(config.getJwtAlgorithm());

		URI endpointURI = URI.create(subscription.getEndpoint());

		Builder httpRequestBuilder = HttpRequest.newBuilder();
		if (body != null) {
			 
			httpRequestBuilder.POST(BodyPublishers.ofByteArray(body))
			.header("Content-Type", "application/octet-stream") // describes the content of the body. an encrypted stream of bytes
					.header("Content-Encoding", "aes128gcm"); // describes how the encrypted payload is formatted.
		} else {
			httpRequestBuilder.POST(BodyPublishers.ofString(""));
			// httpRequestBuilder.header("Content-Length", "0");
		}

		HttpRequest request = httpRequestBuilder.uri(endpointURI).header("TTL", "180")
				//Payload encryption, message has less then 4096 bytes.
				.header("Authorization", "vapid t=" + token + ", k=" + config.getServerKeys().getPublicKeyBase64()).build(); 
		try {
			HttpResponse<Void> response = config.getHttpClient().send(request, BodyHandlers.discarding());

			switch (response.statusCode()) {
			case 201:
				Application.logger.info("Push message successfully sent: {}", subscription.getEndpoint());
				break;
			case 404:
			case 410:
				Application.logger.warn("Subscription not found or gone: {}", subscription.getEndpoint());
				// remove subscription from our collection of subscriptions
				return true;
			case 429:
				Application.logger.error("Too many requests: {}", request);
				break;
			case 400:
				Application.logger.error("Invalid request: {}", request);
				break;
			case 413:
				Application.logger.error("Payload size too large: {}", request);
				break;
			default:
				Application.logger.error("Unhandled status code: {} / {}", response.statusCode(), request);
			}
		} catch (IOException | InterruptedException e) {
			Application.logger.error("send push message", e);
			// --- here roll back!!! After the application has sent the request to the push
			// service, it needs to check the response's status code.
		}

		return false;
	}
	
	
}
