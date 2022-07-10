package ajbc.doodle.calendar.entities.webpush;

import java.net.http.HttpClient;

import javax.persistence.Entity;

import org.springframework.stereotype.Component;

import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;

import ajbc.doodle.calendar.ServerKeys;
import ajbc.doodle.calendar.services.CryptoService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class PushMessageConfig {

	private final ServerKeys serverKeys;

	private final CryptoService cryptoService;

	private final HttpClient httpClient;

	private final Algorithm jwtAlgorithm;

	private final ObjectMapper objectMapper;

	public PushMessageConfig(ServerKeys serverKeys, CryptoService cryptoService, ObjectMapper objectMapper) {
		this.serverKeys = serverKeys;
		this.cryptoService = cryptoService;
		this.httpClient = HttpClient.newHttpClient();
		this.objectMapper = objectMapper;

		this.jwtAlgorithm = Algorithm.ECDSA256(this.serverKeys.getPublicKey(), this.serverKeys.getPrivateKey());
	}
	
	
}
