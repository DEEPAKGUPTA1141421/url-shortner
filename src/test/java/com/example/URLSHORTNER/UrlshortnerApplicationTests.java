package com.example.URLSHORTNER;

import com.example.URLSHORTNER.repository.UrlMappingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(properties = {
		"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration"
})
public class UrlshortnerApplicationTests {

	@MockBean
	private UrlMappingRepository urlMappingRepository;

	@Test
	public void contextLoads() {
	}
}
