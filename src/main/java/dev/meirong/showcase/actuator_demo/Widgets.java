package dev.meirong.showcase.actuator_demo;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.List;

@RestController
public class Widgets {

	private final JdbcClient jdbcClient;
	private final DataSource dataSource;

	Widgets(JdbcClient jdbcClient, DataSource dataSource) {
		this.jdbcClient = jdbcClient;
		this.dataSource = dataSource;
	}

	@GetMapping("/widgets")
	public List<String> list() {
		return jdbcClient.sql("select name from widget order by id")
				.query(String.class)
				.list();
	}

	/**
	 * Holds a pooled connection without doing any work, so hikaricp.connections.active
	 * and hikaricp.connections.pending are observable while requests are in flight.
	 */
	@GetMapping("/widgets/hold")
	public String hold(@RequestParam(defaultValue = "5") int seconds) throws SQLException {
		try (Connection connection = dataSource.getConnection()) {
			Thread.sleep(Duration.ofSeconds(seconds));
			return "held " + connection + " for " + seconds + "s";
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			return "interrupted while holding a connection";
		}
	}
}
