package ajbc.doodle.calendar.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpRequest.Builder;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ajbc.doodle.calendar.Application;
import ajbc.doodle.calendar.ServerKeys;
import ajbc.doodle.calendar.daos.DaoException;
import ajbc.doodle.calendar.daos.interfaces.NotificationDao;
import ajbc.doodle.calendar.daos.interfaces.UserDao;
import ajbc.doodle.calendar.entities.ErrorMessage;
import ajbc.doodle.calendar.entities.Event;
import ajbc.doodle.calendar.entities.Notification;
import ajbc.doodle.calendar.entities.webpush.PushMessage;
import ajbc.doodle.calendar.entities.webpush.Subscription;
import ajbc.doodle.calendar.entities.webpush.SubscriptionEndpoint;

@Service
public class NotificationService {

	@Autowired
	@Qualifier("htNotificationDao")
	NotificationDao dao;

	@Autowired
	@Qualifier("htUserDao")
	UserDao userDao;

	private final ServerKeys serverKeys;

	private final CryptoService cryptoService;

	private final HttpClient httpClient;

	private final Algorithm jwtAlgorithm;

	private final ObjectMapper objectMapper;

	public NotificationService(ServerKeys serverKeys, CryptoService cryptoService, ObjectMapper objectMapper) {
		this.serverKeys = serverKeys;
		this.cryptoService = cryptoService;
		this.httpClient = HttpClient.newHttpClient();
		this.objectMapper = objectMapper;

		this.jwtAlgorithm = Algorithm.ECDSA256(this.serverKeys.getPublicKey(), this.serverKeys.getPrivateKey());
	}

	public byte[] publicSigningKey() {
		return this.serverKeys.getPublicKeyUncompressed();
	}

	public boolean isSubscribed(SubscriptionEndpoint subscription) throws DaoException {
		return userDao.checkEndPointRegistration(subscription.getEndpoint());
	}

	
	public void sendPushMessageToUser(Integer userId, Object message) throws JsonProcessingException {
		try {
			Subscription subscription = userDao.getSubscriptionByUserId(userId);

			byte[] result = this.cryptoService.encrypt(this.objectMapper.writeValueAsString(message),
					subscription.getKeys().getP256dh(), subscription.getKeys().getAuth(), 0);

			boolean remove = sendPushMessage(subscription, result);

		} catch (InvalidKeyException | NoSuchAlgorithmException | InvalidAlgorithmParameterException
				| IllegalStateException | InvalidKeySpecException | NoSuchPaddingException | IllegalBlockSizeException
				| BadPaddingException e) {
			Application.logger.error("send encrypted message", e);
		} catch (DaoException e) {
			ErrorMessage errMsg = new ErrorMessage();
			errMsg.setData(e.getMessage());
			errMsg.setMessage("Failed to get subscription from the DB.");
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
				.withSubject("mailto:example@example.com").sign(this.jwtAlgorithm);

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
				.header("Authorization", "vapid t=" + token + ", k=" + this.serverKeys.getPublicKeyBase64()).build(); 
		try {
			HttpResponse<Void> response = this.httpClient.send(request, BodyHandlers.discarding());

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

	public void addNotificationToDB(Notification notification, Event event) throws DaoException {
		notification.setEvent(event);
		dao.addNotification(notification);
	}

	public Notification createDefaultNotification(Event event) throws DaoException {

		Notification not = new Notification(event.getEventOwnerId(), event.getEventId(), event.getTitle(), 0,
				ChronoUnit.SECONDS, 0);

		addNotificationToDB(not, event);
		return not;
	}

	public List<Notification> getAllNotifications() throws DaoException {
		return dao.getAllNotifications();
	}

	public void deleteAllNotifications() throws DaoException {
		dao.deleteAllNotifications();
	}

	public Notification getNotificationById(Integer id) throws DaoException {
		return dao.getNotification(id);
	}

	public void updateNotification(Notification notification) throws DaoException {
		dao.updateNotification(notification);

	}

	public void softDelete(Notification notification) throws DaoException {
		notification.setDiscontinued(1);
		dao.updateNotification(notification);
	}

	public void hardDelete(Notification notification) throws DaoException {
		dao.deleteNotification(notification);

	}

//	public void addListNotificationsToDB(List<Notification> notifications) {
//		// TODO Auto-generated method stub
//	}

}
